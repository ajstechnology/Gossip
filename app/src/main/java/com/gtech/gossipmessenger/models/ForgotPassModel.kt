package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class ForgotPassModel(

	@field:SerializedName("is_email")
	val isEmail: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
