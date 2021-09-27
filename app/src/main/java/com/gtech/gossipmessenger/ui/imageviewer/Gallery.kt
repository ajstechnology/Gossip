package com.gtech.gossipmessenger.ui.imageviewer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gtech.gossipmessenger.databinding.ActivityImageViewerGalleryBinding
import com.gtech.gossipmessenger.databinding.DlgLoaderBinding
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class Gallery : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerGalleryBinding
    private val rotationList = arrayOf(0, 90, 180, 270)
    private var rotationIndex = 0
    private lateinit var imgUri: Uri
    private lateinit var dlg: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loaderBinding = DlgLoaderBinding.inflate(layoutInflater)
        dlg = AlertDialog.Builder(this)
            .setView(loaderBinding.root)
            .setCancelable(false)
            .create()

        imgUri = intent.extras?.get("img") as Uri

        val type = intent.getStringExtra("IMG_TYPE")
        val FROM_TYPE = intent.getStringExtra("FROM_TYPE")

        when (type) {
            "cover" -> {
                binding.imgPreview.setAspectRatio(9, 5)
            }
            "profile" -> {
                binding.imgPreview.setFixedAspectRatio(true)
            }
        }



        if(FROM_TYPE.equals("Gallery"))
        {
            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    binding.imgPreview.setImageUriAsync(imgUri)
                else
                {
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    // options.inSampleSize = 8
                    val bimg: Bitmap = BitmapFactory.decodeFile(imgUri.toString(), options)
                    binding.imgPreview.setImageBitmap(bimg)

                }
            }
            catch (e:Exception)
            {
                binding.imgPreview.setImageUriAsync(imgUri)
            }


        }
        else
            binding.imgPreview.setImageUriAsync(imgUri)



        binding.imgCaptureClose.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_CANCELED, resultIntent)
            finish()
        }
        binding.imgCaptureRotation.setOnClickListener {
            if (rotationIndex == rotationList.size - 1) {
                rotationIndex = 0
            } else {
                rotationIndex++
            }

            binding.imgPreview.rotateImage(rotationList[rotationIndex])
        }
        binding.imgCapturesave.setOnClickListener {
            saveImageToLocal()
        }
    }

    private fun saveImageToLocal() {
        dlg.show()

        val path = getExternalFilesDir(null)?.absolutePath
        val file = File(path, "${UUID.randomUUID()}.jpeg")

        val out = FileOutputStream(file)
        val croppedImage = binding.imgPreview.croppedImage
        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, out)

        lifecycleScope.launch(Dispatchers.IO) {
            Compressor.compress(this@Gallery, file) {
                quality(100)
                destination(file)
                size(1_048_576)
            }
            val resultIntent = Intent()
            resultIntent.putExtra("file_uri", file.absolutePath)
            setResult(RESULT_OK, resultIntent)
            val compressorDir = File("${cacheDir.absolutePath}/compressor")
            if (compressorDir.exists() && compressorDir.isDirectory) {
                compressorDir.deleteRecursively()
            }
            dlg.dismiss()
            finish()
        }
    }
}