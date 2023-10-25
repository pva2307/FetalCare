package com.ejemplo.fetalcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.EditText
import android.widget.Toast
import com.ejemplo.fetalcare.databinding.ActivityPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PasswordActivity : AppCompatActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivityPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
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

        //Boton para cambiar contrasena
        binding.changePswButton.setOnClickListener {
            val password = binding.edittxtPswActual
            val newPassword = binding.edittxtNewPsw
            val confirmNewPassword = binding.edittxtConfirmNewPsw
            val user = firebaseAuth.currentUser
            val uid = user?.uid
            changePassword(password, newPassword, confirmNewPassword, reference, uid, user)
        }
    }

    //Funcion para el cambio de contraseña
    private fun changePassword(password: EditText, newPassword:EditText, confirmNewPassword: EditText, reference: DatabaseReference,
                               uid: String?,
                               user: FirebaseUser?){
        if(password.text.toString().isNotEmpty() && newPassword.text.toString().isNotEmpty() && confirmNewPassword.text.toString().isNotEmpty()){
            if(newPassword.text.toString() == confirmNewPassword.text.toString()){
                val regex =
                    "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$".toRegex()
                if(confirmNewPassword.text.toString().length >= 8 && confirmNewPassword.text.toString().matches(regex)){
                    reference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val userSnapshot = dataSnapshot.child(uid!!)
                            val helperClass = userSnapshot.getValue(HelperClass::class.java)
                            val actualPassword = helperClass?.password
                            if(password.text.toString() == actualPassword){
                                user!!.updatePassword(confirmNewPassword.text.toString())
                                reference.child(uid!!).child("password").setValue(confirmNewPassword)
                                ui()
                            }else{
                                val context = applicationContext
                                Toast.makeText(context, "La contraseña actual no coincide ", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error al cambiar la contraseña: ${error.message}")
                        }
                    }
                    )
                }else{
                    Toast.makeText(
                        this,
                        "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(this, "La contraseña no coincide", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Los espacios no pueden estar vacios", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //Funcion para modificar la interfaz grafica
    private fun ui(){
        binding.layoutSuccess.visibility = VISIBLE
        binding.layoutPassword.visibility = INVISIBLE
        binding.textInfoPassword.visibility = INVISIBLE
        binding.textMayus.visibility = INVISIBLE
        binding.textMinus.visibility = INVISIBLE
        binding.textNum.visibility = INVISIBLE
        binding.textEspecial.visibility = INVISIBLE
    }
}