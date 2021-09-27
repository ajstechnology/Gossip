package com.gtech.gossipmessenger.ui.signup

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {
    fun checkUsername(payload: RequestBody) = repository.checkUsername(payload)
}