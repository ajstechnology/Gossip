package com.gtech.gossipmessenger.ui.resetpass

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ResetPassViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun resetPassword(payload: RequestBody) = repository.resetPassword(payload)
}