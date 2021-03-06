package com.ubb.citizen_u.ui.fragments.multistep.reports

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentReportIncidentDetailsBinding
import com.ubb.citizen_u.ui.util.toastErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReportIncidentDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportIncidentDetailsFragment"
    }

    private var _binding: FragmentReportIncidentDetailsBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportIncidentDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportIncidentDetailsFragment = this@ReportIncidentDetailsFragment
        }
    }

    fun goNext() {
        Log.d(TAG, "Going next in multistep report incident...")
        val description =
            binding.reportIncidentDescriptionEdittext.editText?.text.toString()
                .trim { it <= ' ' }
        val headline =
            binding.reportIncidentHeadlineEdittext.editText?.text.toString()
                .trim { it <= ' ' }
        when {
            TextUtils.isEmpty(description) -> toastErrorMessage(
                getString(R.string.INVALID_REPORT_INCIDENT_DESCRIPTION_ERROR_MESSAGE)
            )

            TextUtils.isEmpty(headline) -> toastErrorMessage(
                getString(R.string.INVALID_REPORT_INCIDENT_HEADLINE_ERROR_MESSAGE)
            )

            else -> {
                val action =
                    ReportIncidentDetailsFragmentDirections
                        .actionReportIncidentFragmentToReportIncidentPhotoFragment(
                            incidentDescription = description,
                            incidentHeadline = headline
                        )
                findNavController().navigate(action)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
