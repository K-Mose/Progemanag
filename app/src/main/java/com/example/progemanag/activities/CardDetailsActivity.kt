package com.example.progemanag.activities

import android.os.Bundle
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityCardDetailsBinding
import com.example.progemanag.models.Board
import com.example.progemanag.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var _binding: ActivityCardDetailsBinding

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        getIntentData()
        setupActionBar()
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(_binding.toolbarCardDetailsActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition]
                .name
        }
        _binding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            superOnBackPressed()
        }
    }

}