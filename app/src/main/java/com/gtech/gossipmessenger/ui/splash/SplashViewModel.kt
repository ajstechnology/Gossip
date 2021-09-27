package com.gtech.gossipmessenger.ui.splash

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(repository: GossipRepository) : ViewModel() {
    val getDefaultUser = repository.getUser()
}