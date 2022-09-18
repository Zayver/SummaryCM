package com.zayver

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.zayver.databinding.ActivityLocalizationSuscriptionBinding

class LocalizationSubscriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocalizationSuscriptionBinding
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ){
        if(it.resultCode == RESULT_OK){
            startLocationUpdates()
        }
        else{
            Log.d("Mio", "GPS off")
        }
    }
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalizationSuscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d("Mio", "Info: latitude: $locationResult.lastLocation.latitude.toString(), longitude $locationResult.lastLocation.longitude.toString()")
                binding.latitudeText.text = locationResult.lastLocation.latitude.toString()
                binding.longitudeText.text = locationResult.lastLocation.longitude.toString()
            }

        }
        mLocationRequest = createLocationRequest()
        checkLocationSettings()

    }
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    private fun startLocationUpdates(){
        when{
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ->{
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,Looper.getMainLooper() )
                    }
            else -> {
                Log.d("Mio", "Location suscription denied")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
    private fun stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }
    private fun checkLocationSettings(){
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Log.d("Mio","GPS is on")
            startLocationUpdates()
        }
        task.addOnFailureListener{
            if((it as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED){
                val resolvable = it as ResolvableApiException
                val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                requestPermission.launch(isr)
            }else{
                Log.d("Mio", "NO GPS AVALIABLE")

            }
        }

    }
}