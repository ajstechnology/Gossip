package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class SetAcctModel(

	@field:SerializedName("profile_pic")
	val profilePic: String? = null,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
