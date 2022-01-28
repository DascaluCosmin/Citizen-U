package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.databinding.PublicEventsListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.PublicEventViewHolder

class PublicEventsAdapter(
    private val eventDetailsOnClickCallBack: (PublicEvent) -> Unit
) : ListAdapter<PublicEvent, RecyclerView.ViewHolder>(EventsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PublicEventViewHolder(
            eventsListItemBinding = PublicEventsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            eventsDetailsOnClickCallBack = eventDetailsOnClickCallBack
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        (holder as PublicEventViewHolder).bind(event)
    }

    class EventsDiffCallBack : DiffUtil.ItemCallback<PublicEvent>() {
        override fun areItemsTheSame(oldItem: PublicEvent, newItem: PublicEvent): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: PublicEvent, newItem: PublicEvent): Boolean {
            return oldItem == newItem
        }
    }
}

