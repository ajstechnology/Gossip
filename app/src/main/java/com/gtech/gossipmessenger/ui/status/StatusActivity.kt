package com.gtech.gossipmessenger.ui.status

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.databinding.ActivityStatusBinding
import com.gtech.gossipmessenger.databinding.ListItemStatusEditBinding
import com.gtech.gossipmessenger.models.DataModel
import com.gtech.gossipmessenger.models.StatusDataItem
import com.gtech.gossipmessenger.models.StatusModel
import com.gtech.gossipmessenger.utils.AES
import com.gtech.gossipmessenger.utils.Resource
import com.gtech.gossipmessenger.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusBinding
    private lateinit var statusAdapter: StatusAdapter
    private var statusDataSet: ArrayList<StatusDataItem> = ArrayList()
    private val viewModel: StatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        statusAdapter = StatusAdapter(statusDataSet)
        binding.statusList.adapter = statusAdapter

        binding.statusList.setOnItemClickListener { parent, view, position, id ->
            binding.statusEdit.setText(statusDataSet[position].tsText)
        }

        binding.btnSave.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("STATUS_RESULT", binding.statusEdit.text.toString())
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        viewModel.getStatus().observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Timber.i("data -> ${AES.decrypt(it.data?.data)}")
                    it.data?.let { dataModel ->
                        wrapData(dataModel)
                    }
                }
                Resource.Status.LOADING -> {
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                }
            }
        })

        viewModel.getDefaultUser.observe(this, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.statusEdit.setText(it.data?.user?.tu_bio)
                    it.data?.user?.tu_bio?.length?.let { binding.statusEdit.setSelection(it) }
                }
                Resource.Status.LOADING -> {
                }
                Resource.Status.ERROR -> {
                    Timber.e("error -> ${it.message}")
                }
            }
        })
    }

    private fun wrapData(dataModel: DataModel) {
        AES.decrypt(dataModel.data)?.let {
            val statusModel = Utils.toModel(it, StatusModel::class.java)
            if (statusModel.status) {
                statusModel.statusData?.let {
                    statusDataSet.clear()
                    statusDataSet.addAll(it)
                    statusAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun hideKeyboard(view: View) {
        Utils.hideKeyboard(this)
    }

    inner class StatusAdapter(val items: ArrayList<StatusDataItem>) :
        BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val sabinding = ListItemStatusEditBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
            sabinding.statusText.text = items[position].tsText
            return sabinding.root
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}