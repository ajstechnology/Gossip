package com.gtech.gossipmessenger.ui.edit.email

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityEditemailBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.databinding.InputOtpBinding
import com.gtech.gossipmessenger.models.*
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class EmailActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "EmailActivity"
    }

    private lateinit var binding: ActivityEditemailBinding
    private lateinit var dlg: AlertDialog

    private var userId = 0

    private val viewModel: EmailViewModel by viewModels()

    private var oldEmail: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditemailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    it.data?.let { signIn ->
                        signIn.user?.let { user ->
                            binding.apply {
                                binding.edtEmail.setText(user.tu_email)
                                user.tu_email?.length?.let { binding.edtEmail.setSelection(it) }
                                userId = user.tu_id!!
                                oldEmail = user.tu_email.toString()
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })

        binding.previous.setOnClickListener {
            finish()
        }

        binding.btnSaveEmail.setOnClickListener {
            if (binding.edtEmail.text.toString().isBlank()) {
                Utils.showPopup(this, "Please enter your email address")
            } else {
                if (Utils.isNetworkAvailable(this)) {
                    verifyEmail(binding.edtEmail.text.toString())
                } else {
                    Snackbar.make(this, binding.root, "Not connected!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun verifyEmail(email: String) {
        if (!(::dlg.isInitialized)) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(this@EmailActivity)
                .setView(loaderBinding.root).create()

            dlg.setCancelable(false)
        }

        if (email.isBlank()) {
            Utils.showPopup(this, "Email address should not be blank")
            return
        }

        if (!Utils.isValidEmail(email)) {
            Utils.showPopup(this, getString(R.string.email_validation))
            return
        }

        if (oldEmail == email) {
            Utils.showPopup(this, getString(R.string.old_email_address))
            return
        }
        dlg.show()
        val payload = JSONObject()
        payload.put("email", email)
        payload.put("id", userId)

        Timber.i("verifyEmail: $payload")

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.sendOTPEmailChange(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapOTPEmailChangeData(dataModel)
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

    private fun wrapOTPEmailChangeData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)
            ?.let {
                val verifyModel = Utils.toModel(it, CVerifyModel::class.java)
                if (verifyModel.status) {
                    val inputOtp = InputOtpBinding.inflate(layoutInflater)

                    AlertDialog.Builder(this@EmailActivity)
                        .setView(inputOtp.root)
                        .setTitle("Verify email")
                        .setMessage(verifyModel.message)
                        .setPositiveButton("Verify") { dialog, which ->
                            if (inputOtp.inputOtp.text.toString() == verifyModel.otp.toString()) {
                               // Utils.showPopup(this, "OTP Verified")
                                changeEmail(binding.edtEmail.text.toString())
                            } else {
                                Utils.showPopup(this, "Wrong OTP")
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                        .setCancelable(false)
                        .create().show()
                } else {
                    Utils.showPopup(this, verifyModel.message)
                }
            }
    }

    private fun changeEmail(email: String) {
        val payload = JSONObject()
        payload.put("email", email)
        payload.put("id", userId)

        Timber.i("changeEmail: $payload")
        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.updateEmail(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapUpdateEmailData(dataModel)
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

    private fun wrapUpdateEmailData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val emailVerify = Utils.toModel(it, ResetPassModel::class.java)
            if (emailVerify.status) {
                updateEmailLocal(binding.edtEmail.text.toString())
                Toast.makeText(
                    this,
                    emailVerify.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateEmailLocal(email: String) {
        viewModel.updateEmailLocal(email).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    finish()
                }
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })
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
