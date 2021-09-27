package com.gtech.gossipmessenger.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gtech.gossipmessenger.databinding.ButtomSheetLayerBinding
import com.gtech.gossipmessenger.databinding.CustomDialogLayoutBinding
import com.gtech.gossipmessenger.databinding.FragmentHomeBinding
import com.gtech.gossipmessenger.databinding.ListItemChatmenuBinding
import com.gtech.gossipmessenger.models.ChatModel
import com.gtech.gossipmessenger.ui.archive.ArchiveContacts
import com.gtech.gossipmessenger.ui.contactinfo.ContactInfo
import com.gtech.gossipmessenger.ui.contactsynclocal.ContactName
import com.gtech.gossipmessenger.ui.schedulemsg.Schedulemsg
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject


@AndroidEntryPoint
class HomeFragment : Fragment(), ChatCusttomeAdapter.OnSlideArchiveClickListener,
    ChatCusttomeAdapter.OnSlideMoreClickListener, ChatCusttomeAdapter.OnSlideUnreadClickListener {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.chatList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        setChatList()
        binding.menuIcon.setOnClickListener()
        {

            val binding = CustomDialogLayoutBinding.inflate(layoutInflater)
            val view: View = binding.root
            val dialog = Dialog(requireContext())
            dialog.setContentView(view)
            dialog.show()
            binding.recyclerView.adapter = chatmenuAdapter(
                arrayListOf(
                    "New Chat",
                    "Schedule Message",
                    "Archive Chats",
                )
            )

            binding.recyclerView.setOnItemClickListener { parent, view, position, id ->
                when (position) {
                    0 -> {
                        dialog.dismiss()
                        val binding1 = CustomDialogLayoutBinding.inflate(layoutInflater)
                        val view1: View = binding1.root
                        val dialog1 = Dialog(requireContext())
                        dialog1.setContentView(view1)
                        dialog1.show()
                        binding1.recyclerView.adapter = chatmenuAdapter(
                            arrayListOf(
                                "Choose Option",
                                "Contact",
                                "Username's",
                                "Cancel"
                            )
                        )
                        binding1.recyclerView.setOnItemClickListener { parent, view, position, id ->
                            when (position) {
                                0 -> {
                                    startActivity(Intent(requireContext(), ContactName::class.java))
                                }
                                1 -> {
                                    startActivityForResult(
//                                        Intent(
//                                            requireContext(),
//                                            ContactNumberSync::class.java
//                                        )

                                        Intent(
                                            Intent.ACTION_PICK,
                                            ContactsContract.Contacts.CONTENT_URI
                                        ), 2
                                    )

                                }
                                2 -> {

                                }

                            }

                        }
                    }
                    1->{

                        startActivity(Intent(requireContext(), Schedulemsg::class.java))

                    }
                    2->
                    {
                        startActivity(Intent(requireContext(), ArchiveContacts::class.java))
                    }
                }

            }

        }

        binding.insertContact.setOnClickListener {
            val contactInsertIntent =
                Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(contactInsertIntent, 12)
        }

        return binding.root;
    }

    private fun setChatList() {
        val modelList = readFromAsset1()
        val adapter = ChatCusttomeAdapter(modelList, requireContext())
        adapter.setonSlideArchiveClickListener(this)
        adapter.setOnSlideMoreClickListener(this)
        adapter.setOnSlideUnreadClickListener(this)
        binding.chatList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.chatList.adapter = adapter;

    }

    /*Get Date From Assets in json format*/
    private fun readFromAsset1(): MutableList<ChatModel> {
        val modeList = mutableListOf<ChatModel>()
        val bufferReader = requireContext().assets.open("android_chatlist.json").bufferedReader()
        val json_string = bufferReader.use {
            it.readText()
        }
        val jsonArray = JSONArray(json_string);

        for (i in 0..jsonArray.length() - 1) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)

            val ChatModels =
                ChatModel(
                    jsonObject.getString("name"),
                    jsonObject.getString("lastText"),
                    jsonObject.getString("date")
                )
            modeList.add(ChatModels)
        }

        return modeList
    }

    /*Chatbuttom menu screen Adapter*/
    class chatmenuAdapter(val items: ArrayList<String>) : BaseAdapter() {
        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val lia =
                ListItemChatmenuBinding.inflate(
                    LayoutInflater.from(parent!!.context),
                    parent,
                    false
                )

            lia.itemText.text = items[position]

            return lia.root
        }

    }

    /*Left AND Right swipe menu Items*/

    override fun onMoreClick() {
        val binding = ButtomSheetLayerBinding.inflate(layoutInflater)
        val view: View = binding.root
        val dialog1 = BottomSheetDialog(requireActivity())
        dialog1.setContentView(view)
        dialog1.show()
        binding.listmenuofchat.adapter = chatmenuAdapter(
            arrayListOf(
                "Mute",
                "Clear Chat",
                "Lock Chat",
                "Contact Info",
                "Schedule Message",
                "Two Step Verification",
                "Delete",
                "Cancel"
            )
        )

        binding.listmenuofchat.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    dialog1.dismiss()
                    val binding1 = ButtomSheetLayerBinding.inflate(layoutInflater)
                    val view: View = binding1.root
                    val dialog = BottomSheetDialog(requireActivity())
                    dialog.setContentView(view)
                    dialog.show()

                    binding1.listmenuofchat.adapter = chatmenuAdapter(
                        arrayListOf(
                            "Choose Option",
                            "9 Hours",
                            "1 Day",
                            "1 Week",
                            "1 Month",
                            "Forever",
                            "Cancel"
                        )
                    )

                    binding1.listmenuofchat.setOnItemClickListener { parent, view, position, id ->
                        when (position) {
                            0 -> {


                            }
                            1 -> {

                            }
                            2 -> {

                            }
                            3 -> {

                            }
                            4 -> {

                            }
                            6 -> {


                            }
                        }
                        dialog.dismiss()
                    }
                }
                1 -> {
                    val bindingchatclear =
                        ButtomSheetLayerBinding.inflate(layoutInflater)
                    val view: View = bindingchatclear.root
                    val dialog = BottomSheetDialog(requireActivity())
                    dialog.setContentView(view)
                    dialog.show()

                    bindingchatclear.listmenuofchat.adapter = chatmenuAdapter(
                        arrayListOf(
                            "Delete",
                            "Cancel"
                        )
                    )

                    bindingchatclear.listmenuofchat.setOnItemClickListener { parent, view, position, id ->
                        when (position) {
                            0 -> {

                                dialog.dismiss()
                                val dialogBuilder =
                                    androidx.appcompat.app.AlertDialog.Builder(requireContext())

                                // set message of alert dialog
                                dialogBuilder.setMessage("Are you sure you want to delete this chat ?")
                                    // if the dialog is cancelable
                                    .setCancelable(false)
                                    // positive button text and action
                                    .setPositiveButton(
                                        "OK"
                                    ) { dialog, id ->
                                        dialog.dismiss()
                                    }
                                    // negative button text and action
                                    .setNegativeButton(
                                        "Cancel"
                                    ) { dialog, id ->
                                        dialog.cancel()
                                    }

                                // create dialog box
                                val alert = dialogBuilder.create()
                                // set title for alert dialog box
                                alert.setTitle("AlertDialogExample")
                                // show alert dialog
                                alert.show()


                            }
                            1 -> {

                            }

                        }
                    }
                }
                2 -> {

                }
                3 -> {
                    startActivity(Intent(requireContext(), ContactInfo::class.java))


                }
                4 -> {

                }
                6 -> {


                }
            }
            dialog1.dismiss()
        }

    }

    override fun onArchiveClick() {

    }

    override fun onUnreadClick() {

    }
}















