package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class CoverPicModel(

	@field:SerializedName("cover_pic")
	val coverPic: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean

)
