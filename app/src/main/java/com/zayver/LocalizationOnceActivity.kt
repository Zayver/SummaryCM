package com.zayver

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.zayver.databinding.ActivityLocalizationOnceBinding

class LocalizationOnceActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if(it){
            Log.d("Mio","Acceso a ubicacion once granted")
        }
        else{
            Log.d("Mio","Acceso a ubicacion once DENIEDDDD")
        }
    }
    private lateinit var binding: ActivityLocalizationOnceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalizationOnceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onResume() {
        super.onResume()
        updateLocation()
    }
    private fun updateLocation(){
        when{
            ContextCompat.checkSelfPermission(baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ->{
                mFusedLocationClient.lastLocation.addOnSuccessListener {
                    binding.laittudText.text = it.latitude.toString()
                    binding.longitudText.text = it.longitude.toString()
                }
            }
            else ->{
                Log.d("Mio", "Not accepted");
            }
        }
    }
}