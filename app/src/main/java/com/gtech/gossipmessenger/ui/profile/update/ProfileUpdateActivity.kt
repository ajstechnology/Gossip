package com.gtech.gossipmessenger.ui.profile.update

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityProfileUpdateBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.ResetPassModel
import com.gtech.gossipmessenger.ui.imageviewer.ImageViewerActivity
import com.gtech.gossipmessenger.ui.status.StatusActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class ProfileUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileUpdateBinding
    private lateinit var dlg: AlertDialog

    private val profileJson = JSONObject()

    private val viewModel: ProfileUpdateViewModel by viewModels()

    private val STATUS_REQUEST = 4001
    private var userId: Int = 0
    private var profileImg: String = ""
    private var coverImg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Profile Update");
          binding.previous.setOnClickListener {
            finish()
        }
        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                    it.data?.let { signIn ->
                        signIn.user?.let { user ->
                            binding.apply {
                                userId = user.tu_id!!
                                profileImg = "${signIn.img_url}${user.tu_profile_pic}"
                                coverImg = "${signIn.cover_pic_url}${user.tu_cover_pic}"
                                Glide.with(this@ProfileUpdateActivity)
                                    .load("${signIn.img_url}${user.tu_profile_pic}")
                                    .placeholder(
                                        ColorDrawable(
                                            ContextCompat.getColor(
                                                this@ProfileUpdateActivity,
                                                R.color.glidePlaceholder
                                            )
                                        )
                                    )
                                    .into(binding.profileImage)
                                Glide.with(this@ProfileUpdateActivity)
                                    .load("${signIn.cover_pic_url}${user.tu_cover_pic}")
                                    .placeholder(
                                        ColorDrawable(
                                            ContextCompat.getColor(
                                                this@ProfileUpdateActivity,
                                                R.color.glidePlaceholder
                                            )
                                        )
                                    )
                                    .into(binding.background)
                                binding.firstName.setText(user.tu_first_name)
                                user.tu_first_name?.length?.let { binding.firstName.setSelection(it) }
                                binding.lastName.setText(user.tu_last_name)
                                binding.userName.setText(user.tu_username)
                                binding.phone.setText(user.tu_mobile)
                                binding.email.setText(user.tu_email)
                                binding.statusEt.setText(user.tu_bio)
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })
        binding.statusEt.setOnClickListener {
            val statusActivity = Intent(this@ProfileUpdateActivity, StatusActivity::class.java)
            startActivityForResult(statusActivity, STATUS_REQUEST)
        }

        binding.back.setOnClickListener {
            finish()
        }
        binding.btnSaveNext.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                updateProfile()
            } else {
                Snackbar.make(
                    this,
                    binding.root,
                    "Not connected!",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }

        binding.profileImage.setOnClickListener {
            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.profileImage,
                "ImageViewer"
            )
            val imageIntent = Intent(this, ImageViewerActivity::class.java)
                .apply {
                    putExtra(
                        "IMG_PATH",
                        profileImg
                    )
                }
            startActivity(imageIntent, activityOptionsCompat.toBundle())
        }

        binding.background.setOnClickListener {
            val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.background,
                "ImageViewer"
            )
            val imageIntent = Intent(this, ImageViewerActivity::class.java)
                .apply {
                    putExtra(
                        "IMG_PATH",
                        coverImg
                    )
                }
            startActivity(imageIntent, activityOptionsCompat.toBundle())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            STATUS_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    binding.statusEt.setText(data.getStringExtra("STATUS_RESULT"))
                }
            }
        }
    }

    private fun updateProfile() {
        if (!(::dlg.isInitialized)) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(this@ProfileUpdateActivity)
                .setView(loaderBinding.root).create()

            dlg.setCancelable(false)
        }


        if (binding.firstName.text.toString().isBlank()) {
            Utils.showPopup(this, "First name should not be blank")
            return
        }

        if (binding.firstName.text.toString().length < 3) {
            Utils.showPopup(this, "First name should between 3-20 characters")
            return
        }

        if (binding.lastName.text.toString().isBlank()) {
            Utils.showPopup(this, "Last name should not be blank")
            return
        }

        if (binding.lastName.text.toString().length < 3) {
            Utils.showPopup(this, "Last name should between 3-20 characters")
            return
        }

        dlg.show()

        profileJson.put("id", userId.toString())
        profileJson.put("first_name", binding.firstName.text.toString())
        profileJson.put("last_name", binding.lastName.text.toString())
        profileJson.put("status", binding.statusEt.text.toString())


        Timber.i("updateProfile: profileJson $profileJson")

        val data = JSONObject()
        data.put("data", AES.encrypt(profileJson.toString()))

        viewModel.updateProfile(Utils.toRequestBody(data)).observe(this, {
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
                updateProfileLocal(
                    binding.firstName.text.toString(),
                    binding.lastName.text.toString(),
                    binding.statusEt.text.toString()
                )
                Toast.makeText(
                    this@ProfileUpdateActivity,
                    usernameModel.message,
                    Toast.LENGTH_SHORT
                )
                    .show()

                finish()
            }
        }
    }

    private fun updateProfileLocal(firstName: String, lastName: String, bio: String) {
        viewModel.updateProfileLocal(firstName, lastName, bio).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${it.data}")
                }
                Resource.Status.LOADING -> {
                    Timber.i("loading...")
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
