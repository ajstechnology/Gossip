package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class StatusModel(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status_data")
	val statusData: List<StatusDataItem>?,

	@field:SerializedName("status")
	val status: Boolean
)

data class StatusDataItem(

	@field:SerializedName("ts_id")
	val tsId: Int,

	@field:SerializedName("ts_status")
	val tsStatus: Boolean,

	@field:SerializedName("ts_is_deleted")
	val tsIsDeleted: Boolean,

	@field:SerializedName("ts_text")
	val tsText: String
)
