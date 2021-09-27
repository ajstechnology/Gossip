package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class CityModel(

	@field:SerializedName("city_data")
	val cityData: List<CityDataItem>? = null,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class CityDataItem(

	@field:SerializedName("is_active")
	val isActive: Int,

	@field:SerializedName("is_deleted")
	val isDeleted: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("state_id")
	val stateId: Int
)
