package com.gtech.gossipmessenger.ui.archive

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityAccountBinding
import com.gtech.gossipmessenger.databinding.ActivityArchiveBinding
import com.gtech.gossipmessenger.databinding.ActivitySchedulemsgBinding
import com.gtech.gossipmessenger.databinding.ActivitySynccontactBinding
import com.gtech.gossipmessenger.ui.dashboard.DashboardActivity
import com.gtech.gossipmessenger.ui.dashboard.DashboardActivity_GeneratedInjector
import com.gtech.gossipmessenger.ui.signup.SignupActivity
import com.gtech.gossipmessenger.utils.Utils

class ArchiveContacts:AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}