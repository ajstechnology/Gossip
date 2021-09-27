package com.gtech.gossipmessenger.ui.deleteaccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityDeleteuseraccountBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.databinding.InputOtpBinding
import com.gtech.gossipmessenger.models.*
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class DeleteAccountActivity : AppCompatActivity() {
    private lateinit var dlg: AlertDialog
    private lateinit var binding: ActivityDeleteuseraccountBinding
    private val viewModel: DeleteAccountViewModel by viewModels()
    private var userId: Int = 0
    private lateinit var usernumber : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteuseraccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    it.data?.let { signIn ->
                        userId = signIn.user?.tu_id!!
                        usernumber=signIn.user?.tu_mobile!!
                         binding.deleMobilenumber.setText(signIn.user.tu_mobile)
                    }
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })

        binding.previous.setOnClickListener {
            finish()
        }
        binding.btnSaveMobilenumber.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                if(binding.deleMobilenumber.text.toString() == usernumber ) {
                    verifyPhone(binding.deleMobilenumber.text.toString())
                }
                else
                {
                    Utils.showPopup(this, "The Phone number you entered doesn't match your account's")
                }
            } else {
                Utils.showPopup(this, getString(R.string.connection_error))
            }
        }
    }


    private fun verifyPhone(mobile: String) {

        if (!(::dlg.isInitialized)) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(this@DeleteAccountActivity)
                .setView(loaderBinding.root).create()
            dlg.setCancelable(false)
        }

        if (mobile.isBlank()) {
            Utils.showPopup(this, "Please enter your mobile number to delete your account")
            return
        }

        if (mobile.length < 10) {
            Utils.showPopup(this, getString(R.string.phone_validation))
            return
        }

        if (mobile.startsWith("0")) {
            Utils.showPopup(this, getString(R.string.invalid_phone_validation))
            return
        }

        dlg.show()

        val payload = JSONObject()
        payload.put("mobile", mobile)
        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        Timber.i("verifyPhone: $data")

        viewModel.mobileVerification(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapMobileVerificationData(dataModel)
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

    private fun wrapMobileVerificationData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val verifyModel = Utils.toModel(it, CVerifyModel::class.java)
            if (verifyModel.status) {
                val inputOtp = InputOtpBinding.inflate(layoutInflater)
                AlertDialog.Builder(this@DeleteAccountActivity)
                    .setView(inputOtp.root)
                    .setTitle("Verify mobile")
                    .setMessage(verifyModel.message)
                    .setPositiveButton("Verify") { dialog, _ ->
                        if (inputOtp.inputOtp.text.toString() == verifyModel.otp.toString()) {
                            Utils.showPopup(this, "OTP Verified")
                            AlertDialog.Builder(this@DeleteAccountActivity)
                                .setIcon(R.drawable.logo)
                                .setTitle("Delete Account ?")
                                .setMessage("Are you sure you want to parmanently delete this account ?")
                                .setPositiveButton("Yes, Delete it") { dlg, id ->
                                    deleteAccount()
                                    dlg.dismiss()
                                }
                                .setNegativeButton("Cancel") { dlg, _ ->
                                    dlg.dismiss()
                                }
                                .create().show()

                        } else {
                            Utils.showPopup(this, "Wrong OTP")
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .setCancelable(false)
                    .create().show()

            } else {
                Utils.showPopup(this, verifyModel.message)

            }
        }
    }


    private fun deleteAccount() {
        val payload = JSONObject()
        payload.put("id", userId)
        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.deleteAccount(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapDeleteAccountData(dataModel)
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

    private fun wrapDeleteAccountData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val deleteAcctModel = Utils.toModel(it, ResetPassModel::class.java)
            if (deleteAcctModel.status) {
                Toast.makeText(
                    this@DeleteAccountActivity,
                    deleteAcctModel.message,
                    Toast.LENGTH_LONG
                ).show()

                logoutLocal()
            }
        }
    }

    private fun logoutLocal() {
        viewModel.logoutLocal(false).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val signInIntent = Intent(
                        this@DeleteAccountActivity,
                        SignInActivity::class.java
                    )
                    signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(
                        signInIntent
                    )
                }
                Resource.Status.LOADING -> {

                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                }
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