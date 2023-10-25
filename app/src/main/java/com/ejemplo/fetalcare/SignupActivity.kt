package com.ejemplo.fetalcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.ejemplo.fetalcare.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtener instancias
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        //Boton de registro
        binding.signupButton.setOnClickListener {
            val name = binding.name
            val email = binding.signupEmail
            val password = binding.signupPassword
            val confirm = binding.signupConfirm

            signUp(email, password, confirm, name)
        }

        //Redireccion hacia el login
        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    //Funcion para registro
    private fun signUp(email: EditText, password: EditText, confirm: EditText, name: EditText) {
        if (email.text.toString().isNotEmpty() && password.text.toString()
                .isNotEmpty() && confirm.text.toString().isNotEmpty() && name.text.toString()
                .isNotEmpty()
        ) {
            if (password.text.toString() == confirm.text.toString()) {
                val regex =
                    "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$".toRegex()
                if (password.text.toString().length >= 8 && password.text.toString()
                        .matches(regex)
                ) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                            .matches()
                    ) {
                        firebaseAuth.createUserWithEmailAndPassword(
                            email.text.toString(),
                            password.text.toString()
                        )
                            .addOnSuccessListener { authResult ->

                                val user = authResult.user
                                val helperClass = HelperClass(
                                    name.text.toString(),
                                    email.text.toString(),
                                    password.text.toString()
                                )
                                reference.child(user!!.uid).setValue(helperClass)
                                user!!.sendEmailVerification()

                                val intent = Intent(this, LoginActivity::class.java)
                                Toast.makeText(
                                    this, "Se ha registrado con éxito", Toast.LENGTH_SHORT
                                ).show()
                                startActivity(intent)

                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    this, "Error: ${exception.message}", Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "La contraseña no coincide", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Los espacios no pueden estar vacios", Toast.LENGTH_SHORT)
                .show()
        }
    }
}