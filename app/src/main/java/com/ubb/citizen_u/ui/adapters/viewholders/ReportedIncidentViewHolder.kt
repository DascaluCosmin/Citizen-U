package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.databinding.ReportedIncidentListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller
import java.util.*

class ReportedIncidentViewHolder(
    private val language: String,
    private val reportedIncidentListItemBinding: ReportedIncidentListItemBinding,
    private val reportedIncidentDetailsOnClickCallBack: (Incident) -> Unit,
) : RecyclerView.ViewHolder(reportedIncidentListItemBinding.root) {

    fun bind(incident: Incident) {
        reportedIncidentListItemBinding.apply {

            reportedIncidentHeadline.text = incident.headline
            reportedIncidentAddress.text = incident.address

            reportedIncidentStatus.text = incident.status.toString().lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            incident.sentDate?.let {
                reportedIncidentSendDate.text = DateFormatter.toEventFormat(it)
            }

            incident.photos.let { reportedIncidentPhotos ->
                if (reportedIncidentPhotos.isNotEmpty()) {
                    reportedIncidentPhotos[0]?.let { reportedIncidentPhoto ->
                        ImageFiller.fill(itemView.context,
                            reportedIncidentImage,
                            reportedIncidentPhoto)
                    }
                }
            }

            reportedIncidentCard.setOnClickListener {
                reportedIncidentDetailsOnClickCallBack(incident)
            }
        }
    }
}