package com.example.progemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ItemBoardBinding
import com.example.progemanag.models.Board

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    private lateinit var binding: ItemBoardBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemBoardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(holder.binding.ivBoardImage)
            holder.binding.tvName.text = model.name
            holder.binding.tvCreatedBy.text = "Created by : ${model.createdBy}"

            holder.itemView.setOnClickListener {
                onClickListener?.apply {
                    onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    private class MyViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root){

    }
}