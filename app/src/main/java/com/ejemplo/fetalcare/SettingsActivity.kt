package com.ejemplo.fetalcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ejemplo.fetalcare.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtener instancias
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        //Nombre del usuario y correo
        val user = firebaseAuth.currentUser
        val uid = user?.uid
        nameUser(reference, uid)

        //Boton para acceder al cambio de contraseña
        binding.password.setOnClickListener {
            val passwordIntent = Intent(this, PasswordActivity::class.java)
            startActivity(passwordIntent)

        }

        //Boton para acceder al cambio de email
        binding.email.setOnClickListener {
            val emailIntent = Intent(this, EmailActivity::class.java)
            startActivity(emailIntent)

        }

        //Boton para el cierre de sesion
        binding.logout.setOnClickListener {
            firebaseAuth = Firebase.auth
            val currentUser = firebaseAuth.currentUser
            logOut(firebaseAuth, currentUser)
        }

        //Boton para acceder a la ayuda
        binding.ayuda.setOnClickListener {
            val ayudaIntent = Intent(this, HelpActivity::class.java)
            startActivity(ayudaIntent)

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

    //Funcion para obtener nombre y correo  del usuario
    private fun nameUser(reference: DatabaseReference, uid: String?){
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userSnapshot = snapshot.child(uid!!)
                val helperClass = userSnapshot.getValue(HelperClass::class.java)
                if (helperClass != null) {
                    binding.titleName.text = helperClass.name
                    binding.titleEmail.text = helperClass.email
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener el nombre del usuario: ${error.message}")
            }
        })
    }

    //Funcion para cerrar sesion
    private fun logOut(firebase: FirebaseAuth, currentUser: FirebaseUser?){
        if (currentUser != null) {
            firebaseAuth.signOut()
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
            finish()
        }
    }
}