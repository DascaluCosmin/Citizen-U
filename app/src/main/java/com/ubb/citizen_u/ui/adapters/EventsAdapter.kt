package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.databinding.EventsListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.EventViewHolder

class EventsAdapter(
    private val eventsDetailsOnClickCallBack: (Event) -> Unit
) : ListAdapter<Event, RecyclerView.ViewHolder>(EventsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventViewHolder(
            eventsListItemBinding = EventsListItemBinding.inflate(
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

private class EventsDiffCallBack : DiffUtil.ItemCallback<Event>() {

    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }

}