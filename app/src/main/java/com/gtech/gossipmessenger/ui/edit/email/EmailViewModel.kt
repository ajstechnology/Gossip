package com.gtech.gossipmessenger.ui.edit.email

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun sendOTPEmailChange(payload: RequestBody) = repository.sendOTPEmailChange(payload)
    fun updateEmail(payload: RequestBody) = repository.updateEmail(payload)
    fun updateEmailLocal(email: String) = repository.updateEmail(email)
    val getDefaultUser = repository.getUser()
}