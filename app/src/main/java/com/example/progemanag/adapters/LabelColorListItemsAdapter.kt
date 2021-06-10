package com.example.progemanag.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progemanag.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLabelColorBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        Log.i("ItemColor::", "$item")
        if (holder is MyViewHolder) {
            // MaterialCompatTheme에서는 backgroundColor로하면 적용 안되고 Tint로 해야 배경 색 적용된다.
            holder.binding.viewMain.background.setTint(Color.parseColor(item))
            holder.binding.ivSelectColor.visibility =
                if (item == mSelectedColor) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener {
                onItemClickListener?.apply{
                    onClick(position, item)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int, color: String)
    }

    private class MyViewHolder(val binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root)

}