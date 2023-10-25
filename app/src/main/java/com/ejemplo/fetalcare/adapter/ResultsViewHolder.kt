package com.ejemplo.fetalcare.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.fetalcare.R
import com.ejemplo.fetalcare.Results
import com.ejemplo.fetalcare.databinding.ItemLayoutBinding



class ResultsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ItemLayoutBinding.bind(view)
    fun render(resultado : Results, onClickListener:(Results) -> Unit){
        binding.lpm.text = resultado.valor
        binding.fecha.text = resultado.fecha
        binding.hora.text = resultado.hora
        itemView.setOnClickListener{ onClickListener(resultado)}
    }

}