package com.irancell.nwg.ios.util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.util.toast.showToast


class GpsUtil(context: Context) {
        private val context: Context
        private val mSettingsClient: SettingsClient
        private val mLocationSettingsRequest: LocationSettingsRequest
        private val locationManager: LocationManager
        private val locationRequest: LocationRequest

        init {
            this.context = context
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mSettingsClient = LocationServices.getSettingsClient(context)

            locationRequest = LocationRequest.create()

            locationRequest. setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(10 * 1000)
            locationRequest.setFastestInterval(2 * 1000)
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            mLocationSettingsRequest = builder.build()
            //**************************
            builder.setAlwaysShow(true) //this is the key ingredient
            //**************************
        }

        // method for turn on GPS
        fun turnGPSOn(requestCode : Int,onGpsListener: onGpsListener?) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onGpsListener?.gpsStatus(true)
            } else {
                mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(
                        context as Activity,
                        OnSuccessListener<Any?> { //  GPS is already enable, callback GPS status through listener
                            onGpsListener?.gpsStatus(true)
                        })
                    .addOnFailureListener(context as Activity,
                        OnFailureListener { e ->
                            val statusCode = (e as ApiException).statusCode
                            when (statusCode) {
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    val rae = e as ResolvableApiException
                                    startIntentSenderForResult(context,rae.resolution.intentSender, requestCode, null, 0, 0, 0, null);

                                } catch (sie: IntentSender.SendIntentException) {
                                    Log.i("asdad", "PendingIntent unable to execute request.")
                                }
                                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                    val errorMessage =
                                        "Location settings are inadequate, and cannot be " +
                                                "fixed here. Fix in Settings."
                                    Log.e("asdad", errorMessage)
                                    showToast(context, R.string.gps_signal_lost)

                                }
                            }
                        })
            }
        }

        interface onGpsListener {
            fun gpsStatus(isGPSEnable: Boolean)
        }
    }
