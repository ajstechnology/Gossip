package com.gtech.gossipmessenger.ui.profile

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun logout(payload: RequestBody) = repository.logout(payload)

    fun updateProfilePicture(payload: RequestBody) = repository.updateProfilePicture(payload)

    fun updateCoverPicture(payload: RequestBody) = repository.updateCoverPicture(payload)

    val getDefaultUser = repository.getUser()

    fun updateCoverPictureLocal(coverPicture: String) = repository.updateCoverPicture(coverPicture)

    fun updateProfilePictureLocal(profilePicture: String) =
        repository.updateProfilePicture(profilePicture)

    fun logoutLocal(status: Boolean) = repository.updateLogout(status)
}