package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.databinding.EventsListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller

class EventViewHolder(
    private val eventsListItemBinding: EventsListItemBinding
) : RecyclerView.ViewHolder(eventsListItemBinding.root) {

    fun bind(event: Event) {
        eventsListItemBinding.apply {
            eventItemTitle.text = event.title
            eventItemAddress.text = event.address

            event.startDate?.let {
                eventItemStartDate.text = DateFormatter.toEventFormat(it)
            }
            event.endDate?.let {
                eventItemEndDate.text = DateFormatter.toEventFormat(it)
            }

            event.photos?.let { eventPhotos ->
                if (eventPhotos.size > 0) {
                    eventPhotos[0]?.let { eventPhoto ->
                        ImageFiller.fill(itemView.context, eventItemImage, eventPhoto)
                    }
                }
            }
        }
    }
}