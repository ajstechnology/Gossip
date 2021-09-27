package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class AppChatTestResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("c_type")
	val cType: Int,

	@field:SerializedName("time")
	val time: String,

	@field:SerializedName("m_type")
	val mType: String
)
