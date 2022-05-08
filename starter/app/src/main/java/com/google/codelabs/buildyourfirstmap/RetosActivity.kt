package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.adapter.RetoAdapter
import com.google.codelabs.buildyourfirstmap.databinding.ActivityRetosBinding

class RetosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_retos)
        initRecyclerView()
        /*val recyclerView = findViewById<RecyclerView>(R.id.recyclerRetos)
        val rChild = recyclerView.getChildAt(0) as androidx.cardview.widget.CardView
        val rSupChild = rChild.getChildAt(0) as LinearLayout
        val rSupSupChild = rSupChild.getChildAt(0) as RelativeLayout
        val rMegaChild = rSupSupChild.getChildAt(1) as LinearLayout
        val rOmegaChild = rMegaChild.getChildAt(0) as TextView
        rOmegaChild.text = "omg super cute"*/




        // Initialize and assign variable
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigation.selectedItemId = R.id.retos;


        // Perform item selected listener
        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.retos -> {
                    startActivity(Intent(applicationContext, RetosActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnItemSelectedListener true
                }
                R.id.logros -> {
                    startActivity(Intent(applicationContext, LogrosActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnItemSelectedListener true
                }
                R.id.mapa -> {
                    startActivity(Intent(applicationContext, MapaActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnItemSelectedListener true
                }
            }
            false
        })

    }

    private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerRetos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter= RetoAdapter(RetoProvider.retoList)

    }

}