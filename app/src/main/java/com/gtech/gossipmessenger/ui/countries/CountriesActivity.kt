package com.gtech.gossipmessenger.ui.countries

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.ActivityCountriesBinding
import com.gtech.gossipmessenger.models.CountryDataItem
import com.gtech.gossipmessenger.models.CountryModel
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class CountriesActivity : AppCompatActivity(), CountriesListener {
    private lateinit var binding: ActivityCountriesBinding
    private val viewModel: CountriesViewModel by viewModels()
    private lateinit var countriesDataSet: ArrayList<CountryDataItem>
    private lateinit var countriesAdapter: CountriesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data = JSONObject()
        countriesDataSet = ArrayList()

        viewModel.getCountries(Utils.toRequestBody(data))
            .observe(this, {
                when (it.status) {
                    Resource.Status.LOADING -> Timber.i("loading...")
                    Resource.Status.SUCCESS -> wrapData(it.data)
                    Resource.Status.ERROR -> Timber.i("${it.message}")
                }
            })

    }

    private fun wrapData(data: DataModel?) {
        AES.decrypt(data?.data)?.let { decoded ->
            val countryModel = Utils.toModel(decoded, CountryModel::class.java)
            if (countryModel.status) {
                countryModel.countryData?.let { countryData ->
                    countriesDataSet.addAll(countryData)
                    countriesAdapter = CountriesAdapter(countriesDataSet, this@CountriesActivity)
                    binding.countryList.adapter = countriesAdapter
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.countries_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                countriesAdapter.filter.filter(newText)
                return false
            }
        })
        return true
    }

    override fun onItemClick(country: CountryDataItem) {
        Timber.i("$country")
        val resultIntent = Intent()
        resultIntent.putExtra("country", country)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}