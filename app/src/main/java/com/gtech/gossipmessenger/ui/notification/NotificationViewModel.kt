package com.gtech.gossipmessenger.ui.notification

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

}