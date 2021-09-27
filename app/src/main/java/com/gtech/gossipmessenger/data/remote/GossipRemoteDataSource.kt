package com.gtech.gossipmessenger.data.remote

import okhttp3.RequestBody
import javax.inject.Inject

class GossipRemoteDataSource @Inject constructor(private val gossipService: GossipService) :
    BaseDataSource() {

    suspend fun checkUsername(payload: RequestBody) = getResult {
        gossipService.checkUsername(payload)
    }

    suspend fun sendOTP(payload: RequestBody) = getResult {
        gossipService.sendOTP(payload)
    }

    suspend fun signUp(payload: RequestBody) = getResult {
        gossipService.signUp(payload)
    }

    suspend fun setAccountDetails(payload: RequestBody) = getResult {
        gossipService.setAccountDetails(payload)
    }

    suspend fun signIn(payload: RequestBody) = getResult {
        gossipService.signIn(payload)
    }

    suspend fun forgotPass(payload: RequestBody) = getResult {
        gossipService.forgotPass(payload)
    }

    suspend fun checkForgotPassword(payload: RequestBody) = getResult {
        gossipService.checkForgotPassword(payload)
    }

    suspend fun checkForgotPasswordOTP(payload: RequestBody) = getResult {
        gossipService.checkForgotPasswordOTP(payload)
    }

    suspend fun resetPassword(payload: RequestBody) = getResult {
        gossipService.resetPassword(payload)
    }

    suspend fun getCountry(payload: RequestBody) = getResult {
        gossipService.getCountry(payload)
    }

    suspend fun getState(payload: RequestBody) = getResult {
        gossipService.getState(payload)
    }

    suspend fun getCity(payload: RequestBody) = getResult {
        gossipService.getCity(payload)
    }

    suspend fun updateProfile(payload: RequestBody) = getResult {
        gossipService.updateProfile(payload)
    }

    suspend fun mobileVerification(payload: RequestBody) = getResult {
        gossipService.mobileVerification(payload)
    }

    suspend fun emailVerification(payload: RequestBody) = getResult {
        gossipService.emailVerification(payload)
    }

    suspend fun changeUsername(payload: RequestBody) = getResult {
        gossipService.changeUsername(payload)
    }

    suspend fun changePassword(payload: RequestBody) = getResult {
        gossipService.changePassword(payload)
    }

    suspend fun deleteAccount(payload: RequestBody) = getResult {
        gossipService.deleteAccount(payload)
    }

    suspend fun logout(payload: RequestBody) = getResult {
        gossipService.logout(payload)
    }

    suspend fun getCountryCode() = getResult {
        gossipService.getCountryCode()
    }

    suspend fun updateCoverPicture(payload: RequestBody) = getResult {
        gossipService.updateCoverPicture(payload)
    }

    suspend fun updateProfilePicture(payload: RequestBody) = getResult {
        gossipService.updateProfilePicture(payload)
    }

    suspend fun getStatus() = getResult {
        gossipService.getStatus()
    }

    suspend fun updateMobile(payload: RequestBody) = getResult {
        gossipService.updateMobile(payload)
    }

    suspend fun updateEmail(payload: RequestBody) = getResult {
        gossipService.updateEmail(payload)
    }

    suspend fun sendOTPEmailChange(payload: RequestBody) = getResult {
        gossipService.sendOTPEmailChange(payload)
    }

    suspend fun sendOTPMobileChange(payload: RequestBody) = getResult {
        gossipService.sendOTPMobileChange(payload)
    }
}