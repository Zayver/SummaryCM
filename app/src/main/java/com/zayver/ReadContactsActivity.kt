package com.zayver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zayver.databinding.ActivityReadContactsBinding
import com.zayver.databinding.ContactItemBinding
import com.zayver.databinding.CountryItemBinding
import java.util.*


data class Contact(val name:String, val number:String)

class ContactAdapter(private val contacts: List<Contact>): RecyclerView.Adapter<ContactAdapter.ViewHolder>(){
    inner class ViewHolder (binding: ContactItemBinding): RecyclerView.ViewHolder(binding.root){
        val name = binding.contactNameText
        val number = binding.phoneNumberText
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = ContactItemBinding.inflate(inflater)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact: Contact = contacts[position]
        // Set item views based on your views and data model
        holder.name.text = contact.name
        holder.number.text = contact.number
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

}

class ReadContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadContactsBinding
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if(it){
            Log.d("Mio","Acceso a contactos granted")
        }
        else{
            Log.d("Mio","Acceso a contactos DENIEDDDD")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        requestPermission.launch(Manifest.permission.READ_CONTACTS)
        initView()
    }

    private fun initView(){
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(baseContext,Manifest.permission.READ_CONTACTS) -> {
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(
                        ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Profile.HAS_PHONE_NUMBER
                    ),
                    null,
                    null,
                    null
                )
                val contacts = LinkedList<Contact>()
                if(cursor!!.count > 0){
                    while (cursor.moveToNext()){
                        contacts.add(Contact(cursor.getString(0), cursor.getString(1)))
                    }
                }
                binding.contactsRecycleview.adapter = ContactAdapter(contacts)
                binding.contactsRecycleview.layoutManager = LinearLayoutManager(baseContext)
            }
            else -> {
                Log.d("MIO", "Denied")
            }
        }
    }
}