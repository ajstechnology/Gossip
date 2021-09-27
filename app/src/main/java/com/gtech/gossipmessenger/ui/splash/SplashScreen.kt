package com.gtech.gossipmessenger.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.databinding.SplashScreenBinding
import com.gtech.gossipmessenger.ui.dashboard.DashboardActivity
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule


@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    private var timer = Timer()
    private lateinit var binding: SplashScreenBinding
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getDefaultUser.observe(this@SplashScreen, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data?.status == true) {
                            timer.schedule(1000) {
                            startActivity(Intent(this@SplashScreen, DashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        timer.schedule(1000) {
                            startActivity(Intent(this@SplashScreen, SignInActivity::class.java))
                            finish()
                        }
                    }
                }
                Resource.Status.LOADING -> {

                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                    timer.schedule(3000) {
                        startActivity(Intent(this@SplashScreen, SignInActivity::class.java))
                        finish()
                    }
                }
            }
        })


    }

    override fun onPause() {
        timer.cancel()
        super.onPause()
    }
}