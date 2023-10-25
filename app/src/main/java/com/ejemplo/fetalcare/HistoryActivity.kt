package com.ejemplo.fetalcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.fetalcare.adapter.ResultsAdapter
import com.ejemplo.fetalcare.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityHistoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    val listaMutable = mutableListOf<List<String>>() // Lista de listas de valores
    var lista: List<Results> = mutableListOf() // Lista de listas de valores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        var firebaseAuth = FirebaseAuth.getInstance()
        var database = FirebaseDatabase.getInstance()
        var reference = database.getReference("users")
        val user = firebaseAuth.currentUser
        var resultadosRef = reference.child(user!!.uid).child("Resultados")
        val listaResults = mutableListOf<Results>()
        var listaDataBase = mutableListOf<Results>()

        resultadosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    // Para cada nodo bajo "Resultados", obtener los valores
                    val valor = dataSnapshot.child("valor").getValue(String::class.java)
                    val fecha = dataSnapshot.child("fecha").getValue(String::class.java)
                    val hora = dataSnapshot.child("hora").getValue(String::class.java)


                    if (valor != null && fecha != null && hora != null) {

                        val valores = mutableListOf(valor, fecha, hora)
                        listaMutable.add(valores)
                    }

                }

                // Iterar a través de listaMutable y crear objetos Results
                for (valores in listaMutable) {
                    if (valores.size == 3) {
                        val valor = valores[0]
                        val fecha = valores[1]
                        val hora = valores[2]

                        // Crear un objeto Results y agregarlo a la lista
                        val results = Results(valor, fecha, hora)
                        listaResults.add(results)
                    }
                }

                listaDataBase = listaResults
                binding.recyclerview.layoutManager = LinearLayoutManager(applicationContext)
                binding.recyclerview.adapter =
                    ResultsAdapter(listaDataBase) { resultado -> onItemSelected(resultado) }
                Log.e("Firebase", "listaDataBase: $listaDataBase")
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores si es necesario
                Log.e("Firebase", "Error al obtener los resultados: ${error.message}")
            }
        })

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

    fun onItemSelected(resultado: Results) {
        Toast.makeText(this, "Funciona", Toast.LENGTH_SHORT).show()
    }
}