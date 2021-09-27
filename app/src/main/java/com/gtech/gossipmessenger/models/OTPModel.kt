package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class OTPModel(

	@field:SerializedName("otp")
	val otp: Int? = null,

	@field:SerializedName("otp_mobile")
	val mobOtp: Int? = null,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
