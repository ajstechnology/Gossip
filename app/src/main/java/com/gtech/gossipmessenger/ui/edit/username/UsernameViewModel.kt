package com.gtech.gossipmessenger.ui.edit.username

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {
    fun changeUsername(payload: RequestBody) = repository.changeUsername(payload)

    fun changeUsernameLocal(username: String) = repository.updateUsername(username)
    val getDefaultUser = repository.getUser()

    fun changeStatus(status: Boolean, msg: String) =
        repository.updateUsernameChangeStatus(status, msg)
}