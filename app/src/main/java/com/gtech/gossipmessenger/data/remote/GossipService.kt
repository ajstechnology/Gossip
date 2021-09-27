package com.gtech.gossipmessenger.data.remote

import com.gtech.gossipmessenger.base.Endpoints
import com.gtech.gossipmessenger.models.DataModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GossipService {

    @POST(Endpoints.CHECK_USR_NAME)
    suspend fun checkUsername(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SEND_OTP)
    suspend fun sendOTP(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SIGN_UP)
    suspend fun signUp(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SET_ACCT_DETAIL)
    suspend fun setAccountDetails(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SIGN_IN)
    suspend fun signIn(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.FORGOT_PASSWORD)
    suspend fun forgotPass(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.CHECK_FORGOT_PASSWORD)
    suspend fun checkForgotPassword(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.CHECK_FORGOT_PASSWORD_OTP)
    suspend fun checkForgotPasswordOTP(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.RESET_PASSWORD)
    suspend fun resetPassword(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.GET_COUNTRY)
    suspend fun getCountry(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.GET_STATE)
    suspend fun getState(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.GET_CITY)
    suspend fun getCity(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.UPDATE_PROFILE)
    suspend fun updateProfile(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SEND_OTP_MOBILE)
    suspend fun mobileVerification(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SEND_OTP_EMAIL)
    suspend fun emailVerification(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.CHANGE_USERNAME)
    suspend fun changeUsername(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.CHANGE_PASSWORD)
    suspend fun changePassword(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.DELETE_ACCOUNT)
    suspend fun deleteAccount(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.LOG_OUT)
    suspend fun logout(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.GET_COUNTRY_CODE)
    suspend fun getCountryCode(): Response<DataModel>

    @POST(Endpoints.UPDATE_COVER_PIC)
    suspend fun updateCoverPicture(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.UPDATE_PROFILE_PIC)
    suspend fun updateProfilePicture(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.GET_STATUS)
    suspend fun getStatus(): Response<DataModel>

    @POST(Endpoints.UPDATE_MOBILE)
    suspend fun updateMobile(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.UPDATE_EMAIL)
    suspend fun updateEmail(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SEND_OTP_EMAIL_CHANGE)
    suspend fun sendOTPEmailChange(@Body payload: RequestBody): Response<DataModel>

    @POST(Endpoints.SEND_OTP_MOBILE_CHANGE)
    suspend fun sendOTPMobileChange(@Body payload: RequestBody): Response<DataModel>
}