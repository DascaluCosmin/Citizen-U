package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentProjectProposalGeneralBinding
import com.ubb.citizen_u.ui.util.getDefaultLocalizedArrayStringResource
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.util.ValidationConstants.INVALID_PROJECT_CATEGORY_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.ValidationConstants.INVALID_PROJECT_DESCRIPTION_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.ValidationConstants.INVALID_PROJECT_LOCATION_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.ValidationConstants.INVALID_PROJECT_MOTIVATION_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.ValidationConstants.INVALID_PROJECT_TITLE_TEXT_ERROR_MESSAGE

class ProjectProposalGeneralFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProjectProposalGeneralFragment"
    }

    private var _binding: FragmentProjectProposalGeneralBinding? = null
    private val binding: FragmentProjectProposalGeneralBinding get() = _binding!!

    private var unlocalizedProjectCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProjectProposalGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = resources.getStringArray(R.array.project_categories)
        val categoriesArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.project_proposal_categories_dropdown,
            categories
        )

        binding.apply {
            fragmentProjectProposalGeneral = this@ProjectProposalGeneralFragment
            projectCategoriesAutocompleteTextview.setAdapter(categoriesArrayAdapter)
            (projectCategoryDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                unlocalizedProjectCategory =
                    getDefaultLocalizedArrayStringResource(R.array.project_categories)[position]
            }
        }
    }

    fun goToAttachmentsDetails() {
        binding.run {
            val projectTitle = projectTitleEdittext.editText?.text.toString().trim { it <= ' ' }
            val projectMotivation =
                projectMotivationEdittext.editText?.text.toString().trim { it <= ' ' }
            val projectLocation =
                projectLocationEdittext.editText?.text.toString().trim { it <= ' ' }
            val projectDescription =
                projectDescriptionEdittext.editText?.text.toString().trim { it <= ' ' }
            when {
                TextUtils.isEmpty(unlocalizedProjectCategory) -> {
                    toastErrorMessage(INVALID_PROJECT_CATEGORY_TEXT_ERROR_MESSAGE)
                }

                TextUtils.isEmpty(projectTitle) -> {
                    toastErrorMessage(INVALID_PROJECT_TITLE_TEXT_ERROR_MESSAGE)
                }

                TextUtils.isEmpty(projectMotivation) -> {
                    toastErrorMessage(INVALID_PROJECT_MOTIVATION_TEXT_ERROR_MESSAGE)
                }

                TextUtils.isEmpty(projectLocation) -> {
                    toastErrorMessage(INVALID_PROJECT_LOCATION_TEXT_ERROR_MESSAGE)
                }

                TextUtils.isEmpty(
                    projectDescription) -> {
                    toastErrorMessage(INVALID_PROJECT_DESCRIPTION_TEXT_ERROR_MESSAGE)
                }

                else -> {
                    val action =
                        ProjectProposalGeneralFragmentDirections.actionProjectProposalGeneralFragmentToProjectProposalAttachmentFragment(
                            projectCategory = unlocalizedProjectCategory,
                            projectTitle = projectTitle,
                            projectMotivation = projectMotivation,
                            projectLocation = projectLocation,
                            projectDescription = projectDescription,
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}