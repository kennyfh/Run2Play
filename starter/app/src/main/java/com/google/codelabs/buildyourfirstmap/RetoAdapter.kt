package com.google.codelabs.buildyourfirstmap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.codelabs.buildyourfirstmap.R
import com.google.codelabs.buildyourfirstmap.RetoData
import com.google.codelabs.buildyourfirstmap.RetoProvider.Companion.retoList


class RetoAdapter(private val retoList:List<RetoData>) : RecyclerView.Adapter<RetoHolder>(){
    /*
    * Esta clase nos permite agarrar una lista y transformarla
    * en un RecyclerView
    * */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoHolder {
        //Devolver al viewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        return RetoHolder(layoutInflater.inflate(R.layout.item_reto,parent,false))
    }

    override fun onBindViewHolder(holder: RetoHolder, position: Int) {
        val item = retoList.get(position)
        holder.render(item)
    }

    // Devuelve el tamaño del listado que vamos a tener
    override fun getItemCount() = retoList.size
}