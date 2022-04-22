package com.google.codelabs.buildyourfirstmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment


class Mapa : Fragment() {

    private lateinit var mMap : GoogleMap
    private var mapReady =  false
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap -> mMap = googleMap
            mapReady = true
//            updateMap()
        }
        return rootView
    }

    private fun updateMap() {
        TODO("Not yet implemented")
    }

}