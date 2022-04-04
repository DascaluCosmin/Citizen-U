package com.ubb.citizen_u.ui.fragments.reports.analysis

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class IncidentClusterMarker(
    latitude: Double,
    longitude: Double,
    private val title: String,
    private val snippet: String,
) : ClusterItem {

    private val position: LatLng = LatLng(latitude, longitude)

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }
}