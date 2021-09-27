package com.gtech.gossipmessenger.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gtech.gossipmessenger.databinding.ActivityCameraTabBinding


class CameraFragment : Fragment() {
    private lateinit var binding: ActivityCameraTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityCameraTabBinding.inflate(inflater, container, false)
        return binding.root
    }

}

