package com.gtech.gossipmessenger.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.gtech.gossipmessenger.databinding.ActivityAccountBinding
import com.gtech.gossipmessenger.databinding.ListItemAccountBinding
import com.gtech.gossipmessenger.ui.deleteaccount.DeleteAccountActivity
import com.gtech.gossipmessenger.ui.edit.changepass.ChangePasswordActivity
import com.gtech.gossipmessenger.ui.edit.email.EmailActivity
import com.gtech.gossipmessenger.ui.edit.mobile.MobileActivity
import com.gtech.gossipmessenger.ui.edit.username.UsernameActivity
import com.gtech.gossipmessenger.ui.profile.update.ProfileUpdateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.accountList.adapter = AccountAdapter(
            arrayListOf(
                "My Profile Info",
                "Change Username",
                "Change Mobile Number",
                "Change Email",
                "Change Password",
                "Two Step Verification",
                "Delete Account"
            )
        )

        binding.accountList.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    openActivity(ProfileUpdateActivity::class.java)
                }
                1 -> {
                    openActivity(UsernameActivity::class.java)
                }
                2 -> {
                    openActivity(MobileActivity::class.java)
                }
                3 -> {
                    openActivity(EmailActivity::class.java)
                }
                4 -> {
                    openActivity(ChangePasswordActivity::class.java)
                }
                6 -> {
                    openActivity(DeleteAccountActivity::class.java)

                }
            }

        }
    }


    private fun <T> openActivity(target: Class<T>) {
        startActivity(Intent(this@AccountActivity, target))
    }

    inner class AccountAdapter(val items: ArrayList<String>) : BaseAdapter() {
        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val lia =
                ListItemAccountBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)

            lia.itemText.text = items[position]
            return lia.root
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}