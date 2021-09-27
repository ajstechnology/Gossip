package com.gtech.gossipmessenger.ui.verifyotp

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class OTPVerifyViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun checkForgotPasswordOTP(payload: RequestBody) = repository.checkForgotPasswordOTP(payload)

    fun checkForgotPassword(payload: RequestBody) = repository.checkForgotPassword(payload)
}