package com.ubb.citizen_u.ui.fragments.publicspending

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentPublicSpendingListBinding
import com.ubb.citizen_u.ui.adapters.PublicSpendingAdapter
import com.ubb.citizen_u.ui.util.getCurrentLanguage
import com.ubb.citizen_u.ui.viewmodels.PublicSpendingViewModel
import java.util.*

class PublicSpendingListFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicSpendingListFragment"
        private const val COMPLETION_YEAR_LOWER_BOUND = 2010
        private const val COMPLETION_YEAR_UPPER_BOUND = 2050
    }

    private var _binding: FragmentPublicSpendingListBinding? = null
    private val binding: FragmentPublicSpendingListBinding get() = _binding!!

    private lateinit var category: String
    private lateinit var status: String
    private lateinit var completionYear: String
    private lateinit var adapter: PublicSpendingAdapter

    private val publicSpendingViewModel: PublicSpendingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicSpendingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PublicSpendingAdapter()

        val statuses = resources.getStringArray(R.array.statuses)
        val statusesArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.statuses_dropdown,
            statuses
        )

        val categories = resources.getStringArray(R.array.public_spending_categories)
        val categoriesArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.project_proposal_categories_dropdown,
            categories
        )

        var index = 0
        val completionYears =
            arrayOfNulls<String>(COMPLETION_YEAR_UPPER_BOUND - COMPLETION_YEAR_LOWER_BOUND + 2)
        completionYears[index++] = getString(R.string.public_spending_all_years_all_hint)
        for (year in COMPLETION_YEAR_LOWER_BOUND..COMPLETION_YEAR_UPPER_BOUND) {
            completionYears[index++] = year.toString()
        }

        val completionYearsArrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.completion_year_dropdown,
            completionYears
        )

        binding.apply {
            publicSpendingListFragment = this@PublicSpendingListFragment
            publicSpendingRecyclerview.adapter = adapter

            publicSpendingStatusAutocompleteView.setAdapter(statusesArrayAdapter)
            (publicSpendingStatusDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                status = statuses[position]
                loadPublicSpendingList()
            }

            publicSpendingCompletionYearAutocompleteView.setAdapter(completionYearsArrayAdapter)
            (publicSpendingCompletionYearDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                completionYear = completionYears[position]!!
                loadPublicSpendingList()
            }

            publicSpendingCategoryAutocompleteView.setAdapter(categoriesArrayAdapter)
            (publicSpendingCategoryDropdown.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                category = categories[position]
                loadPublicSpendingList()
            }
        }

        completionYear = getString(R.string.public_spending_all_years_all_hint)
        category = getString(R.string.public_spending_all_categories_all_hint)
        status = getString(R.string.public_spending_all_statuses_all_hint)
    }

    override fun onStart() {
        super.onStart()
        loadPublicSpendingList()
    }

    private fun loadPublicSpendingList() {
        val language = requireContext().getCurrentLanguage()
        val filteredPublicSpendingList = publicSpendingViewModel.listPublicSpending
            .asSequence()
            .filterNotNull()
            .filter {
                if (category == getString(R.string.public_spending_all_categories_all_hint)) {
                    return@filter true
                }
                category == it.category[language]
            }
            .filter {
                if (status == getString(R.string.public_spending_all_statuses_all_hint)) {
                    return@filter true
                }
                status == it.status[language]
            }
            .filter {
                if (it.endDate == null) {
                    return@filter false
                }
                if (completionYear == getString(R.string.public_spending_all_years_all_hint)) {
                    return@filter true
                }
                val calendar = Calendar.getInstance()
                calendar.time = it.endDate!!
                completionYear == calendar.get(Calendar.YEAR).toString()
            }.toList()
        Log.d(TAG, "Number of filtered public spending is ${filteredPublicSpendingList.size}")
        adapter.submitList(filteredPublicSpendingList)
    }
}