package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.databinding.EventsListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller

class EventViewHolder(
    private val eventsListItemBinding: EventsListItemBinding,
    private val eventsDetailsOnClickCallBack: (PublicEvent) -> Unit
) : RecyclerView.ViewHolder(eventsListItemBinding.root) {

    fun bind(publicEvent: PublicEvent) {
        eventsListItemBinding.apply {
            eventItemTitle.text = publicEvent.title
            eventItemAddress.text = publicEvent.address

            publicEvent.startDate?.let {
                eventItemStartDate.text = DateFormatter.toEventFormat(it)
            }
            publicEvent.endDate?.let {
                eventItemEndDate.text = DateFormatter.toEventFormat(it)
            }

            publicEvent.photos.let { eventPhotos ->
                if (eventPhotos.isNotEmpty()) {
                    eventPhotos[0]?.let { eventPhoto ->
                        ImageFiller.fill(itemView.context, eventItemImage, eventPhoto)
                    }
                }
            }

            eventCard.setOnClickListener {
                eventsDetailsOnClickCallBack(publicEvent)
            }
        }
    }
}