package com.ubb.citizen_u.ui.adapters.viewholders

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.databinding.PublicReleaseEventsListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller

class PublicReleaseEventViewHolder(
    private val language: String,
    private val binding: PublicReleaseEventsListItemBinding,
    private val eventDetailsOnClickCallBack: (PublicReleaseEvent) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: PublicReleaseEvent, isEvenPosition: Boolean) {
        binding.apply {
            eventCategory.text = event.category[language]
            eventHeadline.text = event.headline[language]
            eventTitle.text = event.title[language]

            event.publicationDate?.let {
                eventPublicationDate.text = DateFormatter.format(it)
            }

            if (event.photos.size > 0) {
                event.photos[0]?.let { publicReleasePhoto ->
                    eventImage.visibility = View.VISIBLE
                    ImageFiller.fill(itemView.context, eventImage, publicReleasePhoto)

                    publicReleasePhoto.name?.let { photoName ->
                        eventImage.contentDescription = photoName
                    }
                }
            }

            eventCard.setOnClickListener {
                eventDetailsOnClickCallBack(event)
            }

            if (isEvenPosition) {
                eventCard.setCardBackgroundColor(Color.LTGRAY)
            }
        }
    }
}