package com.gtech.gossipmessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ImageBottomSheetListBinding
import com.gtech.gossipmessenger.databinding.ListItemImageBottomSheetBinding

class ImageBrowserBottomSheet(val itemClickListener: ItemClickListener) : BottomSheetDialogFragment() {
    private lateinit var binding: ImageBottomSheetListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImageBottomSheetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgOptions = arrayListOf(
            ImageBrowserItem("Camera", R.drawable.ic_photo_camera),
            ImageBrowserItem("Gallery", R.drawable.ic_gallery),
            ImageBrowserItem("Delete Photo", R.drawable.ic_dustbin)
        )

        binding.imageBrowserList.adapter = ImageBrowserAdapter(imgOptions)

        binding.imageBrowserList.setOnItemClickListener { parent, view, position, id ->
            itemClickListener.onItemClick(position)
            dismiss()
        }

        binding.imageBrowserClose.setOnClickListener {
            dismiss()
        }
    }

    // ImageBrowserItem
    data class ImageBrowserItem(val imageTitle: String, val imageResource: Int)

    // Adapter
    inner class ImageBrowserAdapter(var items: ArrayList<ImageBrowserItem>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val binding = ListItemImageBottomSheetBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )

            binding.libsImage.setImageResource(items[position].imageResource)
            binding.libsText.setText(items[position].imageTitle)

            return binding.root
        }

    }

    interface ItemClickListener{
        fun onItemClick(position: Int)
    }
}