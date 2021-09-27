package com.gtech.gossipmessenger.data.entities

data class User(
    val tu_id: Int? = null,
    val tu_username: String? = null,
    val tu_email: String? = null,
    val tu_mobile: String? = null,
    val tu_first_name: String? = null,
    val tu_last_name: String? = null,
    val tu_profile_pic: String? = null,
    val tu_status: Boolean? = null,
    val tu_is_deleted: Boolean? = null,
    val tu_token: String? = null,
    val tu_cover_pic: String? = null,
    val tu_bio: String? = null,
    val tu_city: String? = null,
    val tu_gender: String? = null,
    val tu_birth_date: String? = null,
    val tu_random_string: String? = null,
    val tu_password: String? = null,
    val tu_is_verify: Boolean? = null,
    val tu_otp: String? = null,
    val tu_state: String? = null,
    val tu_country: String? = null,
    val tu_country_code: String? = null,
    val can_change_username: Boolean? = null,
    val change_username_message: String? = null
)
