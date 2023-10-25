package com.ejemplo.fetalcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import com.ejemplo.fetalcare.databinding.ActivityEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmailActivity : AppCompatActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivityEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtener instancias
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        //Boton para ir hacia atras
        binding.backBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
            finish()
        }

        //Mostrar correo actual
        val user = firebaseAuth.currentUser
        val uid = user?.uid
        if (user != null) {
            binding.textEmailActual.text = user.email
        }

        //Boton para cambiar correo
        binding.changeEmailButton.setOnClickListener {
            val newEmail = binding.edittxtNewEmail
            val confirmNewEmail = binding.edittxtConfirmNewEmail
            changeEmail(newEmail, confirmNewEmail, uid, user)
        }
    }

    //Funcion para el cambio de correo
    private fun changeEmail(newEmail: EditText, confirmNewEmail: EditText,  uid: String?, user: FirebaseUser?){
        if (newEmail.text.toString().isNotEmpty() && confirmNewEmail.text.toString().isNotEmpty()) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(confirmNewEmail.text.toString()).matches()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail.text.toString()).matches()) {
                if (newEmail == confirmNewEmail) {
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            user?.updateEmail(confirmNewEmail.text.toString())
                            reference.child(uid!!).child("email").setValue(confirmNewEmail)
                            ui()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(
                                "Firebase",
                                "Error al cambiar el correo electrónico: ${error.message}"
                            )
                        }
                    })
                }else{
                    val context = applicationContext
                    Toast.makeText(context, "El correo no coincide", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Los espacios no pueden estar vacios", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //Funcion para modificar la interfaz grafica
    private fun ui(){
        binding.layoutSuccess.visibility = VISIBLE
        binding.layoutNewEmail.visibility = INVISIBLE
        binding.textEmailActual.visibility = INVISIBLE
        binding.textInfoEmail.visibility = INVISIBLE
    }
}

