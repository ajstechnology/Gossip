package com.gtech.gossipmessenger.ui.profile.update

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun updateProfile(payload: RequestBody) = repository.updateProfile(payload)

    val getDefaultUser = repository.getUser()

    fun updateProfileLocal(firstName: String, lastName: String, bio: String) =
        repository.updateProfileLocal(firstName, lastName, bio)
}