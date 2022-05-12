package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityMapaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class MapaActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    // Nuestro mapa
    private lateinit var map: GoogleMap

    // Variable para ver si los permisos han sido denegados
    private var permissionDenied = false

    private lateinit var binding: ActivityMapaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference


    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e., how often you should receive
    // updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Log.w("tag", "onlocationresult$location")
            }
        }
    }

    // Used only for local storage of the last known location. Usually, this would be saved to your
    // database, but because this is a simplified sample without a full database, we only need the
    // last location to create a Notification if the user navigates away from the app.
    private var currentLocation: Location? = null

    private var serviceRunningInForeground = false

    private lateinit var notificationManager: NotificationManager

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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


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

    override fun onStart() {
        super.onStart()
        if (isLocationPermissionGranted()){
//            getLastLocation()
              checkSettingsAndStartLocationUpdates()
        } else{
            requestLocationPermission()
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun checkSettingsAndStartLocationUpdates(){
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()

        val client = LocationServices.getSettingsClient(this)

//        var locationSettingsResponseTask: Task<LocationSettingsResponse> =
//            client.checkLocationSettings(request)
        val locationSettingsResponseTask = client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener {
            startLocationUpdates()
        }

        locationSettingsResponseTask.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // Handle result in onActivityResult()
                    e.startResolutionForResult(
                        this@MapaActivity,
                        1001
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }

        }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates(){
    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it == null){
                Toast.makeText(this,"Sorry can't get location",Toast.LENGTH_SHORT).show()

            } else it.apply {
                val latitude = it.latitude
                val longitude = it.longitude
                Log.w("TAG", "Latitud: $latitude, Longitud: $longitude")
            }
        }
    }


    // Función que crea el fragmento del mapa
    private fun generateFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    // Esto  se llama cuando el mapa haya sido creado
    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        map.setOnMyLocationClickListener(this)
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
            Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_LONG).show()
        } else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION),
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
            Log.w("TAG", "AAAAA")
//            getLastLocation()
            checkSettingsAndStartLocationUpdates()
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

    override fun onMyLocationClick(l: Location) {
        Toast.makeText(this,"Estás en ${l.latitude} , ${l.longitude}", Toast.LENGTH_SHORT).show()
    }

}

