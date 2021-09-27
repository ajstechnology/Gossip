package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class SignupModel(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("status")
	val status: Boolean,

	@field:SerializedName("username")
	val username: String? = null
)
