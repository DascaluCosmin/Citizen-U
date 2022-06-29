package com.ubb.citizen_u.ui.fragments.reports

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.databinding.FragmentGenerateReportsReportedIncidentsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.util.DateConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

@ExperimentalCoroutinesApi
class GenerateReportsReportedIncidentsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-GenerateReportsReportedIncidentsFragment"

        private const val PERMISSION_REQUEST_CODE = 200
        private const val PDF_HEIGHT = 1120
        private const val PDF_WIDTH = 792
        private const val PDF_NUMBER_OF_PAGES = 1
        private const val LOGO_WIDTH = 75
        private const val LOGO_HEIGHT = 150
        private const val TITLE_TEXT_SIZE = 18f
        private const val CONTENT_TEXT_SIZE = 16f
    }

    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var category: String
    private lateinit var neighborhood: String
    private lateinit var scaledBitMap: Bitmap

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

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
        scaledBitMap = Bitmap.createScaledBitmap(bitmap, LOGO_WIDTH, LOGO_HEIGHT, false)

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
                    generatePdfReport(incidents)
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
        if (!checkPermission()) {
            toastErrorMessage(getString(R.string.PROVIDE_STORAGE_PERMISSIONS_CTA))
            requestPermission()
            return
        }

        Log.d(TAG, "The start date is $startDate, end date is $endDate")
        if (startDate.after(endDate)) {
            toastErrorMessage(getString(R.string.START_DATE_AFTER_END_DATE))
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

    private fun generatePdfReport(reportedIncidents: List<Incident>) {
        val pdf = PdfDocument()

        val paintShapes = Paint()
        val paintContent = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = TITLE_TEXT_SIZE
            color = ContextCompat.getColor(requireContext(), R.color.primaryDarkColor)
        }

        val pdfPageInfo = PdfDocument.PageInfo
            .Builder(PDF_WIDTH, PDF_HEIGHT, PDF_NUMBER_OF_PAGES)
            .create()

        val pdfPage = pdf.startPage(pdfPageInfo)
        val canvas = pdfPage.canvas

        canvas.drawBitmap(
            scaledBitMap,
            60f,
            30f,
            paintShapes
        )

        val formattedStartDateString = DateConverter.convertToFormattedDateString(startDate)
        val formattedEndDateString = DateConverter.convertToFormattedDateString(endDate)
        val timeIntervalString =
            "${getString(R.string.generic_time_interval_label)}: $formattedStartDateString - $formattedEndDateString"
        canvas.drawText(getString(R.string.city_hall_full_Name), 300f, 50f, paintContent)
        canvas.drawText(getString(R.string.generated_report_text), 380f, 80f, paintContent)
        canvas.drawText(timeIntervalString, 310f, 110f, paintContent)
        canvas.drawText("(${getString(R.string.generic_category_label)}: $category, ${getString(R.string.generic_neighborhood_label)}: $neighborhood)",
            235f,
            140f,
            paintContent)

        paintContent.color = ContextCompat.getColor(requireContext(), R.color.black)
        paintContent.textSize = CONTENT_TEXT_SIZE
        canvas.drawText("${reportedIncidents.size} ${getString(R.string.reports_found)}",
            60f,
            210f,
            paintContent)

        var startY = 240f
        val stepY = 30f
        for ((index, incident) in reportedIncidents.withIndex()) {
            val postedOnFormattedString =
                DateConverter.convertToFormattedDateString(incident.sentDate!!)
            val postedOnLabel = getString(R.string.generic_posted_on_label).replaceFirstChar {
                it.lowercase(Locale.getDefault())
            }
            val content =
                "#${index + 1}: ${incident.headline}, ${incident.address}, ${incident.status} , $postedOnLabel $postedOnFormattedString"
            canvas.drawText(content, 60f, startY, paintContent)
            startY += stepY
        }

        pdf.finishPage(pdfPage)

        val formattedCategory = category.replace(" ", "_")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val formattedNeighborhood = neighborhood.replace(" ", "_")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val fileName = "generatedReport$formattedCategory$formattedNeighborhood"

        val file = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "$fileName.pdf"
        )
        Log.d(TAG, "Generated file name is $file")

        try {
            pdf.writeTo(FileOutputStream(file))
            toastMessage(getString(R.string.SUCCESSFUL_REPORT_GENERATED))
        } catch (exception: Exception) {
            Log.e(TAG, "An error has occurred while generating pdf report: ${exception.message}")
            toastErrorMessage()
        }
        pdf.close()
    }

    private fun checkPermission(): Boolean {
        val writePermission =
            ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)
        val readPermission =
            ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val requiredPermissions = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(
            requireActivity(),
            requiredPermissions,
            PERMISSION_REQUEST_CODE
        )
    }
}