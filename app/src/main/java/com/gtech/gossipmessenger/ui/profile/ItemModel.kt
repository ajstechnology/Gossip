package com.gtech.gossipmessenger.ui.profile

import android.graphics.Color

data class ItemModel(
    val color: Int = Color.parseColor("#000"),
    val drawable: Int,
    val title: String,
    val itemForward: Boolean = true
)