package com.gtech.gossipmessenger.ui.contactsynclocal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.gtech.gossipmessenger.databinding.ActivityContactnameBinding
import com.gtech.gossipmessenger.databinding.ChatInTextBinding
import com.gtech.gossipmessenger.databinding.ChatOutTextBinding
import com.gtech.gossipmessenger.models.AppChatTestResponse
import com.gtech.gossipmessenger.utils.Utils

class ContactName : AppCompatActivity() {
    private lateinit var binding: ActivityContactnameBinding
    private lateinit var adapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactnameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatDataSource = Utils.getJsonDataFromAssets(this, "chat_list.json")
        val gson = Gson()
        val chatData =
            gson.fromJson(chatDataSource, Array<AppChatTestResponse>::class.java).toList()

        adapter = ChatAdapter(this)
        adapter.setChatData(chatData)
        binding.chatRecycler.adapter = adapter

    }

    class ChatAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_ONE = 1
            const val VIEW_TYPE_TWO = 2
        }

        private var chatDataList: ArrayList<AppChatTestResponse> = ArrayList()

        inner class ChatInViewHolder(val binding: ChatInTextBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(chatDataItem: AppChatTestResponse) {
                binding.chatInTxt.text = chatDataItem.msg
                binding.chatInTime.text = chatDataItem.time
            }
        }

        inner class ChatOutViewHolder(val binding: ChatOutTextBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(chatDataItem: AppChatTestResponse) {
                binding.chatOutTxt.text = chatDataItem.msg
                binding.chatOutTime.text = chatDataItem.time
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == VIEW_TYPE_ONE) {
                return ChatInViewHolder(
                    ChatInTextBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            }
            return ChatOutViewHolder(
                ChatOutTextBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (chatDataList[position].cType == VIEW_TYPE_ONE) {
                (holder as ChatInViewHolder).bind(chatDataList[position])
            } else {
                (holder as ChatOutViewHolder).bind(chatDataList[position])
            }
        }

        override fun getItemCount(): Int = chatDataList.size

        fun setChatData(chatDataList: List<AppChatTestResponse>) {
            this.chatDataList.clear()
            this.chatDataList.addAll(chatDataList)
            this.notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int = chatDataList[position].cType

    }
}