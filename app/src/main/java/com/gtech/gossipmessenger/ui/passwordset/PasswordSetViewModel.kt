package com.gtech.gossipmessenger.ui.passwordset

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class PasswordSetViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun sentOTP(payload: RequestBody) = repository.sendOTP(payload)
}