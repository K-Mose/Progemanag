package com.example.progemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ItemMemberBinding
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemMemberBinding
    private var onClickListener: OnClickListener? = null

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

                ivSelectMember.visibility = if (model.selected) View.VISIBLE else View.GONE
            }
            holder.itemView.setOnClickListener {
                onClickListener?.apply {
                    if (model.selected)
                        onClick(position, model, Constants.UN_SELECT)
                    else
                        onClick(position, model, Constants.SELECT)
                }

            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
}