package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.databinding.ReportedIncidentListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.ReportedIncidentViewHolder
import com.ubb.citizen_u.ui.util.getCurrentLanguage

class ReportedIncidentsAdapter(
    private val reportedIncidentDetailsOnClickCallBack: (Incident) -> Unit,
) : ListAdapter<Incident, ViewHolder>(ReportedIncidentDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ReportedIncidentViewHolder(
            language = parent.context.getCurrentLanguage(),
            reportedIncidentListItemBinding = ReportedIncidentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            reportedIncidentDetailsOnClickCallBack = reportedIncidentDetailsOnClickCallBack
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incident = getItem(position)
        (holder as ReportedIncidentViewHolder).bind(incident)
    }

    class ReportedIncidentDiffCallBack : DiffUtil.ItemCallback<Incident>() {
        override fun areItemsTheSame(oldItem: Incident, newItem: Incident): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Incident, newItem: Incident): Boolean {
            return oldItem == newItem
        }
    }
}

