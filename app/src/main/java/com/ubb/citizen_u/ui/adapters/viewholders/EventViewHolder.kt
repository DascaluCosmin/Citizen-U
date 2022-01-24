package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.databinding.EventsListItemBinding
import com.ubb.citizen_u.util.DateFormatter

class EventViewHolder(
    private val eventsListItemBinding: EventsListItemBinding
) : RecyclerView.ViewHolder(eventsListItemBinding.root) {

    fun bind(event: Event) {
        eventsListItemBinding.apply {
            eventItemTitle.text = event.title
            eventItemAddress.text = event.address

            if (event.startDate != null) {
                eventItemStartDate.text = DateFormatter.toEventFormat(event.startDate)
            }

            if (event.endDate != null) {
                eventItemEndDate.text = DateFormatter.toEventFormat(event.endDate)
            }
        }
    }
}