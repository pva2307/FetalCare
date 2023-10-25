package com.ejemplo.fetalcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ejemplo.fetalcare.databinding.ActivityHelpBinding
import com.ejemplo.fetalcare.databinding.ActivityPasswordBinding

class HelpActivity : AppCompatActivity() {

    //Declaracion de variables
    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Boton para ir hacia atras
        binding.backBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
            finish()
        }
    }
}