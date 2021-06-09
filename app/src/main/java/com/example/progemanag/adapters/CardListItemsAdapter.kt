package com.example.progemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progemanag.databinding.ItemCardBinding
import com.example.progemanag.models.Card


open class CardListItemsAdapter(
        private val context: Context,
        private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemCardBinding
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.binding.apply {
                tvCardName.text = model.name

            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position)
                }
            }

        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    class MyViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)
}