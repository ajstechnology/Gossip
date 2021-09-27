package com.gtech.gossipmessenger.ui.edit.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityEditmobilenumberBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.databinding.InputOtpBinding
import com.gtech.gossipmessenger.models.*
import com.gtech.gossipmessenger.ui.countries.CountriesActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.CountryFlags
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class MobileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditmobilenumberBinding
    private lateinit var dlg: AlertDialog

    private val GET_COUNTRY = 1001

    private val payload = JSONObject()

    private val viewModel: MobileViewModel by viewModels()

    private var userId = 0

    private var oldMobileNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditmobilenumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@MobileActivity)
            .setView(loaderBinding.root).create()

        dlg.setCancelable(false)

        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    it.data?.let { signIn ->
                        signIn.user?.let { user ->
                            binding.apply {
                                binding.etMobilenumber.setText(user.tu_mobile)
                                user.tu_mobile?.length?.let { binding.etMobilenumber.setSelection(it) }
                                userId = user.tu_id!!
                                oldMobileNumber = user.tu_mobile.toString()
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })

        binding.tilMob.prefixText = "${CountryFlags.getCountryFlagByCountryCode("IN")} +91"
        payload.put("country_code", "91")
        binding.tilMob.prefixTextView.setOnClickListener {
            val getCountryIntent = Intent(this@MobileActivity, CountriesActivity::class.java)
            startActivityForResult(getCountryIntent, GET_COUNTRY)
        }

        binding.previous.setOnClickListener {
            finish()
        }
        binding.btnSaveMobilenumber.setOnClickListener {
            if (binding.etMobilenumber.text.toString().isBlank()) {
                Utils.showPopup(this, "Please enter your Mobile number")
            } else {
                if (Utils.isNetworkAvailable(this)) {
                    verifyPhone(binding.etMobilenumber.text.toString())
                } else {
                    Snackbar.make(this, binding.root, "Not connected!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun verifyPhone(mobile: String) {

        if (!(::dlg.isInitialized)) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(this@MobileActivity)
                .setView(loaderBinding.root).create()

            dlg.setCancelable(false)
        }

        if (mobile.isBlank()) {
            Utils.showPopup(this, "Mobile number should not be blank")
            return
        }

        if (mobile.length < 10 || mobile.startsWith("0")) {
            Utils.showPopup(this, getString(R.string.invalid_phone_validation))
            return
        }

        if (oldMobileNumber == mobile) {
            Utils.showPopup(this, getString(R.string.old_mobile_number))
            return
        }

        dlg.show()

        payload.put("mobile", mobile)
        payload.put("id", userId)

        Timber.i("update phone payload -> $payload")

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        Timber.i("verifyPhone: $data")

        viewModel.sendOTPMobileChange(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapSendOtpData(dataModel)
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

    private fun wrapSendOtpData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)
            ?.let {
                val verifyModel = Utils.toModel(it, CVerifyModel::class.java)
                if (verifyModel.status) {
                    val inputOtp = InputOtpBinding.inflate(layoutInflater)

                    AlertDialog.Builder(this@MobileActivity)
                        .setView(inputOtp.root)
                        .setTitle("Verify mobile")
                        .setMessage(verifyModel.message)
                        .setPositiveButton("Verify") { dialog, which ->
                            if (inputOtp.inputOtp.text.toString() == verifyModel.otp.toString()) {
                                changeMobileNumber(binding.etMobilenumber.text.toString())
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

    private fun changeMobileNumber(mobile: String) {
        val payload = JSONObject()
        payload.put("mobile", mobile)
        payload.put("id", userId)
       payload.put("country_code", "91")

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.updateMobile(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapUpdateMobileData(dataModel)
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

    private fun wrapUpdateMobileData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val mobileverify = Utils.toModel(it, ResetPassModel::class.java)
            if (mobileverify.status) {
                updateMobileLocal(binding.etMobilenumber.text.toString())
               // Utils.showPopup(this, mobileverify.message)
                Toast.makeText(
                    this,
                    mobileverify.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateMobileLocal(mobile: String) {
        viewModel.updateMobileLocal(mobile).observe(this, {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GET_COUNTRY -> {
                if (resultCode == RESULT_OK && data != null) {
                    val countryDataItem = data.getParcelableExtra<CountryDataItem>("country")
                    if (countryDataItem != null) {
                        binding.tilMob.prefixText = "${
                            countryDataItem.sortname.let {
                                CountryFlags.getCountryFlagByCountryCode(
                                    it
                                )
                            }
                        } +${countryDataItem.phonecode}"

                        payload.put("country_code", countryDataItem.phonecode)
                    }
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
    }
}