package com.ubb.citizen_u.ui.fragments.multistep.reports

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.databinding.FragmentReportIncidentDetailsBinding
import com.ubb.citizen_u.ui.fragments.toastErrorMessage
import com.ubb.citizen_u.util.ValidationConstants.INVALID_REPORT_INCIDENT_DESCRIPTION_ERROR_MESSAGE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReportIncidentDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportIncidentDetailsFragment"
    }

    private val args: ReportIncidentDetailsFragmentArgs by navArgs()

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

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch { collectReportIncidentState() }
//            }
//        }
    }

//    private suspend fun collectReportIncidentState() {
//        citizenRequestViewModel.addReportIncidentState.collect {
//            Log.d(TAG, "collectReportIncidentState: Collecting response $it")
//            when (it) {
//                is Response.Error -> {
//                    Log.d(TAG, "collectReportIncidentState: An error has occurred: ${it.message}")
//                    toastErrorMessage(it.message)
//                }
//                Response.Loading -> {
//                    binding.mainProgressbar.visibility = View.VISIBLE
//                }
//                is Response.Success -> {
//                    binding.mainProgressbar.visibility = View.GONE
//                    Toast.makeText(context, SUCCESSFUL_REPORT_INCIDENT, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    fun goNext() {
        Log.d(TAG, "goNext: Going next in multistep report incident...")
        val description =
            binding.reportIncidentDescriptionEdittext.editText?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(description) -> toastErrorMessage(
                INVALID_REPORT_INCIDENT_DESCRIPTION_ERROR_MESSAGE
            )

            else -> {
                val action =
                    ReportIncidentDetailsFragmentDirections
                        .actionReportIncidentFragmentToReportIncidentPhotoFragment(
                            incidentDescription = description,
                            citizenId = args.citizenId
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
