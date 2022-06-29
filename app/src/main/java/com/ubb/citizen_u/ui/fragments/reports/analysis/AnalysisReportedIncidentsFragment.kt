package com.ubb.citizen_u.ui.fragments.reports.analysis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.CustomClustererRenderer
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.databinding.FragmentAnalysisReportedIncidentsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants
import com.ubb.citizen_u.util.TownHallConstants.TOWN_HALL_LATITUDE_COORDINATE
import com.ubb.citizen_u.util.TownHallConstants.TOWN_HALL_LONGITUDE_COORDINATE
import com.ubb.citizen_u.util.TownHallConstants.ZOOM_WEIGHT
import com.ubb.citizen_u.util.isNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class AnalysisReportedIncidentsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-AnalysisReportedIncidentsFragment"
    }

    private lateinit var clusterManager: ClusterManager<IncidentClusterMarker>
    private lateinit var supportMapFragment: SupportMapFragment
    private var category: String? = null

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

            (incidentCategoriesDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                category = citizenRequestViewModel.listIncidentCategories[position]
                Log.d(TAG, "Selected the $category category")

                citizenRequestViewModel.getAllReportedIncidents()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectAllIncidentCategories() }
                launch { collectAllReportedIncidents() }
            }
        }

        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_maps_fragment) as SupportMapFragment

        setUpCluster()
    }

    override fun onStart() {
        super.onStart()
        if (citizenRequestViewModel.listIncidentCategories.isNullOrEmpty()) {
            citizenRequestViewModel.getAllIncidentCategories()
        } else {
            setCategoriesDropdown()
            citizenRequestViewModel.getAllReportedIncidents()
        }
    }

    private suspend fun collectAllIncidentCategories() {
        citizenRequestViewModel.incidentCategoriesState.collect {
            Log.d(TAG, "collectAllIncidentCategories: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(TAG, "collectAllIncidentCategories: An error has occurred: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    setCategoriesDropdown()
                    citizenRequestViewModel.getAllReportedIncidents()
                }
            }
        }
    }

    private fun setCategoriesDropdown() {
        val incidentCategoriesArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.incident_categories_dropdown,
            citizenRequestViewModel.listIncidentCategories
        )
        binding.incidentCategoriesAutocompleteView.setAdapter(incidentCategoriesArrayAdapter)
    }

    private suspend fun collectAllReportedIncidents() {
        citizenRequestViewModel.getAllReportedIncidentsState.collect {
            consumeReportedIncidentsResponse(it, category)
        }
    }

    private fun consumeReportedIncidentsResponse(
        it: Response<List<Incident?>>,
        category: String? = null,
    ) {
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
                val markerItems = it.data.asSequence()
                    .filterNotNull()
                    .filter {
                        // TODO: Setting to all categories by default will cause some clusters, TB decided
                        if (category == null || category == getString(R.string.reports_category_all_hint)) {
                            return@filter true
                        }
                        it.category == category
                    }
                    .filterNot { incident ->
                        incident.latitude.isNull() || incident.longitude.isNull()
                    }
                    .map { incident ->
                        IncidentClusterMarker(
                            latitude = incident.latitude!!,
                            longitude = incident.longitude!!,
                            title = incident.headline
                                ?: CitizenRequestConstants.INCIDENT_GENERIC_HEADLINE,
                            category = incident.category.toString()
                        )
                    }.toList()

                Log.d(TAG, "Setting cluster with ${markerItems.size} items")
                setUpCluster()

                clusterManager.algorithm.maxDistanceBetweenClusteredItems = 30
                clusterManager.addItems(markerItems)
                clusterManager.cluster()
            }
        }
    }

    private fun setUpCluster() {
        supportMapFragment.getMapAsync { googleMap ->
            googleMap.clear()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    TOWN_HALL_LATITUDE_COORDINATE,
                    TOWN_HALL_LONGITUDE_COORDINATE
                ), ZOOM_WEIGHT
            ))

            clusterManager = ClusterManager<IncidentClusterMarker>(requireContext(), googleMap)
                .apply {
                    setOnClusterItemClickListener {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(it.position))

                        toastMessage("Clicked on item ${it.title}")
                        false
                    }

                    setOnClusterClickListener {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(it.position))

                        toastMessage("Clicked on cluster with ${it.size} items")
                        false
                    }

                    renderer = CustomClustererRenderer(
                        context = requireContext(),
                        mMap = googleMap,
                        clusterManager = this
                    )
                }
            googleMap.setOnCameraIdleListener(clusterManager)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}