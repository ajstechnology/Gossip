package com.gtech.gossipmessenger.ui.setacct

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.entities.SignIn
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SetAccountViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun setAccountDetails(payload: RequestBody) = repository.setAccountDetails(payload)

    fun signIn(payload: RequestBody) = repository.signIn(payload)

    fun saveData(signIn: SignIn) = repository.saveUser(signIn)
}