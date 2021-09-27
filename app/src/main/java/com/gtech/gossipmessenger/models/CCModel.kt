package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class CCModel(

	@field:SerializedName("country_data")
	val countryData: List<CountryCodeDataItem>?,

	@field:SerializedName("status")
	val status: Boolean
)

data class CountryCodeDataItem(

	@field:SerializedName("sortname")
	val shortName: String,

	@field:SerializedName("phonecode")
	val phonecode: Int
)
