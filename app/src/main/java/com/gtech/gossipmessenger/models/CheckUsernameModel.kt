package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class CheckUsernameModel(

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("status")
	val status: Boolean,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
