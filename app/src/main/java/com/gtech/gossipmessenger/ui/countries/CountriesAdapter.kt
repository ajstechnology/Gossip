package com.gtech.gossipmessenger.ui.countries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.models.CountryDataItem
import com.gtech.gossipmessenger.ui.countries.CountriesAdapter.CountriesViewHolder
import com.gtech.gossipmessenger.utils.CountryFlags

class CountriesAdapter(
    countriesDataSet: ArrayList<CountryDataItem>,
    countryListener: CountriesListener
) :
    RecyclerView.Adapter<CountriesViewHolder>(), Filterable {

    private var countryList: ArrayList<CountryDataItem>
    private var countryListFull: ArrayList<CountryDataItem>
    private var countryListener: CountriesListener

    init {
        this.countryListFull = ArrayList(countriesDataSet)
        this.countryList = ArrayList(countriesDataSet)
        this.countryListener = countryListener
    }

    inner class CountriesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var countryName: TextView
        var countryCode: TextView
        var parentView: CardView

        init {
            countryName = itemView.findViewById(R.id.country_name)
            countryCode = itemView.findViewById(R.id.country_code)
            parentView = itemView.findViewById(R.id.countries_item_parent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountriesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.countries_item, parent, false)
        return CountriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountriesViewHolder, position: Int) {
        val country = countryList[position]
        holder.countryName.setText("${CountryFlags.getCountryFlagByCountryCode(country.sortname)} ${country.name}")
        holder.countryCode.setText("+${country.phonecode}")
        holder.parentView.setOnClickListener {
            countryListener.onItemClick(country)
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun getFilter(): Filter {
        return countryFilter
    }

    private val countryFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<CountryDataItem>()
            if (constraint.isEmpty()) {
                filteredList.addAll(countryListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase()
                for (item in countryListFull) {
                    if (item.name.lowercase().contains(filterPattern) || item.phonecode.toString()
                            .contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            countryList.clear()
            countryList.addAll(results.values as ArrayList<CountryDataItem>)
            notifyDataSetChanged()
        }
    }
}