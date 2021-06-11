package com.example.progemanag.adapters

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progemanag.activities.TaskListActivity
import com.example.progemanag.databinding.ItemCardBinding
import com.example.progemanag.models.Card
import com.example.progemanag.models.SelectedMembers


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
            Log.e("SelectedColor::","${model.labelColor}")
            holder.binding.apply {
                tvCardName.text = model.name
                if (model.labelColor != ""){
                    viewLabelColor.apply{
                        visibility = View.VISIBLE
                        setBackgroundColor(Color.parseColor(model.labelColor))
                    }
                } else {
                    viewLabelColor.visibility = View.GONE
                }

                if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0) {
                    val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                    for (i in context.mAssignedMemberDetailList.indices) {
                        for (j in model.assignedTo) {
                            if (context.mAssignedMemberDetailList[i].id == j) {
                                val selectedMembers = SelectedMembers(
                                    context.mAssignedMemberDetailList[i].id,
                                    context.mAssignedMemberDetailList[i].image
                                )
                                selectedMembersList.add(selectedMembers)
                            }
                        }
                    }
                    if (selectedMembersList.size > 0) {
                        rvCardSelectedMembersList.also { rv ->
                            rv.visibility = if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) View.GONE
                                else View.VISIBLE
                            rv.layoutManager = GridLayoutManager(context, 4)
                            rv.adapter = CardMemberListItemsAdapter(context, selectedMembersList, false).also {
                                it.setOnClickListener(object : CardMemberListItemsAdapter.OnClickListener {
                                    override fun onClick() {
                                        if (onClickListener != null) {
                                            onClickListener!!.onClick(position)
                                        }
                                    }
                                })
                            }
                        }
                    }
                } else {
                    rvCardSelectedMembersList.visibility = View.GONE
                }
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