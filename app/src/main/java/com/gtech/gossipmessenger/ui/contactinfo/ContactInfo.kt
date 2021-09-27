package com.gtech.gossipmessenger.ui.contactinfo

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.databinding.*
import com.gtech.gossipmessenger.fragments.ImageBrowserBottomSheet

class ContactInfo : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContactinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
           finish()
        }


        binding.contactinfolist.adapter = chatmenuAdapter(
            arrayListOf(
                "Favourite Messages",
                "Groups in common",
                "Media, Links and Documents",
                "Schedule messages",
                "Chat search",
                "Mute",
                "Hide Last seen",
                "Wallpaper",
                  "Start secret chat",
                  "Clear chat",
               "Export chat",
            "Block contact",
                "Report contact"

            )
        )


    }

    inner class chatmenuAdapter(val items: ArrayList<String>) : BaseAdapter() {
        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val lia =
                ListItemContactinfoBinding.inflate(
                    LayoutInflater.from(parent!!.context),
                    parent,
                    false
                )

            lia.itemText.text = items[position]

            return lia.root
        }

    }
}

