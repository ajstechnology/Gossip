package com.gtech.gossipmessenger.ui.countries

import androidx.lifecycle.ViewModel
import com.gtech.gossipmessenger.data.repository.GossipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val repository: GossipRepository
) : ViewModel() {

    fun getCountries(payload: RequestBody) = repository.getCountry(payload)
}