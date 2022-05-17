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
import android.widget.Button
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



    var rewardStart = false
    var lastLatitude = 0.0
    var lastLongitude = 0.0
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

                Log.w("distancemeter", "Achievement start value  $rewardStart")

                if (rewardStart){

                    var met = SphericalUtil.computeDistanceBetween(LatLng(lastLatitude,lastLongitude),LatLng(location.latitude,location.longitude))
                    //filtramos casos extremos donde el gps se vaya a lugares lejanos
                    if(met<1000){

                        mapsDistanceReward -= met
                        if (mapsDistanceReward <=0){
                            //Toast.makeText(this,"Has ganado unas cuantas monedas",Toast.LENGTH_SHORT)
                            Log.w("reward", "Has ganado monedas ")
                            rewardPlayerCurrencyOne()
                            mapsDistanceReward = 1000.0
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

        Log.w("saved", "Recuperando datos")
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardamos Challenge distance

        Log.w("kenny", "Guardamos datos")


    }

    private fun rewardPlayerCurrencyOne() {

        val uId = checkUser()
        val db = FirebaseFirestore.getInstance()

        Log.w("DINERO","Hemos ganado 50 pesitos extra")
        db.collection("users").whereEqualTo("uId", uId).get()
            .addOnSuccessListener { documents ->
                Log.w("DINERO","Uid${uId}")
                Log.w("DINERO","Hemos ganado 50 pesitos extra${documents.toString()}")
                for (document in documents) {
                    Log.w("DINERO","document ${document.id}")
                    var currentCurrency = document.data["currencyOne"].toString().toInt() + 50
                    Log.w("DINERO","current curre ${currentCurrency}")
                    db.collection("users").document(document.id).update("currencyOne",currentCurrency)
                    binding.fragmentContainerView.getFragment<MarcadorFragment>().checkCurrencyOne(uId)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("tag", "Error getting documents ", exception)

            }

    }



    override fun onResume() {
        super.onResume()
//        binding.fragmentContainerView2.getFragment<MarcadorFragment>().checkCurrencyOne(checkUser())
//        binding.fragmentContainerView2.getFragment<MarcadorFragment>().checkCurrencyTwo(checkUser())

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

           Log.w("saved","HEMOS RECUPERADO LOS DATOS :D")
        }

        val BtnInventario = findViewById<Button>(R.id.InventoryBtn)
        BtnInventario.setOnClickListener{
            startActivity(Intent(applicationContext, InventoryActivity::class.java))
        }
        val BtnTienda = findViewById<Button>(R.id.shopBtn)
        BtnTienda.setOnClickListener {
            startActivity(Intent(applicationContext, ShopActivity::class.java))
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        rewardStart = true

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

