package com.huangyuanlove.auxiliary

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.huangyuanlove.auxiliary.bean.Contact

class ContactAdapter(val data: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {


    var onItemClick: OnItemClick? = null


     class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val name: TextView = view.findViewById(R.id.name)
        val root: View = view.findViewById(R.id.root)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = data[position]
        with(holder) {
            if (contact.last) {
                name.text = "添加联系人"
                avatar.setImageResource(R.drawable.baseline_add_24)
            } else {
                name.text = contact.name
                Glide.with(avatar).load(contact.avatar)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
                    .into(avatar)

            }
            root.setOnClickListener {
                onItemClick?.onItemClick(contact)
            }

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

}

interface OnItemClick {
    fun onItemClick(contact: Contact)
}