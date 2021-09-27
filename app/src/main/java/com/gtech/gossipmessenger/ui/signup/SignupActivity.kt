package com.gtech.gossipmessenger.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivitySignupBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.*
import com.gtech.gossipmessenger.ui.countries.CountriesActivity
import com.gtech.gossipmessenger.ui.passwordset.PasswordSetActivity
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.CountryFlags
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var dlg: AlertDialog
    private val GET_COUNTRY = 1001

    private val payload = JSONObject()

    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!::dlg.isInitialized) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(this@SignupActivity)
                .setView(loaderBinding.root).create()
        }

        binding.signinText.setOnClickListener {
            startActivity(Intent(this@SignupActivity, SignInActivity::class.java))
            finish()
        }

        binding.next.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding.btnSignup.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                checkUsername(
                    binding.etUserMob.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etMob.text.toString()
                )
            } else {
                Snackbar.make(this, binding.root, "Not connected!", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.tilMob.prefixText = "${CountryFlags.getCountryFlagByCountryCode("IN")} +91"
        payload.put("country_code", "91")
        binding.tilMob.prefixTextView.setOnClickListener {
            val getCountryIntent = Intent(this@SignupActivity, CountriesActivity::class.java)
            startActivityForResult(getCountryIntent, GET_COUNTRY)
        }
    }


    private fun checkUsername(username: String, email: String, mobile: String) {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@SignupActivity)
            .setView(loaderBinding.root).create()

        if (!dlg.isShowing) {
            dlg.show()
        }

        if (
            binding.etUserMob.text.toString().isBlank() ||
            binding.etEmail.text.toString().isBlank() ||
            binding.etMob.text.toString().isBlank()
        ) {
            Utils.showPopup(this, "All fields required")
            dlg.dismiss()
            return
        }

        if (!Utils.isValidEmail(binding.etEmail.text.toString())) {
            Utils.showPopup(this, getString(R.string.email_validation))
            dlg.dismiss()
            return
        }

        if (binding.etMob.text.toString().length < 10) {
            Utils.showPopup(this, getString(R.string.phone_validation))
            dlg.dismiss()
            return
        }

        if (binding.etMob.text.toString().startsWith("0")) {
            Utils.showPopup(this, getString(R.string.invalid_phone_validation))
            dlg.dismiss()
            return
        }

        if (binding.etUserMob.text.toString().length < 3) {
            Utils.showPopup(this, getString(R.string.username_validation))
            return
        }

        payload.put("username", username)
        payload.put("email", email)
        payload.put("mobile", mobile)

        Timber.i("signup payload -> $payload")

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.checkUsername(Utils.toRequestBody(data)).observe(this, {
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
            val signUpModel = Utils.toModel(it, CheckUsernameModel::class.java)

            signUpModel.let {
                if (it.status) {
                    SignupRepo.username = it.username.toString()
                    SignupRepo.email = it.email.toString()
                    SignupRepo.mobile = it.mobile.toString()
                    if (SignupRepo.countrycode.isEmpty()) {
                        SignupRepo.countrycode = "91"
                    }
                    startActivity(
                        Intent(
                            this@SignupActivity,
                            PasswordSetActivity::class.java
                        )
                    )
                    finish()
                } else {
                    Utils.showPopup(this, it.message.toString())
                }
            }
        }

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
                        SignupRepo.countrycode = countryDataItem.phonecode.toString()
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