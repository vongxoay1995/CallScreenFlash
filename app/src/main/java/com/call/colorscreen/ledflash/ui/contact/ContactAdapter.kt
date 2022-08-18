package com.call.colorscreen.ledflash.ui.contact

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.call.colorscreen.ledflash.databinding.ItemContactBinding
import com.call.colorscreen.ledflash.model.ContactInfor
import java.util.*

class ContactAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listContactInfor: MutableList<ContactInfor> = mutableListOf()
    var listTemp: MutableList<ContactInfor> = mutableListOf()
    var context: Context? = null
    constructor(context: Context, listContactInfor: MutableList<ContactInfor>) : this() {
        this.context = context
        this.listTemp = listContactInfor
        this.listContactInfor.addAll(this.listTemp)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    fun search(value: CharSequence?) {
        listContactInfor.clear()
        if (TextUtils.isEmpty(value)) {
            listContactInfor.addAll(listTemp)
        } else {
            for (i in listTemp.indices) {
                val name: String = listTemp[i].displayName
                if (name.toLowerCase().contains(value!!)) {
                    listContactInfor.add(listTemp[i])
                }
            }
        }
        notifyDataSetChanged()
    }
    fun getContactSelected():MutableList<String> {
        val arrayList: MutableList<String> = mutableListOf()
        for (contactInfor in listTemp) {
            if (contactInfor.isChecked) {
                arrayList.add(contactInfor.contactId)
            }
        }
        return arrayList
    }
    inner class ViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var contactInfor: ContactInfor
        private lateinit var path: String
      fun onBind(position: Int){
          contactInfor = listContactInfor[position]
          Log.e("TAN", "onBind: "+contactInfor.photo )
          path = if (contactInfor.photo == null) {
              "file:///android_asset/avatar.webp"
          } else {
              contactInfor.photo
          }
          Glide.with(context!!).load(path).into(binding.imgAvatar)
          binding.txtName.text = contactInfor.displayName
          binding.imgSelectContact.isChecked = contactInfor.isChecked
          binding.layoutItem.setOnClickListener {
              binding.imgSelectContact.performClick()
              contactInfor.isChecked = binding.imgSelectContact.isChecked
          }
          binding.imgSelectContact.setOnClickListener { contactInfor.isChecked = binding.imgSelectContact.isChecked }
      }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).onBind(position)
    }

    override fun getItemCount(): Int {
        return listContactInfor.size
    }
}