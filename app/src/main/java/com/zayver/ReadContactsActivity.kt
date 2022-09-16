package com.zayver

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cursoradapter.widget.CursorAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zayver.databinding.ActivityReadContactsBinding
import com.zayver.databinding.CountryItemBinding



class ReadContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadContactsBinding
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        if(it){
            //obtenido
        }
        else{
            //no ibtenido
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission.launch(Manifest.permission.READ_CONTACTS)
        binding.contactsRecycleview.adapter = ContactAdapter()

    }
    private fun readContacts(): ArrayList<ContactsContract.Contacts>{
        val mProjection = arrayOf(
            ContactsContract.Profile._ID,
            ContactsContract.Profile.DISPLAY_NAME_PRIMARY
        )
        val mCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            mProjection, null, null, null
        )
    }
}