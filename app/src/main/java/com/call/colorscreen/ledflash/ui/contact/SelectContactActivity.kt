package com.call.colorscreen.ledflash.ui.contact

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.RoomDatabaseHelper
import com.call.colorscreen.ledflash.database.RoomManager
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivitySelectContactBinding
import com.call.colorscreen.ledflash.model.ContactInfor
import com.call.colorscreen.ledflash.util.*
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import java.util.*

class SelectContactActivity : BaseActivity<
        ActivitySelectContactBinding>(), View.OnClickListener, PermissionCallListener {
    private lateinit var theme: Theme
    private lateinit var adapter: ContactAdapter
    val selectModel by inject<SelectContactModel>()
    val db by inject<RoomDatabaseHelper>()
    private var isSearchShow = false
    override fun getLayoutId(): Int {
        return R.layout.activity_select_contact
    }

    override fun onViewReady(savedInstance: Bundle?) {
        setTranslucent()
        init()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnBack -> {
                finish()
            }
            R.id.layoutSet -> {
                PermissionUtil.checkPermissionCall(this, this)
            }
            R.id.imgSearch ->{
                Log.e("TAN", "onClick: search")
                binding.layoutHeader2.visibility = View.VISIBLE
                binding.header1.visibility = View.GONE
                isSearchShow = true
                showSearch()
            }
        }
    }
    fun showSearch() {
        binding.edtSearch.isFocusable = true
        binding.edtSearch.isFocusableInTouchMode = true
        binding.edtSearch.requestFocus()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.showSoftInput(binding.edtSearch, 0)
        }
    }
    fun setTranslucent() {
        val w = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
            AppUtil.overHeaderApply(this, binding.layoutHeader)
        }
    }

    private fun init() {
        val gson = Gson()
        theme =
            gson.fromJson(intent.getStringExtra(Constant.THEME), Theme::class.java)
        val pathFile: String
        if (theme.path_thumb != "") {
            pathFile = if (theme.path_file.contains("default")) {
                "file:///android_asset/" + theme.path_thumb
            } else {
                theme.path_thumb
            }
            Glide.with(applicationContext)
                .load(pathFile)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.1f)
                .into(binding.imgBg)
        }
        getAllContact()
        binding.edtSearch.addTextChangedListener(EditTextListener())
        binding.layoutSet.setOnClickListener(this)
        binding.imgSearch.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    @SuppressLint("Range")
    private fun getAllContact() {
        var linkedHashSet = LinkedHashSet<ContactInfor>()
        try {
            val contentResolver = contentResolver
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val infors = arrayOf("contact_id", "display_name", "data1", "photo_uri")
            val query = contentResolver.query(uri, infors, null, null, "sort_key")
            if (query != null) {
                while (query.moveToNext()) {
                    val contact_id = query.getString(query.getColumnIndex(infors[0]))
                    val display_name = query.getString(query.getColumnIndex(infors[1]))
                    val data1 = query.getString(query.getColumnIndex(infors[2]))
                    val photo_uri = query.getString(query.getColumnIndex(infors[3]))
                    if (!linkedHashSet.contains(
                            ContactInfor(
                                contact_id,
                                display_name,
                                data1,
                                photo_uri
                            )
                        )
                    ) {
                        linkedHashSet.add(ContactInfor(contact_id, display_name, data1, photo_uri))
                    }
                }
                query.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val list: ArrayList<ContactInfor> = ArrayList(linkedHashSet)
        val arrListContactInfor: MutableList<ContactInfor>  = mutableListOf()
        arrListContactInfor.addAll(list)
        RoomManager.get().liveContactList(theme.path_file).observe(this) {

            val listContactDB:MutableList<Contact> = it
            for (i in listContactDB.indices) {
                val it: Iterator<*> = arrListContactInfor.iterator()
                while (true) {
                    if (!it.hasNext()) {
                        break
                    }
                    val contactInforInfor: ContactInfor =
                        it.next() as ContactInfor
                    if (contactInforInfor.contactId == listContactDB[i].contact_id) {
                        contactInforInfor.isChecked = true
                        break
                    }
                }
            }
            adapter = ContactAdapter(this, arrListContactInfor)
            binding.rvContact.adapter = adapter
        }
    }

    inner class EditTextListener : TextWatcher {
        override fun afterTextChanged(editable: Editable) {}
        override fun beforeTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence,
            start: Int,
            before: Int,
            after: Int
        ) {
            binding.imgClear.visibility =
                if (TextUtils.isEmpty(charSequence)) View.GONE else View.VISIBLE
            adapter.search(charSequence)
        }
    }

    fun setThemetoContactId() {
        HawkData.setEnableCall(true)
        val listContactIdSelected: List<String> = adapter.getContactSelected()
        RoomManager.get().liveContactList(theme.path_file).observe(this) { it ->
            Log.e("TAN", "list contact: "+it )
            val listContactDB:MutableList<Contact> = it
            for (i in listContactDB.indices) {
                val contactSelect: String = listContactDB[i].contact_id
                if (!listContactIdSelected.contains(contactSelect)) {
                    selectModel.deleteContact(db,contactSelect)
                }
            }
            val item = listContactIdSelected.iterator()
            Log.e("TAN", "setThemetoContactId: "+listContactIdSelected.size )
            var contact: Contact
            while (item.hasNext()) {
                val contactID = item.next()
                selectModel.listContactLiveData.observe(this) {
                    val listQueryContactID: List<Contact> = it!!
                    if (listQueryContactID.isNotEmpty()) {
                        Log.e("TAN", "setThemetoContactId: update" )
                        contact = listQueryContactID[0]
                        contact.theme_path = theme.path_file
                        contact.theme = Gson().toJson(theme)
                        selectModel.updateContact(db,contact)
                    } else {
                        Log.e("TAN", "setThemetoContactId: insert" )

                        contact =
                            Contact(contactID, theme.path_file, Gson().toJson(theme))
                        selectModel.insertDb(db,contact)
                    }
                }
                Log.e("TAN", "dkien: contactID "+contactID )

                selectModel.getListContact(db,contactID)
            }
            Toast.makeText(this, getString(R.string.set_theme_success), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    override fun onHasCall() {
        Log.e("TAN", "onHasCall: ")
        setThemetoContactId()
    }
}