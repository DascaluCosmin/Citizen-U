package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.events.CouncilMeetEvent
import com.ubb.citizen_u.databinding.CouncilMeetEventsListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.CouncilMeetEventViewHolder
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import com.ubb.citizen_u.util.SettingsConstants.LANGUAGE_SETTINGS_KEY

class CouncilMeetEventsAdapter(
    private val eventDetailsOnClickCallBack: (CouncilMeetEvent) -> Unit,
) : ListAdapter<CouncilMeetEvent, RecyclerView.ViewHolder>(EventsDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val settingsPreferences = PreferenceManager
            .getDefaultSharedPreferences(parent.context)
        val language = settingsPreferences
            .getString(LANGUAGE_SETTINGS_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE

        return CouncilMeetEventViewHolder(
            language = language,
            binding = CouncilMeetEventsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            eventDetailsOnClickCallBack = eventDetailsOnClickCallBack
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        (holder as CouncilMeetEventViewHolder).bind(event)
    }

    class EventsDiffCallBack : DiffUtil.ItemCallback<CouncilMeetEvent>() {
        override fun areItemsTheSame(
            oldItem: CouncilMeetEvent,
            newItem: CouncilMeetEvent,
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: CouncilMeetEvent,
            newItem: CouncilMeetEvent,
        ): Boolean {
            return oldItem == newItem
        }
    }
}

