package com.gtech.gossipmessenger.ui.profile

import android.Manifest
import android.R.attr.thumbnail
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import com.gtech.gossipmessenger.databinding.FragmentProfileBinding
import com.gtech.gossipmessenger.databinding.ItemProfileOptionsBinding
import com.gtech.gossipmessenger.fragments.ImageBrowserBottomSheet
import com.gtech.gossipmessenger.models.CoverPicModel
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.ProfilePicModel
import com.gtech.gossipmessenger.models.ResetPassModel
import com.gtech.gossipmessenger.ui.account.AccountActivity
import com.gtech.gossipmessenger.ui.imageviewer.Gallery
import com.gtech.gossipmessenger.ui.imageviewer.ImageViewerActivity
import com.gtech.gossipmessenger.ui.signin.SignInActivity
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.FileUtils
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.util.*


@AndroidEntryPoint
class ProfileFragment : Fragment(), ImageBrowserBottomSheet.ItemClickListener {

    lateinit var binding: FragmentProfileBinding

    private val storageReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val storagePermission = 1021
    private val fileRequest = 1022

    private val cameraPermission = Manifest.permission.CAMERA
    private val cameraPermissionRequest = 2021
    private val CAMERA_REQUEST = 2022
    private val galleryRequest = 2023

    private val storageWritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private var currentPicture = "profile"
    lateinit var imageUri:Uri
    private val viewModel: ProfileViewModel by viewModels()

    private var userId: Int = 0
    private var profileImg: String = ""
    private var coverImg: String = ""

    private var profileImgFile: String = ""
    private var coverImgFile: String = ""
    val PICKER_REQUEST_CODE = 30

    private lateinit var dlg: AlertDialog

    private var isDelete = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        if (!(::dlg.isInitialized)) {
            val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
            dlg = AlertDialog.Builder(requireActivity())
                .setView(loaderBinding.root).create()

            dlg.setCancelable(false)
        }


