package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class CVerifyModel(

	@field:SerializedName("otp")
	val otp: Int,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
