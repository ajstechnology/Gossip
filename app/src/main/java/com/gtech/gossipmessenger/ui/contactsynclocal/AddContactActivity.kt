package com.gtech.gossipmessenger.ui.contactsynclocal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat.inflate
import com.gtech.gossipmessenger.databinding.ActivityAddcontactsBinding


class AddContactActivity :AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =ActivityAddcontactsBinding.inflate(layoutInflater)

        setContentView(binding.root)




    }
}