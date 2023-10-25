package com.ejemplo.fetalcare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.fetalcare.R
import com.ejemplo.fetalcare.Results

class ResultsAdapter(val resultsList:List<Results>, private val onClickListener:(Results) -> Unit): RecyclerView.Adapter<ResultsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ResultsViewHolder(layoutInflater.inflate(R.layout.item_layout, parent, false))
    }

    override fun getItemCount(): Int = resultsList.size //Indica el tamano que va a tener la lista

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        val item = resultsList[position]
        holder.render(item, onClickListener)

    }
}