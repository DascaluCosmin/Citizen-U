package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.CouncilMeetEvent
import com.ubb.citizen_u.databinding.CouncilMeetEventsListItemBinding

class CouncilMeetEventViewHolder(
    private val binding: CouncilMeetEventsListItemBinding,
    private val eventDetailsOnClickCallBack: (CouncilMeetEvent) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: CouncilMeetEvent) {
        binding.apply {

            eventCard.setOnClickListener {
                eventDetailsOnClickCallBack(event)
            }
        }
    }
}