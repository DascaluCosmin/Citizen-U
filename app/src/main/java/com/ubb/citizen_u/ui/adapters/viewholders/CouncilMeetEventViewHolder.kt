package com.ubb.citizen_u.ui.adapters.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.CouncilMeetEvent
import com.ubb.citizen_u.databinding.CouncilMeetEventsListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller

class CouncilMeetEventViewHolder(
    private val binding: CouncilMeetEventsListItemBinding,
    private val eventDetailsOnClickCallBack: (CouncilMeetEvent) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: CouncilMeetEvent) {
        binding.apply {
            eventCategory.text = event.category
            eventHeadline.text = event.headline
            eventTitle.text = event.title

            event.publicationDate?.let {
                eventPublicationDate.text = DateFormatter.toEventFormat(it)
            }

            if (event.photos.size > 0) {
                event.photos[0]?.let {
                    binding.eventImage.visibility = View.VISIBLE
                    ImageFiller.fill(itemView.context, binding.eventImage, it)
                }
            }

            eventCard.setOnClickListener {
                eventDetailsOnClickCallBack(event)
            }
        }
    }
}