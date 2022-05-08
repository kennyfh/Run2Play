package com.google.codelabs.buildyourfirstmap.adapter

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.buildyourfirstmap.R
import com.google.codelabs.buildyourfirstmap.RetoData
import com.google.codelabs.buildyourfirstmap.databinding.ItemRetoBinding

class RetoHolder(view: View):RecyclerView.ViewHolder(view) {

//    // Se encarga de agarrar cada elemento de la lista y pintarlos
//    // dentro del recyclerview
    val nombreReto = view.findViewById<TextView>(R.id.nombreReto)
    val descripcionReto = view.findViewById<TextView>(R.id.descripcionReto)
    val statusReto = view.findViewById<TextView>(R.id.statusReto)
    // android:id="@+id/nombreReto"  android:id="@+id/descripcionReto" android:text="Activo" android:id="@+id/statusReto"


    fun render(retoModel:RetoData){
        nombreReto.text = retoModel.title
        descripcionReto.text = retoModel.description
        if (retoModel.unlockArch){
            statusReto.text = "Activo"
        } else{
            statusReto.text="No desbloqueado"
        }
//        statusReto.text = retoModel.
    itemView.setOnClickListener {
        Toast.makeText(
            nombreReto.context,
            retoModel.description,
            Toast.LENGTH_LONG
        ).show() }

    }
}