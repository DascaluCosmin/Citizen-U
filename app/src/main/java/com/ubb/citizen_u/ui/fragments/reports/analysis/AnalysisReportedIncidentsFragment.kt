package com.ubb.citizen_u.ui.fragments.reports.analysis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentAnalysisReportedIncidentsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants
import com.ubb.citizen_u.util.EMPTY_STRING
import com.ubb.citizen_u.util.isNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class AnalysisReportedIncidentsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-AnalysisReportedIncidentsFragment"
        private const val TOWN_HALL_LATITUDE_COORDINATE = 46.7687418
        private const val TOWN_HALL_LONGITUDE_COORDINATE = 23.5876332
        private const val ZOOM_WEIGHT = 14.0f
    }

    private lateinit var clusterManager: ClusterManager<IncidentClusterMarker>
    private lateinit var supportMapFragment: SupportMapFragment

    private var _binding: FragmentAnalysisReportedIncidentsBinding? = null
    private val binding: FragmentAnalysisReportedIncidentsBinding get() = _binding!!

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAnalysisReportedIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            fragmentAnalysisReportedIncidents = this@AnalysisReportedIncidentsFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectAllReportedIncidents() }
            }
        }

        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_maps_fragment) as SupportMapFragment

        setUpCluster()
    }

    override fun onStart() {
        super.onStart()
        citizenRequestViewModel.getAllReportedIncidents()
    }

    private suspend fun collectAllReportedIncidents() {
        citizenRequestViewModel.getAllReportedIncidentsState.collect {
            Log.d(TAG, "collectAllReportedIncidents: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(TAG, "collectAllReportedIncidents: An error has occurred: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    val markerItems = it.data.filterNotNull()
                        .filterNot { incident ->
                            incident.latitude.isNull() || incident.longitude.isNull()
                        }
                        .map { incident ->
                            IncidentClusterMarker(
                                latitude = incident.latitude!!,
                                longitude = incident.longitude!!,
                                title = incident.headline
                                    ?: CitizenRequestConstants.INCIDENT_GENERIC_HEADLINE,
                                snippet = incident.description ?: EMPTY_STRING
                            )
                        }.toList()

                    clusterManager.addItems(markerItems)
                    clusterManager.cluster()
                }
            }
        }
    }

    private fun setUpCluster() {
        supportMapFragment.getMapAsync { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    TOWN_HALL_LATITUDE_COORDINATE,
                    TOWN_HALL_LONGITUDE_COORDINATE
                ), ZOOM_WEIGHT
            ))

            clusterManager = ClusterManager<IncidentClusterMarker>(requireContext(), googleMap)
                .apply {
                    setOnClusterItemClickListener {
                        toastMessage("Clicked on item ${it.title}")
                        false
                    }

                    setOnClusterClickListener {
                        toastMessage("Clicked on cluster with ${it.size} items")
                        false
                    }
                }
            googleMap.setOnCameraIdleListener(clusterManager)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}