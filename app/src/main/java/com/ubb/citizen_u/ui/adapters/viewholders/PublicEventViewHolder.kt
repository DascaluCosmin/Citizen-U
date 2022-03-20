package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.databinding.PublicEventsListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller

class PublicEventViewHolder(
    private val language: String,
    private val eventsListItemBinding: PublicEventsListItemBinding,
    private val eventsDetailsOnClickCallBack: (PublicEvent) -> Unit,
) : RecyclerView.ViewHolder(eventsListItemBinding.root) {

    fun bind(publicEvent: PublicEvent) {
        eventsListItemBinding.apply {
            // TODO: In case of null, maybe use DEFAULT_LANGUAGE or translation API.
            eventItemTitle.text = publicEvent.title[language]
            eventItemAddress.text = publicEvent.address[language]
            eventItemCategory.text = publicEvent.category[language]

            publicEvent.startDate?.let {
                eventItemStartDate.text = DateFormatter.format(it)
            }
            publicEvent.endDate?.let {
                eventItemEndDate.text = DateFormatter.format(it)
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