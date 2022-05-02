package com.google.codelabs.buildyourfirstmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class LogrosAdapter(private val logrosList: List<Logro>) : RecyclerView.Adapter<LogrosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogrosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LogrosViewHolder(layoutInflater.inflate(R.layout.item_logro, parent, false))
    }

    override fun onBindViewHolder(holder: LogrosViewHolder, position: Int) {
        val item = logrosList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return logrosList.size
    }


}