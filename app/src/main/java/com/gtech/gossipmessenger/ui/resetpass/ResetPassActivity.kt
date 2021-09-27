package com.gtech.gossipmessenger.ui.resetpass

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityResetPassBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.ResetPassModel
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class ResetPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPassBinding
    private lateinit var dlg: AlertDialog
    private var otp = 0L
    private var fromMail: Boolean = false
    private val viewModel: ResetPassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otp = intent.getLongExtra("OTP", 0L)

        if (otp == 0L) {
            otp = intent.getLongExtra("EMAIL", 0L)
            fromMail = true
        }

        binding.signinText.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.previous.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                resetPass()
            } else {
                Snackbar.make(this, binding.root, "Not connected!", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.password.doOnTextChanged { text, _, _, _ ->
            if (Utils.isValidPassword(text.toString())) {
                binding.passwordTil.isErrorEnabled = false
            } else {
                binding.passwordTil.isErrorEnabled = true
                binding.passwordTil.error =
                    "Password must be 1 uppercase, 1 lowercase, 1 numeric, 1 special char and minimum length 8"
            }
        }

        binding.confirmPassword.doOnTextChanged { text, _, _, _ ->
            if (binding.password.text.toString() == text.toString()) {
                binding.confirmPasswordTil.isErrorEnabled = false
            } else {
                binding.confirmPasswordTil.isErrorEnabled = true
                binding.confirmPasswordTil.error = "Password Mismatch"
            }
        }
    }

    private fun resetPass() {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@ResetPassActivity)
            .setView(loaderBinding.root).create()

        if (!dlg.isShowing) {
            dlg.show()
        }

        if (binding.password.text.toString().isBlank()) {
            Utils.showPopup(this, "Password is required")
            return
        }

        if (!Utils.isValidPassword(binding.password.text.toString())) {
            Utils.showPopup(this, getString(R.string.password_validation_desc))
            return
        }

        val rootObj = JSONObject()
        if (fromMail) {
            rootObj.put("code", otp)

        } else {
            rootObj.put("otp", otp)
        }
        rootObj.put("password", binding.password.text.toString())

        Timber.i("resetPass: $rootObj")

        val data = JSONObject()
        data.put("data", AES.encrypt(rootObj.toString()))

        viewModel.resetPassword(Utils.toRequestBody(data)).observe(this, {
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
            val resetPassModel = Utils.toModel(it, ResetPassModel::class.java)
            if (resetPassModel.status) {
                startActivity(
                    Intent(
                        this@ResetPassActivity,
                        SignInActivity::class.java
                    )
                )
                finish()
            }
            Toast.makeText(
                this@ResetPassActivity,
                resetPassModel.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, SignInActivity::class.java))
        finish()
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