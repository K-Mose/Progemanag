package com.example.progemanag.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.LabelColorListItemsAdapter
import com.example.progemanag.databinding.DialogListBinding
import com.example.progemanag.databinding.ItemLabelColorBinding

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {
    private var adapter: LabelColorListItemsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogListBinding.inflate(LayoutInflater.from(context))
        val view = binding.root //LayoutInflater.from(context).inflate(binding.root, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(binding)

    }

    private fun setupRecyclerView(binding: DialogListBinding) {
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        binding.rvList.adapter = adapter
        adapter!!.onItemClickListener =
            object : LabelColorListItemsAdapter.OnItemClickListener{
                override fun onClick(position: Int, color: String) {
                    dismiss()
                    onItemSelected(color)
                }
            }
    }

    protected abstract fun onItemSelected(color: String)
}