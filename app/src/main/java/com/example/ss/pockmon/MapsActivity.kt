package com.example.ss.pockmon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Request user for dangerous permission
        checkPermission()
        loadPockemon()

    }

    var ACCESSLOCATION = 123

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ), ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    fun getUserLocation() {
        Toast.makeText(
                this,
                "Location on",
                Toast.LENGTH_SHORT
        ).show()

        var myLocation = MyLocationListener()

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 3, 3f, myLocation
        )

        var mythread = myThread()
        mythread.start()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(
                            this,
                            "Permission required to continue using this service",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }


    // Get user location
    var location: Location? = null

    inner class MyLocationListener : LocationListener {
        constructor() {
            location = Location("Start")
            location!!.latitude = -1.277089
            location!!.longitude = 36.826548
        }

        override fun onLocationChanged(p0: Location?) {
            location = p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation: Location? = null

    inner class myThread : Thread {

        constructor() : super() {
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {
            while (true) {
                try {

                    if (oldLocation!!.distanceTo(location) == 0f) {
                        continue
                    }

                    oldLocation = location

                    runOnUiThread {
                        mMap.clear()
                        // Add a marker for Mario
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("Mario")
                                .snippet("${location!!.latitude}, ${location!!.longitude}")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        // show pockemons

                        for (i in 0..(listPockemons.size - 1)) {
                            var newPockemon = listPockemons[i]
                            if (!newPockemon.isCatch!!) {
                                val pockemonLoc = LatLng(newPockemon.location!!.latitude,
                                        newPockemon.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                        .position(pockemonLoc)
                                        .title(newPockemon.name!!)
                                        .snippet("Power: ${newPockemon.power!!}")
                                        .icon(BitmapDescriptorFactory.fromResource(
                                                newPockemon.image!!))
                                )
                                if (location!!.distanceTo(newPockemon.location) < 2) {
                                    newPockemon.isCatch = true
                                    listPockemons[i] = newPockemon
                                    playerPower += newPockemon.power!!
                                    Toast.makeText(
                                            this@MapsActivity,
                                            "You captured ${newPockemon.name}",
                                            Toast.LENGTH_SHORT
                                            ).show()
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)
                } catch (e: Exception) {

                }
            }
        }

    }

    var playerPower = 0.0

    var listPockemons = ArrayList<Pockemon>()

    fun loadPockemon() {
        listPockemons.add(Pockemon(
                "Charmander",
                "Charmander living in Kencom",
                R.drawable.charmander,
                55.0,
                -1.285979,
                36.825228
        ))
        listPockemons.add(Pockemon(
                "Squirtle",
                "Squirtle living in Uhuru Park",
                R.drawable.squirtle,
                33.5,
                -1.290100,
                36.817248
        ))

        listPockemons.add(Pockemon(
                "Bulbasaur",
                "Bulbasaur living in Jeevanjee Gardens",
                R.drawable.bulbasaur,
                90.5,
                -1.281227,
                36.819531
        ))
    }


}
