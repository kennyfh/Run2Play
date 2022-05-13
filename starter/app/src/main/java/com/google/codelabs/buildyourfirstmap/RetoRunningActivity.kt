package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityRetoRunningBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.maps.android.SphericalUtil

class RetoRunningActivity : AppCompatActivity(){

    // Atributos que vamos a utilizar para que funcione esto
    // Variable para ver si los permisos han sido denegados
    private var permissionDenied = false

    private lateinit var binding: ActivityRetoRunningBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference



    var achievementStart = false
    var challengeDistance = 20000.0 as Double
    var lastLatitude = 0.0
    var lastLongitude = 0.0
    var challengeTitle = ""
    var challengeReward = 0
    var achievementTitle = ""
    var unlockAchievement = true
    var mapsDistanceReward = 1000.0



    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e., how often you should receive
    // updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {

                Log.w("distancemeter", "Achievement start value  $achievementStart")
                Log.w("distancemeter", "Distancia restante $challengeDistance")
                if (achievementStart){

                    var met = SphericalUtil.computeDistanceBetween(
                        LatLng(lastLatitude,lastLongitude),
                        LatLng(location.latitude,location.longitude)
                    )
                    //filtramos casos extremos donde el gps se vaya a lugares lejanos
                    if(met<1000){

                        Log.w("distancemeter", "Achievement start value  $challengeDistance")
                        Log.w("distancemeter", "Distancia del metro calculada $met")
                        Log.w("distancemeter", "Restamos $challengeDistance a $met y obtenermos ${challengeDistance-met}")
                        challengeDistance -= met
                        Log.w("distancemeter", "The current challenge Distance is  $challengeDistance")
                        mapsDistanceReward -= met
                        if (mapsDistanceReward <=0){
                            //rewardPlayerCurrencyOne()
                            mapsDistanceReward = 1000.0
                        }
                        if (challengeDistance<=0){
                            achievementStart = false
                            // rewardPlayerCurrencyTwo()
                            Log.w("distancemeter", "REWAAAAAAAAAARD IS COMING   $challengeDistance")
                        }

                    }

                }
                Log.w("tag", "onlocationresult$location")

                lastLatitude = location.latitude
                lastLongitude = location.longitude

            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        challengeDistance =  savedInstanceState.getDouble("challengeDistance")
        Log.w("saved", "Recuperando datos")
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardamos Challenge distance
        outState.putDouble("challengeDistance", challengeDistance)
        Log.w("kenny", "Guardamos datos")


    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetoRunningBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_reto_running)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // Nos traemos los datos si nos hemos salido de la actividad
        Log.w("kennylog", "The distance required for this challenge is ${savedInstanceState.toString()}" )
        if (savedInstanceState != null) {
            challengeDistance =  savedInstanceState.getDouble("challengeDistance")
            Log.w("saved","HEMOS RECUPERADO LOS DATOS :D")
        }


        // Conseguir la localización
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

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

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != MapaActivity.LOCATION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (isLocationPermissionGranted()) {
            Log.w("TAG", "Permitimos que la aplicación vaya correctamente")
//            getLastLocation()
            checkSettingsAndStartLocationUpdates()
        } else {
            permissionDenied = true

        }
    }


    override fun onResume() {
        super.onResume()
        Log.w("saved", "Resuming...")
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

    // Función que pregunta por los permisos de ubicación
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) ==  PackageManager.PERMISSION_GRANTED


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
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
                        this@RetoRunningActivity,
                        1001
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }

    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this,"Ve a ajustes y acepta los permisos", Toast.LENGTH_LONG).show()
        } else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }


    }


    /*
    *  ON STOP
    * */
    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }



    // Variable quen nos dice el código de localización
    companion object {
        const val LOCATION_REQUEST_CODE = 96

    }




}