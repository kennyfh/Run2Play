package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityLogrosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

class LogrosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogrosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val uId = checkUser()
        checkName(uId)

        initRecyclerView()

        // Initialize and assign variable
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigation.selectedItemId = R.id.logros;

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
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerLogros)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = LogrosAdapter(logrosProvider.setup())
    }

    private fun checkUser(): String {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){

            //user not null,user is logged in, get user info
            val uid = firebaseUser.uid
            //set to text view
            return uid.toString()


        }
        else {


            return ""
        }

    }

    private fun checkName(userName: String){

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var name = ""

        db.collection("users").whereEqualTo("uId",userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    binding.nombrePerfil.text = document.data["username"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }



    }

}