package com.gtech.gossipmessenger.models

import com.google.gson.annotations.SerializedName

data class SignInModel(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("cover_pic_url")
    val coverPicUrl: String? = null,

    @field:SerializedName("user_data")
    val userData: UserData? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Boolean
)

data class UserData(

    @field:SerializedName("tu_id")
    val tuId: Int? = null,

    @field:SerializedName("tu_bio")
    val tuBio: String? = null,

    @field:SerializedName("tu_mobile")
    val tuMobile: String? = null,

    @field:SerializedName("tu_is_deleted")
    val tuIsDeleted: Boolean? = null,

    @field:SerializedName("tu_city")
    val tuCity: String? = null,

    @field:SerializedName("tu_first_name")
    val tuFirstName: String? = null,

    @field:SerializedName("tu_token")
    val tuToken: String? = null,

    @field:SerializedName("tu_cover_pic")
    val tuCoverPic: String? = null,

    @field:SerializedName("tu_random_string")
    val tuRandomString: String? = null,

    @field:SerializedName("tu_username")
    val tuUsername: String? = null,

    @field:SerializedName("tu_last_name")
    val tuLastName: String? = null,

    @field:SerializedName("tu_status")
    val tuStatus: Boolean? = null,

    @field:SerializedName("tu_password")
    val tuPassword: String? = null,

    @field:SerializedName("tu_birth_date")
    val tuBirthDate: Any? = null,

    @field:SerializedName("tu_profile_pic")
    val tuProfilePic: String? = null,

    @field:SerializedName("tu_email")
    val tuEmail: String? = null,

    @field:SerializedName("tu_gender")
    val tuGender: String? = null,

    @field:SerializedName("tu_is_verify")
    val tuIsVerify: Boolean? = null,

    @field:SerializedName("tu_otp")
    val tuOtp: Int? = null,

    @field:SerializedName("tu_state")
    val tuState: String? = null,

    @field:SerializedName("tu_country")
    val tuCountry: String? = null,

    @field:SerializedName("tu_country_code")
    val tuCountryCode: String? = null,

    @field:SerializedName("can_change_username")
    val canChangeUsername: Boolean? = null,

    @field:SerializedName("change_username_message")
    val changeUsernameMessage: String? = null
)
