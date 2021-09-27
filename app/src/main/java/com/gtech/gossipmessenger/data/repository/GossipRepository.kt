package com.gtech.gossipmessenger.data.repository

import androidx.lifecycle.liveData
import com.gtech.gossipmessenger.data.entities.SignIn
import com.gtech.gossipmessenger.data.entities.UserDao
import com.gtech.gossipmessenger.data.remote.GossipRemoteDataSource
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.performLocalOperation
import com.gtech.gossipmessenger.utils.performNetworkOperation
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody
import javax.inject.Inject

class GossipRepository @Inject constructor(
    private val remoteDataSource: GossipRemoteDataSource,
    private val localDataSource: UserDao
) {
    // Web Resources
    fun checkUsername(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.checkUsername(payload)
    }

    fun sendOTP(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.sendOTP(payload)
    }

    fun signUp(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.signUp(payload)
    }

    fun setAccountDetails(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.setAccountDetails(payload)
    }

    fun signIn(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.signIn(payload)
    }

    fun forgotPass(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.forgotPass(payload)
    }

    fun checkForgotPassword(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.checkForgotPassword(payload)
    }

    fun checkForgotPasswordOTP(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.checkForgotPasswordOTP(payload)
    }

    fun resetPassword(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.resetPassword(payload)
    }

    fun getCountry(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.getCountry(payload)
    }

    fun getState(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.getState(payload)
    }

    fun getCity(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.getCity(payload)
    }

    fun updateProfile(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.updateProfile(payload)
    }

    fun mobileVerification(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.mobileVerification(payload)
    }

    fun emailVerification(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.emailVerification(payload)
    }

    fun changeUsername(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.changeUsername(payload)
    }

    fun changePassword(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.changePassword(payload)
    }

    fun deleteAccount(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.deleteAccount(payload)
    }

    fun logout(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.logout(payload)
    }

    fun getCountryCode() = performNetworkOperation {
        remoteDataSource.getCountryCode()
    }

    fun updateCoverPicture(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.updateCoverPicture(payload)
    }

    fun updateProfilePicture(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.updateProfilePicture(payload)
    }

    fun getStatus() = performNetworkOperation {
        remoteDataSource.getStatus()
    }

    fun updateMobile(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.updateMobile(payload)
    }

    fun updateEmail(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.updateEmail(payload)
    }

    fun sendOTPEmailChange(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.sendOTPEmailChange(payload)
    }

    fun sendOTPMobileChange(
        payload: RequestBody
    ) = performNetworkOperation {
        remoteDataSource.sendOTPMobileChange(payload)
    }

    // Local Resources
    fun getUser() = performLocalOperation {
        localDataSource.getDefaultUser()
    }

    fun saveUser(
        signIn: SignIn
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.insert(signIn)
        emit(Resource.success(true))
    }

    fun updateCoverPicture(
        coverPicture: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateCoverPicture(coverPicture)
        emit(Resource.success(true))
    }

    fun updateProfilePicture(
        profilePicture: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateProfilePicture(profilePicture)
        emit(Resource.success(true))
    }

    fun updateProfileLocal(
        firstName: String,
        lastName: String,
        bio: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateProfile(firstName, lastName, bio)
        emit(Resource.success(true))
    }


    fun updateUsername(username: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateUsername(username)
        emit(Resource.success(true))
    }

    fun updateMobile(mobile: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateMobileNumber(mobile)
        emit(Resource.success(true))
    }

    fun updateEmail(email: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateEmailAddress(email)
        emit(Resource.success(true))
    }

    fun updateLogout(status: Boolean) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateLogout(status)
        emit(Resource.success(true))
    }

    fun updateUsernameChangeStatus(status: Boolean, msg: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        localDataSource.updateUsernameChangeStatus(status, msg)
        emit(Resource.success(true))
    }

}