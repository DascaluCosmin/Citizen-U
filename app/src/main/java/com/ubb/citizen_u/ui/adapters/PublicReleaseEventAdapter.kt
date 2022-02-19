package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.databinding.PublicReleaseEventsListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.PublicReleaseEventViewHolder
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import com.ubb.citizen_u.util.SettingsConstants.LANGUAGE_SETTINGS_KEY

class PublicReleaseEventAdapter(
    private val eventDetailsOnClickCallBack: (PublicReleaseEvent) -> Unit,
) : ListAdapter<PublicReleaseEvent, RecyclerView.ViewHolder>(EventsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val settingsPreferences = PreferenceManager
            .getDefaultSharedPreferences(parent.context)
        val language = settingsPreferences
            .getString(LANGUAGE_SETTINGS_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE

        return PublicReleaseEventViewHolder(
            language = language,
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
        (holder as PublicReleaseEventViewHolder).bind(event)
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

