package com.zayver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zayver.databinding.ActivityReadJsonBinding
import com.zayver.databinding.CountryItemBinding
import org.json.JSONObject


data class Country(val name: String, val intName: String, val initials: String,
                   val capital: String) {
    override fun toString(): String {
        return "$name ($intName)"
    }
}
class CountryAdapter (private val mContacts: List<Country>) : RecyclerView.Adapter<CountryAdapter.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.nameText
        val intName = binding.intNameText
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = CountryItemBinding.inflate(inflater)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: CountryAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val country: Country = mContacts[position]
        // Set item views based on your views and data model
        Log.d("Hola", "Infooooooo")
        viewHolder.name.text = country.name
        viewHolder.intName.text = country.intName
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }
}


class ReadJsonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadJsonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadJsonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recycle = binding.countryRecyclerview
        Log.d("Hola", "Test")
        recycle.adapter = CountryAdapter(deserializeCountries())
        recycle.layoutManager = LinearLayoutManager(baseContext)
    }

    private fun deserializeCountries() : ArrayList<Country>{
        val asset = assets.open("paises.json")
        val jsonCountry = asset.bufferedReader().use {
            it.readText()
        }
        val json = JSONObject(jsonCountry)
        val arrList = ArrayList<Country>()
        val arr = json.getJSONArray("paises")
        for (i in 0 until arr.length()) {
            val name = arr.getJSONObject(i).getString("nombre_pais")
            val nameInt = arr.getJSONObject(i).getString("nombre_pais_int")
            val capital = arr.getJSONObject(i).getString("capital")
            val initials = arr.getJSONObject(i).getString("sigla")
            arrList.add(Country(name, nameInt, initials, capital))
        }
        return arrList
    }
}