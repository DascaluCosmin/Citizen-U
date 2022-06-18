package com.ubb.citizen_u.ui.adapters.viewholders

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.PublicSpending
import com.ubb.citizen_u.databinding.PublicSpendingListItemBinding
import com.ubb.citizen_u.util.DateConverter

class PublicSpendingViewHolder(
    private val language: String,
    private val binding: PublicSpendingListItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(publicSpending: PublicSpending, isEvenPosition: Boolean) {
        binding.apply {
            publicSpendingTitle.text = publicSpending.title[language]
            publicSpendingStatus.text = publicSpending.status[language]
            publicSpendingCategory.text = publicSpending.category[language]
            publicSpendingValue.text = "${publicSpending.getFormattedValue()} lei"
            publicSpendingCompletionDate.text =
                DateConverter.convertToFormattedDateString(publicSpending.endDate)

            if (isEvenPosition) {
                publicSpendingCard.setCardBackgroundColor(Color.LTGRAY)
            }
        }
    }
}