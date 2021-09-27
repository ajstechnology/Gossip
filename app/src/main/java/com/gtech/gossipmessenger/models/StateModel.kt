package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class StateModel(

    @field:SerializedName("state_data")
    val stateData: List<StateDataItem>? = null,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Boolean
)

data class StateDataItem(

    @field:SerializedName("is_active")
    val isActive: Int,

    @field:SerializedName("is_deleted")
    val isDeleted: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("country_id")
    val countryId: Int
)
