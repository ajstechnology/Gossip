package com.gtech.gossipmessenger.ui.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.gtech.gossipmessenger.R
import com.gtech.gossipmessenger.databinding.FragementCallBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallFragment : Fragment() {

    private lateinit var homeViewModel: CallViewModel
    private lateinit var binding: FragementCallBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(CallViewModel::class.java)
        binding = FragementCallBinding.inflate(inflater, container, false)
        binding.callList.adapter = ChatAdapter()
        return binding.root





    }



    inner class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            return ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_call, parent, false))
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return 50
        }
    }



}