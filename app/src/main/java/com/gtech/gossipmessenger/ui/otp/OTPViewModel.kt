package com.gtech.gossipmessenger.ui.otp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class OTPViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    val timeString: MutableLiveData<String> = MutableLiveData()

    fun signUp(payload: RequestBody) = repository.signUp(payload)

    fun sentOTP(payload: RequestBody) = repository.sendOTP(payload)
}