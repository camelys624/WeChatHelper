package com.huangyuanlove.auxiliary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.huangyuanlove.auxiliary.bean.Contact

class AvatarAdapter(private val avatarList: List<Int>) :
    RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {
    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)

        return AvatarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarResId = avatarList[position]
        holder.avatarImageView.setImageResource(avatarResId)
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }
}