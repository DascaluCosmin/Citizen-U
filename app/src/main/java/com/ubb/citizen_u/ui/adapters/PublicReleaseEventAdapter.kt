package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.databinding.PublicReleaseEventsListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.PublicReleaseEventViewHolder
import com.ubb.citizen_u.ui.util.getCurrentLanguage

class PublicReleaseEventAdapter(
    private val eventDetailsOnClickCallBack: (PublicReleaseEvent) -> Unit,
) : ListAdapter<PublicReleaseEvent, RecyclerView.ViewHolder>(EventsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PublicReleaseEventViewHolder(
            language = parent.context.getCurrentLanguage(),
            binding = PublicReleaseEventsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            eventDetailsOnClickCallBack = eventDetailsOnClickCallBack
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        (holder as PublicReleaseEventViewHolder).bind(event, position % 2 == 0)
    }

    class EventsDiffCallBack : DiffUtil.ItemCallback<PublicReleaseEvent>() {
        override fun areItemsTheSame(
            oldItem: PublicReleaseEvent,
            newItem: PublicReleaseEvent,
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: PublicReleaseEvent,
            newItem: PublicReleaseEvent,
        ): Boolean {
            return oldItem == newItem
        }
    }
}

