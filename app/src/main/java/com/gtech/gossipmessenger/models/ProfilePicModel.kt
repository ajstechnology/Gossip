package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class ProfilePicModel(

	@field:SerializedName("profile_pic")
	val profilePic: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
