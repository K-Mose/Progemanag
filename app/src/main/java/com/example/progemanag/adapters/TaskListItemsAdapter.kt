package com.example.progemanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progemanag.activities.TaskListActivity
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
            holder.binding.apply {
                if (position == list.size - 1) {
                    tvAddTaskList.visibility = View.VISIBLE
                    llTaskItem.visibility = View.GONE
                } else {
                    binding.tvAddTaskList.visibility = View.GONE
                    binding.llTaskItem.visibility = View.VISIBLE
                }
                tvTaskListTitle.text = model.title
                // Add Task Name
                tvAddTaskList.setOnClickListener {
                    tvAddTaskList.visibility = View.GONE
                    cvAddTaskListName.visibility = View.VISIBLE
                }
                ibCloseListName.setOnClickListener {
                    tvAddTaskList.visibility = View.VISIBLE
                    cvAddTaskListName.visibility = View.GONE
                }
                ibDoneListName.setOnClickListener {
                    val listName = etTaskListName.text.toString()

                    if(listName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.createTaskList(listName)
                        }
                    }
                }

                // Edit Task Name
                ibEditListName.setOnClickListener {
                    etEditTaskListName.setText(model.title)
                    llTitleView.visibility = View.GONE
                    cvEditTaskListName.visibility = View.VISIBLE
                }
                ibCloseEditableView.setOnClickListener {
                    llTitleView.visibility = View.VISIBLE
                    cvEditTaskListName.visibility = View.GONE
                }
                ibDoneEditListName.setOnClickListener {
                    val listName = etEditTaskListName.text.toString()

                    if(listName.isNotEmpty()) {
                        if (context is TaskListActivity){
                            context.updateTaskList(position, listName, model)
                        } else {
                            Toast.makeText(context, "Please Enter a List Name", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                ibDeleteList.setOnClickListener {
                    alterDialogForDelete(position, model.title)
                }

                // Add Card
                tvAddCard.setOnClickListener {
                    tvAddCard.visibility = View.GONE
                    cvAddCard.visibility = View.VISIBLE
                }
                ibCloseCardName.setOnClickListener{
                    tvAddCard.visibility = View.VISIBLE
                    cvAddCard.visibility = View.GONE
                }
                ibDoneCardName.setOnClickListener{
                    etCardName.text.toString().also {
                        if (it.isNotEmpty()) {
                            if (context is TaskListActivity) {
                                context.createCard(position, it)
                            }
                        } else {
                            Toast.makeText(context, "Please Enter a Card Name", Toast.LENGTH_SHORT).show()
                        }
                    }
                    tvAddCard.visibility = View.VISIBLE
                    cvAddCard.visibility = View.GONE
                }
                val adapter = CardListItemsAdapter(context, model.cardList)
                rvCardList.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    this.adapter = adapter
                }

            }
        }
    }

    // Alert Dialog
    private fun alterDialogForDelete(position: Int, title: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context)
                .setTitle("Alert")
                .setMessage("Are you sure you want to delete $title")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    if (context is TaskListActivity) {
                        context.deleteTaskList(position)
                    }
                }.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // dp from px & px from dp
    private fun Int.toDp(): Int = (this/ Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this* Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {

    }
}