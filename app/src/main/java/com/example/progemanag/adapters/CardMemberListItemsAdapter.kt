package com.example.progemanag.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ItemCardSelectedMemberBinding
import com.example.progemanag.models.SelectedMembers

data class CardMemberListItemsAdapter (
    private val context: Context,
    private val list: ArrayList<SelectedMembers>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("getSize", "${list.size}")
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.binding.apply{
                if(position == list.size - 1) {
                    ivAddMember.visibility = View.VISIBLE
                    ivSelectedMemberImage.visibility = View.GONE
                } else {
                    ivAddMember.visibility = View.GONE
                    ivSelectedMemberImage.visibility = View.VISIBLE

                    Glide
                        .with(context)
                        .load(model.image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(holder.binding.ivSelectedMemberImage)
                }
            }
            holder.itemView.setOnClickListener {
                onClickListener?.apply {
                    onClick()
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }

    class MyViewHolder(val binding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root)
}