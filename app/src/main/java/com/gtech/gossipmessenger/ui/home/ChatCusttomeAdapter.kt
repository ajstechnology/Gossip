package com.gtech.gossipmessenger.ui.home

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gtech.gossipmessenger.models.ChatModel
import com.gtech.gossipmessenger.databinding.ItemChatListBinding


class ChatCusttomeAdapter(val modelList: List<ChatModel>, val context: Context) :
    RecyclerView.Adapter<ChatCusttomeAdapter.ViewHolder>() {

    var handler: Handler? = Handler()

    private lateinit var onSlideMoreClickListener: OnSlideMoreClickListener
    private lateinit var onSlideArchiveClickListener: OnSlideArchiveClickListener
    private lateinit var onSlideUnreadClickListener: OnSlideUnreadClickListener

    fun setOnSlideMoreClickListener(onSlideMoreClickListener: OnSlideMoreClickListener) {
        this.onSlideMoreClickListener = onSlideMoreClickListener
    }

    fun setonSlideArchiveClickListener(onSlideArchiveClickListener: OnSlideArchiveClickListener) {
        this.onSlideArchiveClickListener= onSlideArchiveClickListener
    }

    fun setOnSlideUnreadClickListener(onSlideUnreadClickListener: OnSlideUnreadClickListener) {
        this.onSlideUnreadClickListener = onSlideUnreadClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modelList.get(position));
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatCusttomeAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemChatListBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return modelList.size;
    }

    inner class ViewHolder(val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatModel): Unit {

            binding.txtName.setText(model.name)
            binding.txtLastChat.setText(model.lastText)
            binding.txtDate.setText(model.date)

            binding.slideMore.setOnClickListener {
                onSlideMoreClickListener.onMoreClick()
            }

            binding.slideArchive.setOnClickListener {
                onSlideArchiveClickListener.onArchiveClick()
            }

            binding.slideUnread.setOnClickListener {
                onSlideUnreadClickListener.onUnreadClick()
            }
        }
    }

    fun undoView(position: Int) {
        handler?.postDelayed({
            notifyItemChanged(position)
        }, 1000)
    }

    interface OnSlideMoreClickListener {
        fun onMoreClick()
    }

    interface OnSlideArchiveClickListener {
        fun onArchiveClick()
    }

    interface OnSlideUnreadClickListener {
        fun onUnreadClick()
    }
}