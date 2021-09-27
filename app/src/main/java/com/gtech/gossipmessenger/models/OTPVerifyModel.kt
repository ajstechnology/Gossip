package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class OTPVerifyModel(
    @field:SerializedName("status")
    val status: Boolean
)
