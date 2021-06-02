package com.example.progemanag.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.progemanag.databinding.ItemTaskBinding
import com.example.progemanag.models.Task

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var binding: ItemTaskBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(15.toDp().toPx(),0, 40.toDp().toPx(), 0)
        binding.root.layoutParams = layoutParams
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            if (position == list.size - 1) {
                binding.tvAddTaskList.visibility = View.VISIBLE
                binding.llTaskItem.visibility = View.GONE
            } else {
                binding.tvAddTaskList.visibility = View.GONE
                binding.llTaskItem.visibility = View.VISIBLE
            }
        }
    }

    // dp from px & px from dp
    private fun Int.toDp(): Int = (this/ Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this* Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {

    }
}