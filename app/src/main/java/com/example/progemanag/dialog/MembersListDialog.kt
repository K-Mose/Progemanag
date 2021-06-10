package com.example.progemanag.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.adapters.MemberListItemsAdapter
import com.example.progemanag.databinding.DialogListBinding
import com.example.progemanag.models.User

abstract class MembersListDialog(
    context: Context,
    val list: ArrayList<User>,
    val title: String
) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: DialogListBinding = DialogListBinding.inflate(LayoutInflater.from(context))
        val view = binding.root

        binding.tvTitle.text = title

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        setupRecyclerView(binding)

    }

    private fun setupRecyclerView(binding: DialogListBinding){
        binding.rvList.apply {
            adapter = MemberListItemsAdapter(context, list)
                .apply {
                    setOnClickListener (object : MemberListItemsAdapter.OnClickListener {
                        override fun onClick(position: Int, user: User, action: String) {
                            dismiss()
                            onItemSelected(user, action)
                        }
                    })
                }
            layoutManager = LinearLayoutManager(context)

        }
    }

    protected abstract fun onItemSelected(user: User, action: String)
}
