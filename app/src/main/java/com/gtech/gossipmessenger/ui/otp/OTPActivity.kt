package com.gtech.gossipmessenger.ui.otp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityOtpBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.OTPModel
import com.gtech.gossipmessenger.models.SignupModel
import com.gtech.gossipmessenger.ui.setacct.SetAccountDetailActivity
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.ui.signup.SignupRepo
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private lateinit var dlg: AlertDialog

    private val viewModel: OTPViewModel by viewModels()

    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask

    private var time = 59

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.assocMobile.setText("+${SignupRepo.countrycode}${SignupRepo.mobile}")

        binding.singinText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        viewModel.timeString.observe(this, {
            binding.resendOtp.text = it
            if (it == "Resend OTP") {
                binding.resendOtp.setTextColor(resources.getColor(R.color.teal_200))
                binding.resendOtp.setTypeface(binding.resendOtp.typeface, Typeface.BOLD)
            } else {
                binding.resendOtp.setTextColor(resources.getColor(R.color.black))
                binding.resendOtp.setTypeface(binding.resendOtp.typeface, Typeface.NORMAL)
            }
        })

        binding.btnVerify.setOnClickListener {
            if (verifyOTP()) {
                if (Utils.isNetworkAvailable(this)) {
                    signUp()
                } else {
                    Snackbar.make(
                        this,
                        binding.root,
                        getString(R.string.connection_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Utils.showPopup(this, "Wrong OTP")
            }
        }

        binding.previous.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        startTimer()

        // Resend OTP
        binding.resendOtp.setOnClickListener {
            if (binding.resendOtp.text.toString() == "Resend OTP") {
                sendOTP()
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerTask = object : TimerTask() {
            override fun run() {
                val pattern = "00"
                viewModel.timeString.postValue("Resend in 00:${DecimalFormat(pattern).format(time)}")
                time--
                if (time < 0) {
                    stopTimer()
                    viewModel.timeString.postValue("Resend OTP")
                    time = 59
                }
            }
        }
        timer = Timer()
        timer.schedule(timerTask, 1000L, 1000L)
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun verifyOTP(): Boolean = SignupRepo.mobOtp == binding.etOtp.text.toString()
            || SignupRepo.otp == binding.etOtp.text.toString()

    private fun sendOTP() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@OTPActivity)
            .setView(loaderBinding.root).create()

        if (!dlg.isShowing) {
            dlg.show()
        }

        val payload = JSONObject()
        payload.put("email", SignupRepo.email)
        payload.put("mobile", SignupRepo.mobile)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.sentOTP(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapDataOTP(dataModel)
                    }
                    dlg.hide()
                }
                Resource.Status.LOADING -> {
                    if (!dlg.isShowing) {
                        dlg.show()
                    }
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                    dlg.hide()
                }
            }
        })
    }

    private fun wrapDataOTP(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            Utils.toModel(it, OTPModel::class.java).let {
                SignupRepo.mobOtp = it.mobOtp.toString()
                SignupRepo.otp = it.otp.toString()
            }
        }
    }

    private fun signUp() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@OTPActivity)
            .setView(loaderBinding.root).create()

        if (!dlg.isShowing) {
            dlg.show()
        }

        val payload = JSONObject()
        payload.put("username", SignupRepo.username)
        payload.put("email", SignupRepo.email)
        payload.put("mobile", SignupRepo.mobile)
        payload.put("password", SignupRepo.password)
        payload.put("country_code", SignupRepo.countrycode)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.signUp(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapData(dataModel)
                    }
                    dlg.hide()
                }
                Resource.Status.LOADING -> {
                    if (!dlg.isShowing) {
                        dlg.show()
                    }
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                    dlg.hide()
                }
            }
        })
    }

    private fun wrapData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let { it ->
            val signUpModel = Utils.toModel(it, SignupModel::class.java)
            signUpModel.let {
                if (it.status) {
                    SignupRepo.id = it.id.toString()
                    val dashIntent = Intent(
                        this@OTPActivity,
                        SetAccountDetailActivity::class.java
                    )
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    startActivity(dashIntent)
                    finish()
                } else {
                    Utils.showPopup(this, it.message)
                    dlg.dismiss()
                }
            }
        }
    }

    fun hideKeyboard(view: View) {
        Utils.hideKeyboard(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dlg.isInitialized) {
            dlg.dismiss()
        }
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}