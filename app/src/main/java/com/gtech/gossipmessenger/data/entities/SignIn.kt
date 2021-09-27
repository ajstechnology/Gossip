package com.gtech.gossipmessenger.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class SignIn(
    @PrimaryKey
    val id: Int,
    val status: Boolean,
    val message: String?,
    val img_url: String?,
    val cover_pic_url: String?,
    @Embedded val user: User?
)
