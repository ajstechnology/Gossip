package com.gtech.gossipmessenger.ui.countries

import com.gtech.gossipmessenger.models.CountryDataItem

interface CountriesListener {
    fun onItemClick(country: CountryDataItem)
}