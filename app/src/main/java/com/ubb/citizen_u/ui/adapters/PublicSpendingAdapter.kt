package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.PublicSpending
import com.ubb.citizen_u.databinding.PublicSpendingListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.PublicSpendingViewHolder
import com.ubb.citizen_u.ui.util.getCurrentLanguage

class PublicSpendingAdapter :
    ListAdapter<PublicSpending, RecyclerView.ViewHolder>(PublicSpendingDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PublicSpendingViewHolder(
            language = parent.context.getCurrentLanguage(),
            binding = PublicSpendingListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val publicSpending = getItem(position)
        (holder as PublicSpendingViewHolder).bind(publicSpending)
    }

    class PublicSpendingDiffCallBack : DiffUtil.ItemCallback<PublicSpending>() {
        override fun areItemsTheSame(
            oldItem: PublicSpending,
            newItem: PublicSpending,
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: PublicSpending,
            newItem: PublicSpending,
        ): Boolean {
            return oldItem == newItem
        }
    }
}