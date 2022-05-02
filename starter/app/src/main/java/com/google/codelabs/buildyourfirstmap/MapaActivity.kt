package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityMapaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

class MapaActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMapaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val uId = checkUser()
        checkCurrencyOne(uId)
        checkCurrencyTwo(uId)

        // Initialize and assign variable
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigation.selectedItemId = R.id.mapa;

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

    private fun checkCurrencyOne(userName: String){

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var currency = ""

        db.collection("users").whereEqualTo("uId",userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    binding.marcador.currencyOne.text = document.data["currencyOne"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }



    }

    private fun checkCurrencyTwo(userName: String) {
        //sacamos el id del user autentificado

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados


        var currency = ""

        db.collection("users").whereEqualTo("uId", userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.marcador.currencyTwo.text = document.data["currencyTwo"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("tag", "Error getting documents ", exception)

            }
    }

}