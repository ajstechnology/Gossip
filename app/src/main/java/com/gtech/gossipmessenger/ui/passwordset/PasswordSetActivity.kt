package com.gtech.gossipmessenger.ui.passwordset

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityPasswordSetBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.OTPModel
import com.gtech.gossipmessenger.ui.otp.OTPActivity
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.ui.signup.SignupRepo
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class PasswordSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordSetBinding
    private lateinit var dlg: AlertDialog

    private val viewModel: PasswordSetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tiPassword.doOnTextChanged { text, _, _, _ ->
            if (Utils.isValidPassword(text.toString())) {
                binding.password.isErrorEnabled = false
            } else {
                binding.password.isErrorEnabled = true
                binding.password.error =
                    getString(R.string.password_validation_desc)
            }
        }
        binding.previous.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        binding.tiConfirmPassword.doOnTextChanged { text, _, _, _ ->
            if (binding.tiPassword.text.toString() == text.toString()) {
                binding.confirmPassword.isErrorEnabled = false
            } else {
                binding.confirmPassword.isErrorEnabled = true
                binding.confirmPassword.error = "Password Mismatch"
            }
        }

        binding.btnSetnow.setOnClickListener {
            if (binding.tiPassword.text.toString().isNotEmpty()) {
                if (binding.tiPassword.text.toString() == binding.tiConfirmPassword.text.toString()) {
                    binding.confirmPassword.isErrorEnabled = false

                    if (Utils.isNetworkAvailable(this)) {
                        sendOTP()
                    } else {
                        Snackbar.make(
                            this,
                            binding.root,
                            getString(R.string.connection_error),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } else {
                    binding.confirmPassword.isErrorEnabled = true
                    binding.confirmPassword.error = "Password Mismatch"
                }
            } else {
                Utils.showPopup(this, "Password must not be blank")
            }

        }

        binding.singinText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun sendOTP() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@PasswordSetActivity)
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
            Utils.toModel(it, OTPModel::class.java).let {
                SignupRepo.mobOtp = it.mobOtp.toString()
                SignupRepo.otp = it.otp.toString()
                SignupRepo.password = binding.tiPassword.text.toString()
                startActivity(Intent(this@PasswordSetActivity, OTPActivity::class.java))
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
    }
}