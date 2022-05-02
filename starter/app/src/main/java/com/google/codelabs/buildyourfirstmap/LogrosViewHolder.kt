package com.google.codelabs.buildyourfirstmap

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class LogrosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val logroTitle = view.findViewById<TextView>(R.id.tvLogro)
    val logroIcon = view.findViewById<ImageView>(R.id.ivLogro)

    fun render(logroModel: Logro){
        logroTitle.text = logroModel.nameLogro

    }
}