package com.gtech.gossipmessenger.ui.verifyotp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.databinding.ActivityVerifyOtpBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.OTPVerifyModel
import com.gtech.gossipmessenger.ui.resetpass.ResetPassActivity
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class OTPVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var dlg: AlertDialog
    private var isForMail: Boolean = false

    private val viewModel: OTPVerifyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.data?.encodedFragment?.let {
            isForMail = true
            binding.otp.setText(intent.data?.encodedFragment?.substringAfter("="))
            binding.otp.setSelection(binding.otp.text.toString().length)
        }

        binding.btnVerify.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                if (isForMail) {
                    verifyCode()
                } else {
                    verifyOTP()
                }
            } else {
                Snackbar.make(this, binding.root, "Not connected!", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.previous.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.signinText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun verifyOTP() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@OTPVerifyActivity)
            .setView(loaderBinding.root).create()

        if (!dlg.isShowing) {
            dlg.show()
        }

        if (binding.otp.text.toString().isBlank()) {
            Utils.showPopup(this, "Please give the OTP")
            dlg.dismiss()
            return
        }

        val rootObj = JSONObject()
        rootObj.put("otp", binding.otp.text.toString())

        val data = JSONObject()
        data.put("data", AES.encrypt(rootObj.toString()))

        viewModel.checkForgotPasswordOTP(Utils.toRequestBody(data)).observe(this, {

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
        AES.decrypt(dataModel.data)?.let {
            val otpVerifyModel = Utils.toModel(
                it,
                OTPVerifyModel::class.java
            )
            if (otpVerifyModel.status) {
                startActivity(
                    Intent(
                        this@OTPVerifyActivity,
                        ResetPassActivity::class.java
                    ).apply {
                        putExtra("OTP", binding.otp.text.toString().toLong())
                    }
                )
                finish()
            }
        }
    }

    private fun verifyCode() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@OTPVerifyActivity)
            .setView(loaderBinding.root).create()

        if (!dlg.isShowing) {
            dlg.show()
        }

        if (binding.otp.text.toString().isBlank()) {
            Utils.showPopup(this, "Please give the OTP")
            dlg.dismiss()
            return
        }

        val rootObj = JSONObject()
        rootObj.put("code", binding.otp.text.toString())

        println(rootObj)

        val data = JSONObject()
        data.put("data", AES.encrypt(rootObj.toString()))

        viewModel.checkForgotPassword(Utils.toRequestBody(data)).observe(this, {

            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapDataCode(dataModel)
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

    private fun wrapDataCode(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val otpVerifyModel = Utils.toModel(
                it,
                OTPVerifyModel::class.java
            )
            if (otpVerifyModel.status) {
                startActivity(
                    Intent(
                        this@OTPVerifyActivity,
                        ResetPassActivity::class.java
                    ).apply {
                        if (isForMail) {
                            putExtra("EMAIL", binding.otp.text.toString().toLong())
                        } else {
                            putExtra("OTP", binding.otp.text.toString().toLong())
                        }
                    }
                )
                finish()
            }
        }
    }

    fun hideKeyboard(view: View) {
        Utils.hideKeyboard(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dlg.isInitialized) {
            dlg.dismiss()
        }
    }
}