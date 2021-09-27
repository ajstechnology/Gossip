package com.gtech.gossipmessenger.ui.deleteaccount

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {
    fun mobileVerification(payload: RequestBody) = repository.mobileVerification(payload)
    fun deleteAccount(payload: RequestBody) = repository.deleteAccount(payload)
    val getDefaultUser = repository.getUser()
    fun logoutLocal(status: Boolean) = repository.updateLogout(status)
}