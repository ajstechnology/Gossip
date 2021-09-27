package com.gtech.gossipmessenger.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityDashboardBinding
import com.gtech.gossipmessenger.ui.call.CallFragment
import com.gtech.gossipmessenger.ui.camera.CameraFragment
import com.gtech.gossipmessenger.ui.home.HomeFragment
import com.gtech.gossipmessenger.ui.notification.NotificationFragment
import com.gtech.gossipmessenger.ui.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeFragment())

        binding.navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_camera -> {
                    replaceFragment(CameraFragment())
                    true
                }
                R.id.navigation_calls -> {
                    replaceFragment(CallFragment())
                    true
                }
                R.id.navigation_chat -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.navigation_notification -> {
                    replaceFragment(NotificationFragment())
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        ft.replace(binding.dashContainer.id, fragment)
        ft.commit()
    }
}