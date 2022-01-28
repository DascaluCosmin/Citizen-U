package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.databinding.PublicEventsListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.EventViewHolder

class EventsAdapter(
    private val eventsDetailsOnClickCallBack: (PublicEvent) -> Unit
) : ListAdapter<PublicEvent, RecyclerView.ViewHolder>(EventsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventViewHolder(
            eventsListItemBinding = PublicEventsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            eventsDetailsOnClickCallBack = eventsDetailsOnClickCallBack
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        (holder as EventViewHolder).bind(event)
    }
}

private class EventsDiffCallBack : DiffUtil.ItemCallback<PublicEvent>() {

    override fun areItemsTheSame(oldItem: PublicEvent, newItem: PublicEvent): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: PublicEvent, newItem: PublicEvent): Boolean {
        return oldItem == newItem
    }

}