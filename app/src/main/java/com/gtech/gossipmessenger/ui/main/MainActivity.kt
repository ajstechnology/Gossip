package com.gtech.gossipmessenger.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.databinding.ActivityTesterBinding
import com.gtech.gossipmessenger.fragments.ImageBrowserBottomSheet

class MainActivity : AppCompatActivity(), ImageBrowserBottomSheet.ItemClickListener {

    companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var binding: ActivityTesterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            ImageBrowserBottomSheet(this).apply {
                show(supportFragmentManager, "imageBrowser")
            }
        }
    }

    override fun onItemClick(position: Int) {
        Log.d(TAG, "onItemClick: $position")
    }
}