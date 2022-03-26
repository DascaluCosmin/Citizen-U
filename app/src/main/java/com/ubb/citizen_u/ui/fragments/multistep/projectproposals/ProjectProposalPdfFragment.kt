package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentProjectProposalPdfBinding
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectProposalPdfFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProjectProposalPdfFragment"
    }

    private var _binding: FragmentProjectProposalPdfBinding? = null
    private val binding: FragmentProjectProposalPdfBinding get() = _binding!!

    private val uploadPdfResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val uri = intent.data
                    Log.d(TAG,
                        "The Pdf file has been upload successfully in the application's memory. Uri = $uri")
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProjectProposalPdfBinding.inflate(inflater, container, false)

        binding.apply {
            projectProposalPdfFragment = this@ProjectProposalPdfFragment
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun uploadPdfFile() {
        Log.d(TAG, "Uploading PDF file...")
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT

        try {
            uploadPdfResultLauncher.launch(intent)
        } catch (exception: Exception) {
            Log.e(TAG, "An exception has been thrown: ${exception.message}")
            toastMessage(DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN)
        }
    }
}