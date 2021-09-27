package com.gtech.gossipmessenger.ui.edit.mobile

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class MobileViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {
    fun sendOTPMobileChange(payload: RequestBody) = repository.sendOTPMobileChange(payload)
    fun updateMobile(payload: RequestBody) = repository.updateMobile(payload)
    fun updateMobileLocal(mobile: String) = repository.updateMobile(mobile)
    val getDefaultUser = repository.getUser()
}