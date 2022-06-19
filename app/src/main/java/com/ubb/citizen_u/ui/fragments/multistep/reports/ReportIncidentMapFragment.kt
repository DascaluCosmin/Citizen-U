package com.ubb.citizen_u.ui.fragments.multistep.reports

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentReportIncidentMapBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.TownHallConstants.TOWN_HALL_LATITUDE_COORDINATE
import com.ubb.citizen_u.util.TownHallConstants.TOWN_HALL_LONGITUDE_COORDINATE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
class ReportIncidentMapFragment : Fragment() {

    companion object {
        private const val TAG = "ReportIncidentMapFragment"
        private const val ZOOM_WEIGHT = 15.0f
    }

    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val args: ReportIncidentMapFragmentArgs by navArgs()

    private var _binding: FragmentReportIncidentMapBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportIncidentMapBinding.inflate(inflater, container, false)

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_maps_fragment) as SupportMapFragment

        supportMapFragment.getMapAsync { googleMap ->
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    TOWN_HALL_LATITUDE_COORDINATE,
                    TOWN_HALL_LONGITUDE_COORDINATE
                ), ZOOM_WEIGHT
            ))

            googleMap.setOnMapClickListener { latLng ->
                val latitude = latLng.latitude
                val longitude = latLng.longitude

                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("${latLng.latitude} : ${latLng.longitude}")

                // Reverse Geo-Coding
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val addressList: List<Address> =
                        geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addressList.isNullOrEmpty()) {
                        val reversedGeocodedAddress = addressList.first()
                        val firstAddressLine =
                            reversedGeocodedAddress.thoroughfare + ", " + reversedGeocodedAddress.subThoroughfare
                        if (firstAddressLine.isNotEmpty()) {
                            markerOptions.title(firstAddressLine)
                            citizenRequestViewModel.incidentAddress = firstAddressLine
                            citizenRequestViewModel.incidentLatitude = latitude
                            citizenRequestViewModel.incidentLongitude = longitude
                        }
                    }
                } catch (exception: Exception) {
                    Log.e(TAG, "onCreateView: Error at doing reverse geo-coding")
                    toastErrorMessage(getString(R.string.FAILED_ADDRESS_COMPUTING))
                }

                googleMap.clear()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_WEIGHT))
                googleMap.addMarker(markerOptions)?.run {
                    // Make sure the marker title is displayed without needing to click it
                    showInfoWindow()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportIncidentMapFragment = this@ReportIncidentMapFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectReportIncidentState() }
            }
        }
    }

    private suspend fun collectReportIncidentState() {
        citizenRequestViewModel.addReportIncidentState.collect {
            Log.d(TAG, "collectReportIncidentState: Collecting response $it...")
            when (it) {
                is Response.Error -> {
                    Log.d(TAG, "collectReportIncidentState: An error has occurred ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage(it.message)
                }
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    toastMessage(getString(R.string.SUCCESSFUL_REPORT_INCIDENT))
                    goToUserProfile()
                }
            }
        }
    }

    private fun goToUserProfile() {
        val action =
            ReportIncidentMapFragmentDirections.actionReportIncidentMapFragmentToSignedInFragment(
                citizenId = citizenViewModel.citizenId,
                periodicEventDetailsId = null
            )
        findNavController().navigate(action)
    }

    fun sendIncidentReport() {
        Log.d(TAG, "sendIncidentReport: Sending incident report...")
        citizenRequestViewModel.reportIncident(
            description = args.incidentDescription,
            headline = args.incidentHeadline,
            citizenId = citizenViewModel.citizenId,
            citizen = citizenViewModel.currentCitizen
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}