package com.ejemplo.fetalcare

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.ejemplo.fetalcare.databinding.ActivityMainBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var latidos: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtenerinstancias
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        //Boton para conectar el dispositivo
        binding.btnConnect.setOnClickListener {
            ConnectToDevice(this).execute()
            if(connectSuccess){
                Toast.makeText(this, "Conexión exitosa", Toast.LENGTH_LONG).show()
            }
        }

        latidos = " "
        //Boton para iniciar
        binding.btnPlay.setOnClickListener {
            sendCommand("1")
            Toast.makeText(this, play, Toast.LENGTH_LONG).show()
            try {
                //Recibir informacion
                val inputStream = btSocket!!.inputStream
                val handlerThread = Thread(Runnable {
                    while (true) {
                        val bytes = ByteArray(1024)
                        val bytesRead = inputStream.read(bytes)
                        if (bytesRead > 0) {
                            val message = String(bytes, 0, bytesRead)
                            Log.d("BluetoothReceiver", "Received message: $message")
                            runOnUiThread {
                                /*binding.result.text = message*/
                                latidos = message
                                if(latidos != "0" && latidos > "100" && latidos < "170"){
                                    sendToDatabase(latidos)
                                    binding.result.text = latidos
                                }
                            }
                        }
                    }
                })
                handlerThread.start()
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show()
                Log.i("MainActivity", "ERROR DE CONEXION")
            }


        } //Envia una senal para que se inicie el dispositivo

        //Boton para pausar
        binding.btnPause.setOnClickListener {
            sendCommand("0")
            Toast.makeText(this, pause, Toast.LENGTH_LONG).show()
        }

        //Boton para mostrar informacion del dislcaimer
        binding.btnInfo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_information, null)
            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<ImageButton>(R.id.close).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        //Barra de navegacion en la App
        binding.bottomNavigationView.setOnItemReselectedListener { item ->
            val historyIntent = Intent(this, HistoryActivity::class.java)
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            val homeIntent = Intent(this, MainActivity::class.java)
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(homeIntent)
                    finish()
                }

                R.id.bottom_history -> {
                    startActivity(historyIntent)
                    finish()
                }

                R.id.bottom_settings -> {
                    startActivity(settingsIntent)
                    finish()
                }
            }
        }
    }

    //Funcion para enviar los valores a la base de datos
    private fun sendToDatabase(res: String){

        val user = firebaseAuth.currentUser
        var fechaHora = Date()
        var formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        var fechaActual = formatoFecha.format(fechaHora)
        var horaActual = formatoHora.format(fechaHora)

        var valoresList = Results(res, fechaActual, horaActual)

        //Envio a la base de datos los resultados
        var newRef = reference.child(user!!.uid).child("Resultados").push()
        var key: String = newRef.key.toString()
        reference.child(user!!.uid).child("Resultados").child(key).setValue(valoresList)

    }
    @SuppressLint("StaticFieldLeak")
    //Clase que permite conectarse al dispositivo
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog.show(context, "Conectando...", "Por favor espere")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (btSocket == null || !isConnected) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter()
                   /* val pairedDevices: Set<BluetoothDevice>? = btAdapter.bondedDevices
                    if (pairedDevices != null) {
                        for (device in pairedDevices) {
                            if (device.name == "Fetal Care") {
                                // El nombre del dispositivo es el deseado, obtén la dirección MAC
                                address = device.address
                                btSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                                btSocket!!.connect()
                                connectSuccess = true
                                Log.i("MainActivity", "Conexión exitosa")
                                break
                            }
                        }
                    }*/
                    val device: BluetoothDevice = btAdapter.getRemoteDevice(address)
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    btSocket!!.connect()
                    connectSuccess = true
                    Log.i("MainActivity", "Conexión exitosa")
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
                Log.i("MainActivity", "Error de conexión")
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("MainActivity", "No se pudo conectar")
            } else {
                isConnected = true
            }
            progress.dismiss()
        }
    }

    private fun sendCommand(input: String) {
        if (btSocket != null) {
            try {
                btSocket!!.outputStream.write(input.toByteArray())
                Log.i("MainActivity", "$input")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val REQUEST_BLUETOOTH_PERMISSION = 1
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var address = "C4:DD:57:EB:26:AA"
        private lateinit var btAdapter: BluetoothAdapter
        private lateinit var progress: ProgressDialog
        private var btSocket: BluetoothSocket? = null
        private var isConnected: Boolean = false
        var connectSuccess: Boolean = false
        private var play = "Iniciando"
        private var pause = "Pausa"

    }
}