package com.gtech.gossipmessenger.ui.status

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun getStatus() = repository.getStatus()

    val getDefaultUser = repository.getUser()
}