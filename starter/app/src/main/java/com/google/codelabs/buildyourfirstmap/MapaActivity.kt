package com.google.codelabs.buildyourfirstmap

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityMapaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil


class MapaActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    // Nuestro mapa
    private lateinit var map: GoogleMap

    // Variable para ver si los permisos han sido denegados
    private var permissionDenied = false

    private lateinit var binding: ActivityMapaBinding
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

                    var met = SphericalUtil.computeDistanceBetween(LatLng(lastLatitude,lastLongitude),LatLng(location.latitude,location.longitude))
                    //filtramos casos extremos donde el gps se vaya a lugares lejanos
                    if(met<1000){

                        Log.w("distancemeter", "Achievement start value  $challengeDistance")
                        Log.w("distancemeter", "Distancia del metro calculada $met")
                        Log.w("distancemeter", "Restamos $challengeDistance a $met y obtenermos ${challengeDistance-met}")
                        challengeDistance -= met
                        Log.w("distancemeter", "The current challenge Distance is  $challengeDistance")
                        mapsDistanceReward -= met
                        if (mapsDistanceReward <=0){
                            rewardPlayerCurrencyOne()
                            mapsDistanceReward = 1000.0
                        }
                        if (challengeDistance<=0){
                            achievementStart = false
                            rewardPlayerCurrencyTwo()
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

    private fun rewardPlayerCurrencyOne() {

        val uId = checkUser()
        val db = FirebaseFirestore.getInstance()


        db.collection("users").whereEqualTo("uId", uId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    var currentCurrency = document.data["currencyOne"].toString().toInt()
                    db.collection("users").document(uId).update("currencyOne",currentCurrency+50)
                    binding.fragmentContainerView.getFragment<MarcadorFragment>().checkCurrencyOne(uId)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("tag", "Error getting documents ", exception)

            }











    }

    private fun rewardPlayerCurrencyTwo() {

        val uId = checkUser()
        val db = FirebaseFirestore.getInstance()


        db.collection("users").whereEqualTo("uId", uId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    var currentCurrency = document.data["currencyTwo"].toString().toInt()
                    db.collection("users").document(uId).update("currencyTwo",currentCurrency+challengeReward)
                    binding.fragmentContainerView.getFragment<MarcadorFragment>().checkCurrencyTwo(uId)

                }
            }
            .addOnFailureListener { exception ->
                Log.w("tag", "Error getting documents ", exception)

            }

        if (unlockAchievement){
            achievementTitle = "El logro de Kenny"
            db.collection("achievements").whereEqualTo("title", achievementTitle).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        val usAchiev = hashMapOf(
                            "mix" to (uId + document.id)

                        )
                        db.collection("userAchievements").document().set(usAchiev)
                            .addOnSuccessListener { Log.d("tag", "Has conseguido un nuevo reto!")}
                            .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu") }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("tag", "Error getting documents ", exception)

                }



        }





    }

    override fun onResume() {
        super.onResume()
        Log.w("saved", "Resuming...")
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generateFragment()
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // Nos traemos los datos si nos hemos salido de la actividad
        Log.w("kennylog", "The distance required for this challenge is ${savedInstanceState.toString()}" )
        if (savedInstanceState != null) {
           challengeDistance =  savedInstanceState.getDouble("challengeDistance")
           Log.w("saved","HEMOS RECUPERADO LOS DATOS :D")
        }


        //testeo para retos

        val achievementId = "A6MipiwGLR9RE6xEnNgX"
        val db = FirebaseFirestore.getInstance()

        challengeTitle = "Cuarto titulo"

        db.collection("challenges").whereEqualTo("title", challengeTitle).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    challengeDistance = document.data["distance"].toString().toDouble()
                    challengeReward = document.data["reward"].toString().toInt()
                    unlockAchievement = document.data["unlockAchievement"].toString().toBoolean()


                    Log.w("kennylog", "The distance required for this challenge is $challengeDistance" )
                    achievementStart = true
                    Log.w("tag", "Now is the moment, START RUNNING" )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("tag", "Error getting documents ", exception)

            }






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
            Log.w("TAG", "Permitimos que la aplicación vaya correctamente")
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

    override fun onMyLocationClick(l: Location) {
        Toast.makeText(this,"Estás en ${l.latitude} , ${l.longitude}", Toast.LENGTH_SHORT).show()
    }

}

