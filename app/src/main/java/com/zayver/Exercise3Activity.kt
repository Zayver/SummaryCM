package com.zayver

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.zayver.databinding.ActivityEjercicio3Binding
import com.zayver.databinding.LocationItemBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

data class Position(val latitude: Double, val longitude:Double, val date: String){
    fun toJson(): JSONObject{
        val json = JSONObject()
        json.put("latitude", latitude)
        json.put("longitude", longitude)
        json.put("date", date)
        return json
    }
}

class PositionAdapter(private val locations: List<Position>): RecyclerView.Adapter<PositionAdapter.ViewHolder>(){
    inner class ViewHolder (binding: LocationItemBinding): RecyclerView.ViewHolder(binding.root){
        val latitude = binding.latitudeText
        val longitude = binding.longitudeText
        val date = binding.dateText
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = LocationItemBinding.inflate(inflater)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val position: Position = locations[pos]
        // Set item views based on your views and data model
        holder.latitude.text = position.latitude.toString()
        holder.longitude.text = position.longitude.toString()
        holder.date.text = position.date
    }

    override fun getItemCount(): Int {
        return locations.size
    }

}



class Exercise3Activity : AppCompatActivity() {
    companion object{
        const val JSONFILENAME = "locations.json"
    }
    private lateinit var binding:ActivityEjercicio3Binding
    //Localización
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    //Permisos
    private val requestGPSenable = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ){
        if(it.resultCode == RESULT_OK){
            Log.d("Mio", "GPS PERMISSION GRANTED")
        }
        else{
            Log.d("Mio", "GPS off")
        }
    }
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

    //localizacion actual
    private lateinit var actualLocation: Position
    //lista de localizaciones
    private var locations = ArrayList<Position>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicio3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getInternalLocations()
        Log.d("Mio", "Info size: ${locations.size}")
        binding.localizationRecycleview.adapter = PositionAdapter(locations)
        binding.localizationRecycleview.layoutManager = LinearLayoutManager(baseContext)
        binding.saveLocationButton.setOnClickListener {
            locations.add(actualLocation)
            saveLocation(actualLocation)
            binding.localizationRecycleview.adapter?.notifyDataSetChanged()
        }
        binding.saveLocationButton.isEnabled = false
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                binding.saveLocationButton.isEnabled = true
                actualLocation = Position(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude,
                    getDateHour()
                )
                binding.latitudText.text = actualLocation.latitude.toString()
                binding.longitudText.text = actualLocation.longitude.toString()
            }
        }

        mLocationRequest = createLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        checkLocationSettings()

    }
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(10000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    private fun getDateHour(): String {
        return getDateTimeInstance().format(Date())
    }
    private fun startLocationUpdates(){
        when{
            ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ->{
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.getMainLooper() )
            }
            else -> {
                Log.d("Mio", "TRING")
            }
        }
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
                requestGPSenable.launch(isr)
            }else{
                Log.d("Mio", "NO GPS AVALIABLE")
            }
        }
    }
    private fun getInternalLocations(){
        val file = File(baseContext.getExternalFilesDir(null), JSONFILENAME)
        if(!file.exists()){
            return
        }
        val input = file.bufferedReader().use {
            it.readText()
        }
        val json = JSONObject(input)
        val arr = json.getJSONArray("positions")
        for (i in 0 until arr.length()) {
            val latitude = arr.getJSONObject(i).getDouble("latitude")
            val longitude = arr.getJSONObject(i).getDouble("longitude")
            val date = arr.getJSONObject(i).getString("date")
            locations.add(Position(latitude, longitude, date))
        }
    }
    private fun saveLocation(location: Position){
        val file = File(baseContext.getExternalFilesDir(null), JSONFILENAME)
        val input: String
        try{
            input = file.bufferedReader().use {
                it.readText()
            }
        }catch (e:FileNotFoundException){
            val obj = JSONObject()
            val arr = JSONArray()
            arr.put(actualLocation.toJson())
            obj.put("positions",arr)
            file.bufferedWriter().use {
                it.write(obj.toString())
            }
            return
        }
        val json = JSONObject(input)
        val arr = json.getJSONArray("positions")
        arr.put(location.toJson())
        file.bufferedWriter().use {
            it.write(json.toString())
        }
    }

}