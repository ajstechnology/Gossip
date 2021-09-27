package com.gtech.gossipmessenger.ui.contactsynclocal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.databinding.ActivityContactBinding
import com.gtech.gossipmessenger.databinding.ActivityContactinfoBinding

class ContactNumberSync :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtCreatenewcontact.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }
}