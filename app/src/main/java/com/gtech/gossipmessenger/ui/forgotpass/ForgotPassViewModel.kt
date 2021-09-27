package com.gtech.gossipmessenger.ui.forgotpass

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ForgotPassViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun forgotPassword(payload: RequestBody) = repository.forgotPass(payload)
}