        viewModel.getDefaultUser.observe(viewLifecycleOwner, {
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

                                profileImgFile = "${user.tu_profile_pic}"
                                coverImgFile = "${user.tu_cover_pic}"

                                userFlName.text = "${user.tu_first_name} ${user.tu_last_name}"
                                userName.text = "@${user.tu_username}"
                                status.text = user.tu_bio
                                Glide.with(requireActivity())
                                    .load("${signIn.cover_pic_url}${user.tu_cover_pic}")
                                    .placeholder(
                                        ColorDrawable(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.glidePlaceholder
                                            )
                                        )
                                    )
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(binding.coverPicture)
                                Glide.with(requireActivity())
                                    .load("${signIn.img_url}${user.tu_profile_pic}")
                                    .placeholder(
                                        ColorDrawable(
                                            ContextCompat.getColor(
                                                requireActivity(),
                                                R.color.glidePlaceholder
                                            )
                                        )
                                    )
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(binding.profileImage)
                            }
                        }
                    }
                }
                Resource.Status.ERROR -> Timber.e("error -> ${it.message}")
            }
        })

        binding.editCover.setOnClickListener {
            ImageBrowserBottomSheet(this).show(childFragmentManager, "ImageBrowser")
            currentPicture = "cover"
        }

        binding.coverPicture.setOnClickListener {

            if (coverImgFile.isNotEmpty()) {
                val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    binding.coverPicture,
                    "ImageViewer"
                )
                val imageIntent = Intent(requireActivity(), ImageViewerActivity::class.java)
                    .apply {
                        putExtra(
                            "IMG_PATH",
                            coverImg
                        )
                    }
                startActivity(imageIntent, activityOptionsCompat.toBundle())
            }
        }

        binding.profileImage.setOnClickListener {

            if (profileImgFile.isNotEmpty()) {
                val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    binding.profileImage,
                    "ImageViewer"
                )

                val imageIntent = Intent(requireActivity(), ImageViewerActivity::class.java)
                    .apply {
                        putExtra(
                            "IMG_PATH",
                            profileImg
                        )
                    }
                startActivity(imageIntent, activityOptionsCompat.toBundle())
            }
        }

        binding.editProfileImage.setOnClickListener {
            ImageBrowserBottomSheet(this).show(childFragmentManager, "ImageBrowser")
            currentPicture = "profile"
        }

        val listAdapter = ListAdapter(
            listOf(
                ItemModel(Color.parseColor("#e2b80a"), R.drawable.ic_user, "Account"),
                ItemModel(
                    Color.parseColor("#e21b1b"),
                    R.drawable.ic_webpage,
                    "Gossip Web"
                ),
                ItemModel(Color.parseColor("#ffa200"), R.drawable.ic_privacy, "Favourite Message"),
                ItemModel(Color.parseColor("#ce26d3"), R.drawable.ic_privacy, "Privacy"),
                ItemModel(Color.parseColor("#59d337"), R.drawable.ic_chat, "Chat"),
                ItemModel(Color.parseColor("#4d3edd"), R.drawable.ic_notification, "Notification"),
                ItemModel(Color.parseColor("#29d3ef"), R.drawable.ic_invitation, "Invite"),
                ItemModel(Color.parseColor("#4aa879"), R.drawable.ic_information, "Help"),
                ItemModel(Color.parseColor("#7676b7"), R.drawable.ic_transfer, "Data & Storage"),
                ItemModel(
                    Color.parseColor("#158e9b"),
                    R.drawable.ic_lock_chat,
                    "Lock Chat Settings"
                ),
                ItemModel(Color.parseColor("#1655c4"), R.drawable.ic_logout, "Web Logout", false),
                ItemModel(Color.parseColor("#97cc15"), R.drawable.ic_logout, "Logout", false),
            )
        )
        binding.profileOptions.adapter = listAdapter

        binding.profileOptions.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    startActivity(Intent(requireActivity(), AccountActivity::class.java))
                }
                11 -> {
                    if (Utils.isNetworkAvailable(requireActivity())) {
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Logout")
                            .setMessage("Are you sure want to logout?")
                            .setPositiveButton("Logout") { dialog, _ ->
                                logout()
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                            .create().show()
                    } else {
                        Snackbar.make(
                            requireActivity(),
                            binding.root,
                            getString(R.string.connection_error),
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }

        return binding.root
    }

    private fun logout() {
        val payload = JSONObject()
        payload.put("id", userId)
        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.logout(Utils.toRequestBody(data)).observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapLogoutData(dataModel)
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

    private fun wrapLogoutData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val logoutModel = Utils.toModel(it, ResetPassModel::class.java)

            if (logoutModel.status) {
                Toast.makeText(
                    requireActivity(),
                    logoutModel.message,
                    Toast.LENGTH_SHORT
                ).show()
                logoutLocal(false)
            }

        }
    }

    private fun logoutLocal(status: Boolean) {
        viewModel.logoutLocal(status).observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    startActivity(Intent(requireActivity(), SignInActivity::class.java))
                    requireActivity().finish()
                }
                Resource.Status.LOADING -> {

                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                }
            }
        })
    }

    inner class ListAdapter(val items: List<ItemModel>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemBinding = ItemProfileOptionsBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
            itemBinding.itemText.text = items[position].title
            itemBinding.itemIcon.setImageResource(items[position].drawable)
            itemBinding.itemIcon.setColorFilter(items[position].color)
            if (!items[position].itemForward) {
                itemBinding.itemForeward.visibility = View.INVISIBLE
            }
            return itemBinding.root
        }

    }

    private fun accessLocalStorage() {
        //  val intent = Intent()
        // intent.setType("image/*")
        //intent.setAction(Intent.ACTION_GET_CONTENT)
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), fileRequest)
        try
        {
            /* GligarPicker().limit(1).disableCamera(false).requestCode(
                 PICKER_REQUEST_CODE
             ).withFragment(this).show()*/
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
        catch (e: Exception)
        {
            e.printStackTrace()
        }


    }

    private fun accessCameraIntent() {
        /*  val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          startActivityForResult(cameraIntent, CAMERA_REQUEST)
  */
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = this.requireActivity().getContentResolver().insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            fileRequest -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    galleryRequest(data.data!!, "Camera")
                }
            }
            CAMERA_REQUEST -> {
                if (resultCode == AppCompatActivity.RESULT_OK)
                {
                    try
                    {
                        galleryRequest(imageUri, "Camera")
                    }
                    catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }


                }
            }
            PICKER_REQUEST_CODE -> {
                val images: ArrayList<Image> = ImagePicker.getImages(data)
                for (image in images) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        galleryRequest(image.uri, "Gallery")
                    } else {
                        val fileUri = Uri.parse(image.path)
                        galleryRequest(fileUri, "Gallery")

                    }
                }

            }
            galleryRequest -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    val fileUri = data.extras?.get("file_uri") as String
                    when (currentPicture) {
                        "profile" -> {
                            updateProfileImage(fileUri)
                            Glide.with(requireActivity()).load(fileUri)
                                .into(binding.profileImage)
                        }
                        "cover" -> {
                            updateCoverPicture(fileUri)
                            Glide.with(requireActivity()).load(fileUri)
                                .into(binding.coverPicture)
                        }
                    }
                }
            }
        }
    }

    private fun getFileUri(bitmap: Bitmap): Uri {


        bitmap.height
        bitmap.width

        val path = requireActivity().getExternalFilesDir(null)?.absolutePath
        val file = File(path, "${UUID.randomUUID()}.png")


        try {
            val stream: OutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            bitmap.height
            bitmap.width
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }



        // Return the saved image path to uri
        return FileUtils.getUri(file)!!
    }


    private fun updateProfileImage(filePath: String) {
        dlg.show()
        val payload = JSONObject()

        if (isDelete) {
            payload.put("profile_pic", "")
            payload.put("is_delete", isDelete)
        } else {
            payload.put(
                "profile_pic",
                Utils.encodeToBase64(filePath)
            )
        }

        payload.put("id", userId)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.updateProfilePicture(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapProfilePictureData(dataModel)
                        if (File(filePath).exists()) {
                            File(filePath).delete()
                        }
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

    private fun wrapProfilePictureData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val profilePicModel = Utils.toModel(it, ProfilePicModel::class.java)
            if (profilePicModel.status) {
                if (isDelete) {
                    updateProfilePicLocal("", profilePicModel.message)
                } else {
                    updateProfilePicLocal(profilePicModel.profilePic, profilePicModel.message)
                }
            }
        }
    }

    private fun updateProfilePicLocal(profilePic: String, msg: String) {
        viewModel.updateProfilePictureLocal(profilePic).observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("${it.data}")
                    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    dlg.dismiss()
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                    dlg.dismiss()
                }
            }
        })
    }

    private fun updateCoverPicture(filePath: String) {
        dlg.show()

        val payload = JSONObject()

        if (isDelete) {
            payload.put("cover_pic", "")
            payload.put("is_delete", isDelete)
        } else {
            payload.put("cover_pic", Utils.encodeToBase64(filePath))
        }


        payload.put("id", userId)

        val data = JSONObject()
        data.put("data", AES.encrypt(payload.toString()))

        viewModel.updateCoverPicture(Utils.toRequestBody(data)).observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    wrapCoverPicData(it.data!!)
                    if (File(filePath).exists()) {
                        File(filePath).delete()
                    }
                }
                Resource.Status.LOADING -> {
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                    dlg.dismiss()
                }
            }
        })
    }

    private fun wrapCoverPicData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val coverPicModel = Utils.toModel(it, CoverPicModel::class.java)
            if (coverPicModel.status) {
                if (isDelete) {
                    updateCoverPicLocal("", coverPicModel.message)
                } else {
                    updateCoverPicLocal(coverPicModel.coverPic, coverPicModel.message)
                }
            }
        }
    }

    private fun updateCoverPicLocal(coverPic: String, msg: String) {
        viewModel.updateCoverPictureLocal(coverPic).observe(this, {
            when (it.status) {
                Resource.Status.LOADING -> Timber.i("loading...")
                Resource.Status.SUCCESS -> {
                    Timber.i("${it.data}")
                    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
                    dlg.dismiss()
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                    dlg.dismiss()
                }
            }
        })
    }

    private fun checkRequiredPermission(): Boolean = ContextCompat.checkSelfPermission(
        requireActivity(),
        storageReadPermission
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkCameraPermission(): Boolean = ContextCompat.checkSelfPermission(
        requireActivity(),
        cameraPermission
    ) == PackageManager.PERMISSION_GRANTED

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
                        requireActivity(),
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
                        requireActivity(),
                        "Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        Timber.i("onItemClick: $position")
        when (position) {
            0 -> {
                // camera
                if (!checkCameraPermission()) {
                    requestPermissions(
                        arrayOf(cameraPermission),
                        cameraPermissionRequest
                    )
                } else {
                    // Access camera intent
                    isDelete = false
                    accessCameraIntent()
                }
            }
            1 -> {
                // Gallery
                if (!checkRequiredPermission()) {
                    requestPermissions(
                        arrayOf(storageReadPermission, storageWritePermission),
                        storagePermission
                    )
                } else {
                    // access to local storage
                    isDelete = false
                    accessLocalStorage()
                }
            }
            2 -> {
                // Delete Photo
                when (currentPicture) {
                    "profile" -> {
                        if (profileImgFile.length > 0) {
                            isDelete = true
                            Glide.with(requireActivity()).load("")
                                .into(binding.profileImage)
                            updateProfileImage("")
                        }

                    }
                    "cover" -> {
                        if (coverImgFile.length > 0) {
                            isDelete = true
                            Glide.with(requireActivity()).load("")
                                .into(binding.coverPicture)
                            updateCoverPicture("")
                        }
                    }
                }
            }
        }
    }

    private fun galleryRequest(data: Uri, type: String)
    {
        val galleryIntent = Intent(requireActivity(), Gallery::class.java)
        galleryIntent.putExtra("img", data)
        if (currentPicture == "profile")
        {
            galleryIntent.putExtra("IMG_TYPE", "profile")
            galleryIntent.putExtra("FROM_TYPE", type)
        }
        else {
            galleryIntent.putExtra(
                "IMG_TYPE",
                "cover",

                )
            galleryIntent.putExtra("FROM_TYPE", type)
        }
        startActivityForResult(galleryIntent, galleryRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        dlg.dismiss()
    }
}