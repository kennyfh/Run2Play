package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityMapaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

class MapaActivity: AppCompatActivity(), OnMapReadyCallback {

    // Nuestro mapa
    private lateinit var map: GoogleMap

    // Variable para ver si los permisos han sido denegados
    private var permissionDenied = false

    private lateinit var binding: ActivityMapaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generateFragment()

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

    // Función que crea el fragmento del mapa
    private fun generateFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    // Esto  se llama cuando el mapa haya sido creado
    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        enableMyLocation()

    }

    /**
     * Permite el layer de tu localización si los permisos de localización
     * han sido garantizados
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if(!::map.isInitialized) return
        // Si se han aceptado los permisos
        if(isLocationPermissionGranted()){
            map.isMyLocationEnabled = true

        } else{ // Si no
            requestLocationPermission()
        }

    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,Manifest.permission.ACCESS_FINE_LOCATION
    ) ==  PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
        } else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }


    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (isLocationPermissionGranted()) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            permissionDenied = true

        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    private fun showMissingPermissionError() {
        //newInstance(true).show(supportFragmentManager, "dialog")
        Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
    }


    // Variable quen nos dice el código de localización
    companion object {
        const val LOCATION_REQUEST_CODE = 1
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