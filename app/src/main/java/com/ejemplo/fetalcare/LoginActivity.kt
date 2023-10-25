package com.ejemplo.fetalcare

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ejemplo.fetalcare.databinding.ActivityLoginBinding
import com.ejemplo.fetalcare.databinding.DialogForgotBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtener instancias
        firebaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        //Boton de login
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail
            val password = binding.loginPassword
            logIn(email, password)
        }

        //Olvidar contraseña
        binding.forgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            view.findViewById<Button>(R.id.btnResetl).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }

        //Redireccionar al signup
        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }

    }

    override fun onStart() {
        super.onStart()
        //Agrego un AuthStateListener al inicio de la actividad
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        // Elimino el AuthStateListener al detener la actividad para evitar fugas de memoria
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    //Funcion para revisar el correo electronico y enviar el correo de recuperacion de contraseña
    private fun compareEmail(email: EditText) {
        //Reviso que el cuadro del correo no este vacio
        if (email.text.toString().isEmpty()) {
            Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_LONG).show()
            return
        }

        //Reviso que se ingrese un correo electronico
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_LONG).show()
        } else {
            firebaseAuth.fetchSignInMethodsForEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods == null || signInMethods.isEmpty()) {
                            Toast.makeText(this, "Correo no registrado", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            firebaseAuth.sendPasswordResetEmail(email.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Revisa tu correo",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    }
                }
        }
    }

    //Funcion para revisar el correo y contraseña e iniciar sesion
    private fun logIn(email: EditText, password: EditText){
        if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Los espacios no pueden estar en blanco", Toast.LENGTH_SHORT)
                .show()
        }
    }
}