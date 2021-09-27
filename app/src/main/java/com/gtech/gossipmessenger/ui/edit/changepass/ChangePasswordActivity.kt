package com.gtech.gossipmessenger.ui.edit.changepass

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityChangepasswordBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.ResetPassModel
import com.gtech.gossipmessenger.ui.forgotpass.ForgotPassActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber


@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangepasswordBinding

    private var canUpdatePassword = false
    private var oldPasswordMatch = false

    private val viewModel: ChangePasswordViewModel by viewModels()

    private var userId = 0
    private var localPass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangepasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    it.data?.let { signIn ->
                        localPass = signIn.user?.tu_password.toString()
                        userId = signIn.user?.tu_id!!
                    }
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })

        binding.previous.setOnClickListener {
            finish()
        }

        binding.currentPassword.doOnTextChanged { text, start, before, count ->
            oldPasswordMatch = text.toString() == AES.decrypt(localPass)
        }

        binding.newPassword.doOnTextChanged { text, start, before, count ->
            if (text.toString().length >= 8) {
                binding.newPasswordTil.isErrorEnabled = false
            } else {
                binding.newPasswordTil.isErrorEnabled = true
                binding.newPasswordTil.error = "Password minimum length 8 characters"
            }
        }

        binding.retypePassword.doOnTextChanged { text, start, before, count ->
            if (text.toString() == binding.newPassword.text.toString()) {
                canUpdatePassword = true
                binding.retypePasswordTil.isErrorEnabled = false
            } else {
                canUpdatePassword = false
                binding.retypePasswordTil.isErrorEnabled = true
                binding.retypePasswordTil.error = "Password Mismatch"
            }
        }
        binding.forgotTextLink.setOnClickListener {
            startActivity(Intent(this@ChangePasswordActivity, ForgotPassActivity::class.java))
            finish()
        }

        binding.btnUpdatePassword.setOnClickListener {
            if (oldPasswordMatch) {
                if (Utils.isNetworkAvailable(this)) {
                    if ((binding.newPassword.text.toString() == binding.retypePassword.text.toString()) && canUpdatePassword) {
                        if (!binding.newPassword.text.toString().isBlank()) {
                            updatePassword()
                        } else {
                            Utils.showPopup(this, "Please enter your new password")
                        }
                    } else {
                        Utils.showPopup(this, "Could not update your password.\nPlease try again.")
                    }
                } else {
                    Utils.showPopup(this, getString(R.string.connection_error))
                }
            } else {
                Utils.showPopup(this, "Your current password is wrong. \nPlease try again.")
            }
        }
    }

    private fun updatePassword() {

        if (binding.newPassword.text.toString().isBlank()) {
            Utils.showPopup(this, "Password should not be blank")
            return
        }
        val payload = JSONObject()
        payload.put("id", userId)
        payload.put("password", binding.newPassword.text.toString())
        Timber.i("updatePassword -> $payload")
        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.changePassword(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapChangePasswordData(dataModel)
                    }
                }
                Resource.Status.LOADING -> {
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                }
            }
        })
    }

    private fun wrapChangePasswordData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val changePassModel = Utils.toModel(it, ResetPassModel::class.java)
            if (changePassModel.status) {
                Toast.makeText(
                    this@ChangePasswordActivity,
                    changePassModel.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            } else {
                Utils.showPopup(this, changePassModel.message)
            }
        }
    }

    fun hideKeyboard(view: View) {
        Utils.hideKeyboard(this)
    }

}