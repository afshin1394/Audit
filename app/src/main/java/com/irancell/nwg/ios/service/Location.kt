package com.irancell.nwg.ios.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

@SuppressLint("MissingPermission")

class Location(private val context: Context) {
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false

    var _location: Location? = null
    val location get() = _location!!

    var latitude = 0.0
    var longitude = 0.0
    var altitude = 0.0
    var accurancy = 0.0f
    var bearing = 0.0f
    var speed = 0.0f
    var time = 0L
    var _locationManager: LocationManager? = null
    val locationManager get() = _locationManager

    val locationObserver: MutableLiveData<Location> = MutableLiveData()

    init {

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                this@Location._location = location
                locationObserver.value = location
                locationObserver.value?.also {
                    if (it.latitude.toString().length > 12)
                        it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                    if (it.longitude.toString().length > 12)
                        it.longitude = it.longitude.toString().substring(0, 12).toDouble()
                }
            }


            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}
        }
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        locationObserver.value = it
                        locationObserver.value?.also {
                            if (it.latitude.toString().length > 12)
                                it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                            if (it.longitude.toString().length > 12)
                                it.longitude = it.longitude.toString().substring(0, 12).toDouble()
                        }
                        this@Location._location = it
                        _location?.let { _location ->
                            latitude = _location.getLatitude()
                            longitude = _location.getLongitude()
                            altitude = _location.getAltitude()
                            accurancy = _location.getAccuracy()
                            bearing = _location.getBearing()
                            speed = _location.getSpeed()
                            time = _location.time
                        }
                        Log.i("fusedLocationClient", "provideLocation: " + _location)
                    }


                }

            _locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
            isNetworkEnabled =
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
            if (isGPSEnabled || isNetworkEnabled) {
                canGetLocation = true
                if (isGPSEnabled) {
                    if (context.checkSelfPermission(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                            2000
                        )
                    }

                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        3000,
                        0f,
                        locationListener
                    )
                    _location =
                        locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    locationObserver.value =
                        locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.also {
                            if (it.latitude.toString().length > 12)
                                it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                            if (it.longitude.toString().length > 12)
                                it.longitude = it.longitude.toString().substring(0, 12).toDouble()
                        }
                    _location?.let { _location ->
                        latitude = _location.getLatitude()
                        longitude = _location.getLongitude()
                        altitude = _location.getAltitude()
                        accurancy = _location.getAccuracy()
                        bearing = _location.getBearing()
                        speed = _location.getSpeed()
                        time = _location.time
                    }


                }
                if (isNetworkEnabled) {
                    if (context.checkSelfPermission(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                            2000
                        )
                    }
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        60000,
                        3f,
                        locationListener
                    )
                    _location =
                        locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    locationObserver.value =
                        locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            ?.also {
                                if (it.latitude.toString().length > 12)
                                    it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                                if (it.longitude.toString().length > 12)
                                    it.longitude =
                                        it.longitude.toString().substring(0, 12).toDouble()
                            }

                    _location?.let { _location ->
                        latitude = _location.getLatitude()
                        longitude = _location.getLongitude()
                        altitude = _location.getAltitude()
                        accurancy = _location.getAccuracy()
                        bearing = _location.getBearing()
                        speed = _location.getSpeed()
                        time = _location.time
                    }


                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun getLocationValue() : Location{
        return location
    }
    fun getLocation(): LiveData<Location> {
        return locationObserver
    }

}