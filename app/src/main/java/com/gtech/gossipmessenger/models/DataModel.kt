package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class DataModel(

	@field:SerializedName("data")
	val data: String
)
