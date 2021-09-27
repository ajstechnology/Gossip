package com.gtech.gossipmessenger.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.data.entities.SignIn
import com.gtech.gossipmessenger.data.entities.User
import com.gtech.gossipmessenger.databinding.ActivitySigninBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.SignInModel
import com.gtech.gossipmessenger.ui.dashboard.DashboardActivity
import com.gtech.gossipmessenger.ui.forgotpass.ForgotPassActivity
import com.gtech.gossipmessenger.ui.signup.SignupActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

import com.gtech.gossipmessenger.databinding.ButtomSheetLayerBinding

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var dlg: AlertDialog
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignin.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                signIn(binding.etUserMob.text.toString(), binding.etPass.text.toString())
            } else {
                Utils.showPopup(this, getString(R.string.connection_error))
            }
        }

        binding.next.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.singupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassActivity::class.java))
            finish()
        }
    }

    private fun signIn(username: String, password: String) {
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@SignInActivity)
            .setView(loaderBinding.root).create()

        if (username.isBlank()) {
            Utils.showPopup(
                this,
                "Please enter Email / Username / Phone number"
            )
            return
        }

        if (password.isBlank()) {
            Utils.showPopup(
                this,
                "Please enter password"
            )
            return
        }

        if (username.length < 3) {
            Utils.showPopup(this, "Username must be between 3-20 characters")
            return
        }


        val payload = JSONObject()
        payload.put("username", username)
        payload.put("password", password)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.signIn(Utils.toRequestBody(data)).observe(this, {
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
            val signInModel = Utils.toModel(it, SignInModel::class.java)
            if (signInModel.status) {
                // Saving data to local prefs
                saveUserDataToLocal(signInModel)
                Toast.makeText(
                    this@SignInActivity,
                    signInModel.message,
                    Toast.LENGTH_SHORT
                ).show()
                binding.etUserMob.text?.clear()
                binding.etPass.text?.clear()
                startActivity(
                    Intent(
                        this@SignInActivity,
                        DashboardActivity::class.java
                    )
                )
                finish()
            } else {
                Toast.makeText(
                    this@SignInActivity,
                    signInModel.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveUserDataToLocal(signInModel: SignInModel) {
        var user: User? = null
        signInModel.userData?.let {
            user = User(
                it.tuId,
                it.tuUsername,
                it.tuEmail,
                it.tuMobile,
                it.tuFirstName,
                it.tuLastName,
                it.tuProfilePic,
                it.tuStatus,
                it.tuIsDeleted,
                it.tuToken,
                it.tuCoverPic,
                it.tuBio,
                it.tuCity,
                it.tuGender,
                it.tuBirthDate.toString(),
                it.tuRandomString,
                it.tuPassword,
                it.tuIsVerify,
                it.tuOtp.toString(),
                it.tuState,
                it.tuCountry,
                it.tuCountryCode,
                it.canChangeUsername,
                it.changeUsernameMessage
            )
        }
        val signIn = SignIn(
            1,
            signInModel.status,
            signInModel.message,
            signInModel.imageUrl,
            signInModel.coverPicUrl,
            user
        )

        viewModel.saveData(signIn).observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> Timber.i("data -> ${it.data}")
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