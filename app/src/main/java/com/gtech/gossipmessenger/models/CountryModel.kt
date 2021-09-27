package com.gtech.gossipmessenger.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CountryModel(

	@field:SerializedName("country_data")
	val countryData: List<CountryDataItem>? = null,

	@field:SerializedName("status")
	val status: Boolean,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class CountryDataItem(

    @field:SerializedName("sortname")
    val sortname: String,

    @field:SerializedName("is_active")
    val isActive: Int,

    @field:SerializedName("is_deleted")
    val isDeleted: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("phonecode")
    val phonecode: Int,

    @field:SerializedName("id")
    val id: Int
) : Parcelable
