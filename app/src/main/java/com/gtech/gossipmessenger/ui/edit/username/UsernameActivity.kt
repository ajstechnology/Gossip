package com.gtech.gossipmessenger.ui.edit.username

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityEditusernameBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.ResetPassModel
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class UsernameActivity : AppCompatActivity() {
    private lateinit var dlg: AlertDialog
    private lateinit var binding: ActivityEditusernameBinding
    private val viewModel: UsernameViewModel by viewModels()
    private var userId = 0
    private lateinit var oldUsername: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditusernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    it.data?.let { signIn ->
                        signIn.user?.let { user ->
                            binding.apply {
                                oldUsername = user.tu_username.toString()
                                binding.etUserName.setText(user.tu_username)
                                user.tu_username?.length?.let { binding.etUserName.setSelection(it) }
                                userId = user.tu_id!!
                                if (user.can_change_username == false) {
                                    binding.etUserName.isEnabled = false
                                    binding.btnSaveUsername.isEnabled = false
                                }
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

        binding.btnSaveUsername.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                updateUserName()
            } else {
                Snackbar.make(
                    this,
                    binding.root,
                    getString(R.string.connection_error),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun updateUserName() {
        val payload = JSONObject()

        if (!(::dlg.isInitialized)) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(this@UsernameActivity)
                .setView(loaderBinding.root).create()

            dlg.setCancelable(false)
        }

        if (binding.etUserName.text.toString().isBlank()) {
            Utils.showPopup(this, "Username should not be blank")
            return
        }

        if (oldUsername == binding.etUserName.text.toString()) {
            Utils.showPopup(
                this,
                "You're entering your old username. Please try again with a new username"
            )
            return
        }

        dlg.show()

        payload.put("username", binding.etUserName.text.toString())
        payload.put("id", userId)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.changeUsername(Utils.toRequestBody(data)).observe(this, {
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
            val usernameModel = Utils.toModel(it, ResetPassModel::class.java)

            if (usernameModel.status) {
                updateUsernameLocal(binding.etUserName.text.toString())
                Toast.makeText(
                    this,
                    usernameModel.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
            {
                Utils.showPopup(this, usernameModel.message)
            }
        }
    }

    private fun updateUsernameLocal(username: String) {
        viewModel.changeUsernameLocal(username).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    viewModel.changeStatus(false, "msg")
                        .observe(this, {     // msg passed hard coded
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