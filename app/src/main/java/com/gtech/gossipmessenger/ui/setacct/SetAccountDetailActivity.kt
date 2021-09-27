package com.gtech.gossipmessenger.ui.setacct

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.gtech.gossipmessenger.data.entities.SignIn
import com.gtech.gossipmessenger.data.entities.User
import com.gtech.gossipmessenger.databinding.ActivitySetAccountDetailsBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.fragments.ImageBrowserBottomSheet
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.SetAcctModel
import com.gtech.gossipmessenger.models.SignInModel
import com.gtech.gossipmessenger.ui.imageviewer.Gallery
import com.gtech.gossipmessenger.ui.signup.SignupRepo
import com.gtech.gossipmessenger.ui.synccontacts.SyncContacts
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.FileUtils
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


@AndroidEntryPoint
class SetAccountDetailActivity : AppCompatActivity(), ImageBrowserBottomSheet.ItemClickListener {

    private lateinit var binding: ActivitySetAccountDetailsBinding
    private val storageReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val storagePermission = 1021
    private val fileRequest = 1022
    val PICKER_REQUEST_CODE = 30
    private val cameraPermission = Manifest.permission.CAMERA
    private val cameraPermissionRequest = 2021
    private val CAMERA_REQUEST = 2022
    private val galleryRequest = 2023

    private val payload = JSONObject()
    private lateinit var dlg: AlertDialog

    private val viewModel: SetAccountViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this@SetAccountDetailActivity)
            .setView(loaderBinding.root).create()

        binding.profileImageChange.setOnClickListener {
            ImageBrowserBottomSheet(this).show(supportFragmentManager, "ImageBrowser")
        }

        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }

        binding.btnSkip.setOnClickListener {
            signIn(SignupRepo.username, SignupRepo.password)
        }
    }


    private fun signIn(username: String, password: String) {
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
                        wrapSignInData(dataModel)
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

    private fun wrapSignInData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val signInModel = Utils.toModel(it, SignInModel::class.java)
            if (signInModel.status) {
                // Saving data to local prefs
                saveUserDataToLocal(signInModel)
            } else {
                Toast.makeText(
                    this@SetAccountDetailActivity,
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
                Resource.Status.SUCCESS -> {
                    val dashboardIntent = Intent(
                        this@SetAccountDetailActivity, SyncContacts::class.java
                    )

                    dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(
                        dashboardIntent
                    )
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })
    }

    fun updateProfile() {

        if (binding.firstName.text.toString().isBlank() && binding.lastName.text.toString()
                .isBlank()
        ) {
            Utils.showPopup(this, "All fields required")
            return
        }

        if (binding.firstName.text.toString().length < 3) {
            Utils.showPopup(this, "First name should between 3-20 characters")
            return
        }

        if (binding.lastName.text.toString().length < 3) {
            Utils.showPopup(this, "Last name should between 3-20 characters")
            return
        }

        if (!payload.has("profile_pic")) {
            payload.put("profile_pic", "")
        }

        if (!dlg.isShowing) {
            dlg.show()
        }

        payload.put("first_name", binding.firstName.text.toString())
        payload.put("last_name", binding.lastName.text.toString())
        payload.put("id", SignupRepo.id)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.setAccountDetails(Utils.toRequestBody(data)).observe(this, {
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
                val acctModel = Utils.toModel(it, SetAcctModel::class.java)
                if (acctModel.status) {
                    signIn(SignupRepo.username, SignupRepo.password)
                } else {
                    Toast.makeText(
                        this@SetAccountDetailActivity,
                        acctModel.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun accessLocalStorage() {
        // val intent = Intent()
        //intent.setType("image/*")
        //intent.setAction(Intent.ACTION_GET_CONTENT)
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), fileRequest)
        try
        {
            ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setRootDirectoryName("Image Picker")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setToolbarColor("#02688e")
                .setBackgroundColor("#ffffff")
                .setShowCamera(false)
                .setLimitMessage("You can select up to 1 images")
                .setRequestCode(PICKER_REQUEST_CODE)
                .start();
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun accessCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            storagePermission -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessLocalStorage()
                } else {
                    Toast.makeText(
                        this@SetAccountDetailActivity,
                        "Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            cameraPermissionRequest -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessCameraIntent()
                } else {
                    Toast.makeText(
                        this@SetAccountDetailActivity,
                        "Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            fileRequest -> {
                if (resultCode == RESULT_OK && data != null) {
                    galleryRequest(data.data!!,"Camera")
                }
            }
            CAMERA_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    val fileUri = getFileUri(data.extras?.get("data") as Bitmap)
                    galleryRequest(fileUri,"Camera")
                }
            }
            PICKER_REQUEST_CODE -> {
                val images: ArrayList<Image> = ImagePicker.getImages(data)
                for (image in images)
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        galleryRequest(image.uri,"Gallery")
                    }
                    else
                    {
                        val fileUri = Uri.parse(image.path)
                        galleryRequest(fileUri,"Gallery")

                    }
                }
            }
            galleryRequest -> {
                if (resultCode == RESULT_OK && data != null) {
                    val fileUri = data.extras?.get("file_uri") as String
                    Glide.with(this@SetAccountDetailActivity).load(fileUri)
                        .into(binding.profileImg)
                    val encoded: String? = Utils.encodeToBase64(fileUri)
                    payload.put("profile_pic", encoded)
                }
            }
        }
    }

    private fun getFileUri(bitmap: Bitmap): Uri {
        val path = this@SetAccountDetailActivity.getExternalFilesDir(null)?.absolutePath
        val file = File(path, "${UUID.randomUUID()}.png")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved image path to uri
        return FileUtils.getUri(file)!!
    }

    override fun onItemClick(position: Int) {
        Timber.i("onItemClick: $position")
        when (position) {
            0 -> {
                // camera
                if (!checkCameraPermission()) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(cameraPermission),
                        cameraPermissionRequest
                    )
                } else {
                    // Access camera intent
                    accessCameraIntent()
                }
            }
            1 -> {
                // Gallery
                if (!checkRequiredPermission()) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(storageReadPermission, storageReadPermission),
                        storagePermission
                    )
                } else {
                    // access to local storage
                    accessLocalStorage()
                }
            }
            2 -> {
                // Delete Photo

            }
        }
    }

    private fun checkRequiredPermission(): Boolean = ContextCompat.checkSelfPermission(
        this@SetAccountDetailActivity,
        storageReadPermission
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkCameraPermission(): Boolean = ContextCompat.checkSelfPermission(
        this@SetAccountDetailActivity,
        cameraPermission
    ) == PackageManager.PERMISSION_GRANTED

    private fun galleryRequest(data: Uri,type:String) {
        val galleryIntent = Intent(this@SetAccountDetailActivity, Gallery::class.java)
        galleryIntent.putExtra("img", data)
        galleryIntent.putExtra(
            "IMG_TYPE",
            "profile"
        )
        galleryIntent.putExtra("FROM_TYPE", type)
        startActivityForResult(galleryIntent, galleryRequest)
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