package com.gtech.gossipmessenger.ui.edit.changepass

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun changePassword(payload: RequestBody) = repository.changePassword(payload)
    val getDefaultUser = repository.getUser()
}