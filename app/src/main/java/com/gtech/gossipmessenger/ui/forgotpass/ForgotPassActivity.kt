package com.gtech.gossipmessenger.ui.forgotpass

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityForgotPasswordBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.ForgotPassModel
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.ui.verifyotp.OTPVerifyActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class ForgotPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var dlg: AlertDialog
    private val viewModel: ForgotPassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSubmit.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                forgotPass()
            } else {
                Snackbar.make(this, binding.root, "Not connected!", Snackbar.LENGTH_LONG).show()
            }
        }
        binding.signinText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        binding.previous.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun forgotPass() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@ForgotPassActivity)
            .setView(loaderBinding.root).create()

        if (binding.mobile.text.toString().isBlank() && binding.email.text.toString().isBlank()) {
            Utils.showPopup(this, "Please enter your email address or mobile number")
            return
        }

        if (binding.mobile.text.toString().isNotBlank()) {
            if (binding.mobile.text.toString().length < 10) {
                Utils.showPopup(this, getString(R.string.phone_validation))
                return
            }

            if (binding.mobile.text.toString().startsWith("0")) {
                Utils.showPopup(this, getString(R.string.invalid_phone_validation))
                return
            }
        }

        if (binding.email.text.toString().isNotBlank()) {
            if (!Utils.isValidEmail(binding.email.text.toString())) {
                Utils.showPopup(this, getString(R.string.email_validation))
                return
            }
        }

        if (!dlg.isShowing) {
            dlg.show()
        }

        val rootObj = JSONObject()

        rootObj.put("mobile", binding.mobile.text.toString())
        rootObj.put("email", binding.email.text.toString())

        Timber.i("forgotPass: $rootObj")

        val data = JSONObject()
        data.put("data", AES.encrypt(rootObj.toString()))

        viewModel.forgotPassword(Utils.toRequestBody(data)).observe(this, {
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
        AES.decrypt(dataModel.data)
            ?.let {
                val forgotPassModel = Utils.toModel(it, ForgotPassModel::class.java)
                if (forgotPassModel.status) {
                    startActivity(
                        Intent(
                            this@ForgotPassActivity,
                            OTPVerifyActivity::class.java
                        )
                    )
                    finish()
                }
                Utils.showPopup(this, forgotPassModel.message)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dlg.isInitialized) {
            dlg.dismiss()
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
}