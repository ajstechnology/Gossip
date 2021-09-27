package com.gtech.gossipmessenger.ui.synccontacts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityAccountBinding
import com.gtech.gossipmessenger.databinding.ActivitySynccontactBinding
import com.gtech.gossipmessenger.ui.dashboard.DashboardActivity
import com.gtech.gossipmessenger.ui.dashboard.DashboardActivity_GeneratedInjector
import com.gtech.gossipmessenger.ui.signup.SignupActivity
import com.gtech.gossipmessenger.utils.Utils

class SyncContacts:AppCompatActivity()
{
    private lateinit var binding: ActivitySynccontactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySynccontactBinding.inflate(layoutInflater)
        binding.btnSynccontacts.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
               //Permission set on contact
            } else {
                Utils.showPopup(this, getString(R.string.connection_error))
            }
        }
        binding.txtskip.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        setContentView(binding.root)
    }
}