package com.example.progemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ItemMemberBinding
import com.example.progemanag.models.User

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemMemberBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemMemberBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            holder.binding.apply {
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(ivMemberImage)
                tvMemberEmail.text = model.email
                tvMemberName.text = model.name
            }


        }
    }

    class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
}