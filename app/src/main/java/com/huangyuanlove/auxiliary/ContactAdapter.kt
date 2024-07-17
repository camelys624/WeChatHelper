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
import com.huangyuanlove.auxiliary.databinding.ItemContactBinding

class ContactAdapter(val data: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {


    var onItemClick: OnItemClick? = null


     class MyViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
         
         fun bindData(contact: Contact,onItemClick:OnItemClick?){
             if(contact.last){
                    binding.name.text = "添加联系人"
                 binding.avatar.setImageResource(R.drawable.baseline_add_24)
             }else{
                binding.name.text = contact.name
                 Glide.with(binding.avatar).load(contact.avatar)
                     .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
                     .into(binding.avatar)

             }
             binding.root.setOnClickListener{
                 onItemClick?.onItemClick(contact)
             }
         }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context))

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(data[position],onItemClick)

    }

    override fun getItemCount(): Int {
        return data.size
    }

}

interface OnItemClick {
    fun onItemClick(contact: Contact)
}