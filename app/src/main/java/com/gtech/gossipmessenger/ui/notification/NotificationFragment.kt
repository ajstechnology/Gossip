package com.gtech.gossipmessenger.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.FragmentHomeBinding
import com.gtech.gossipmessenger.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var homeViewModel: NotificationViewModel
    private lateinit var binding: FragmentNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(NotificationViewModel::class.java)
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.notificationList.adapter = ChatAdapter()
        return binding.root
    }

    inner class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            return ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_notification, parent, false))
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return 50
        }
    }

}