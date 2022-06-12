package com.ubb.citizen_u.ui.fragments.reports

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
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentGenerateReportsReportedIncidentsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.util.DateConverter
import com.ubb.citizen_u.util.ValidationConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
class GenerateReportsReportedIncidentsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-GenerateReportsReportedIncidentsFragment"
    }

    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var category: String
    private lateinit var neighborhood: String

    private var _binding: FragmentGenerateReportsReportedIncidentsBinding? = null
    private val binding: FragmentGenerateReportsReportedIncidentsBinding get() = _binding!!

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            FragmentGenerateReportsReportedIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val neighborhoods = resources.getStringArray(R.array.neighborhoods)
        val neighborhoodsArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.neighborhoods_dropdown,
            neighborhoods
        )

        binding.apply {
            generateReportsReportedIncidentsFragment = this@GenerateReportsReportedIncidentsFragment

            incidentNeighborhoodAutocompleteView.setAdapter(neighborhoodsArrayAdapter)
            (incidentCategoriesDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                category = citizenRequestViewModel.listIncidentCategories[position]
            }
            (incidentNeighborhoodDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                neighborhood = neighborhoods[position]
            }

            startDateCalendarView.apply {
                // Make sure startDate and endDate have a value (the midnight value of today)
                startDate = Date(date).apply {
                    DateConverter.convertToStartDate(this)
                }

                setOnDateChangeListener { _, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    startDate = calendar.time.apply {
                        DateConverter.convertToStartDate(this)
                    }
                }
            }
            endDateCalendarView.apply {
                endDate = Date(date).apply {
                    DateConverter.convertToEndDate(this)
                }

                setOnDateChangeListener { _, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    endDate = calendar.time.apply {
                        DateConverter.convertToEndDate(this)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectAllIncidentCategories() }
                launch { collectAllReportedIncidents() }
            }
        }

        category = getString(R.string.reports_category_all_hint)
        neighborhood = getString(R.string.reports_neighborhoods_all_hint)
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
                }
            }
        }
    }

    private suspend fun collectAllReportedIncidents() {
        citizenRequestViewModel.getAllReportedIncidentsState.collect {
            Log.d(TAG,
                "Collecting reported incidents for category = $category and neighborhood = $neighborhood. Start date = $startDate, end date = $endDate")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    Log.e(TAG, "An error has occurred: ${it.message}")
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    val incidents = it.data
                        .asSequence()
                        .filterNotNull()
                        .filter { incident ->
                            if (category == getString(R.string.reports_category_all_hint)) {
                                return@filter true
                            }
                            category == incident.category
                        }
                        .filter { incident ->
                            if (neighborhood == getString(R.string.reports_neighborhoods_all_hint)) {
                                return@filter true
                            }
                            neighborhood == incident.neighborhood
                        }
                        .filter { incident ->
                            if (incident.sentDate == null) {
                                return@filter false
                            }
                            incident.sentDate!!.after(startDate) && incident.sentDate!!.before(
                                endDate)
                        }.toList()
                    Log.d(TAG, "Number of filtered reported incidents is ${incidents.size}")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (citizenRequestViewModel.listIncidentCategories.isNullOrEmpty()) {
            citizenRequestViewModel.getAllIncidentCategories()
        } else {
            setCategoriesDropdown()
        }
    }

    private fun setCategoriesDropdown() {
        val incidentCategoriesArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.incident_categories_dropdown,
            citizenRequestViewModel.listIncidentCategories
        )
        val allCategoriesString = getString(R.string.reports_category_all_hint)
        if (!citizenRequestViewModel.listIncidentCategories.contains(allCategoriesString)) {
            citizenRequestViewModel.listIncidentCategories.add(0, allCategoriesString)
        }

        binding.incidentCategoriesAutocompleteView.setAdapter(incidentCategoriesArrayAdapter)
    }

    fun generatePDF() {
        Log.d(TAG, "The start date is $startDate, end date is $endDate")
        if (startDate.after(endDate)) {
            toastErrorMessage(ValidationConstants.START_DATE_AFTER_END_DATE)
            return
        }
        citizenRequestViewModel.getAllReportedIncidents()
    }

    fun viewIncidentsOnMap() {
        val action =
            GenerateReportsReportedIncidentsFragmentDirections.actionGenerateReportsReportedIncidentsFragmentToAnalysisReportedIncidentsFragment()
        binding.apply {
            (incidentCategoriesDropdown.editText as AutoCompleteTextView).editableText?.clear()
            (incidentNeighborhoodDropdown.editText as AutoCompleteTextView).editableText?.clear()
        }

        findNavController().navigate(action)
    }
}