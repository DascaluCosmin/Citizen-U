package com.ubb.citizen_u.ui.fragments.publicspending

import android.graphics.Color
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
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ubb.citizen_u.databinding.FragmentPublicSpendingBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.PublicSpendingViewModel
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PublicSpendingFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicSpendingFragment"
    }

    private lateinit var pieChart: PieChart

    private val publicSpendingViewModel: PublicSpendingViewModel by activityViewModels()

    private var _binding: FragmentPublicSpendingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicSpendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            publicSpendingFragment = this@PublicSpendingFragment
            pieChart = pieChartPublicSpending.apply {
                setPieChart(this)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetAllPublicSpendingState() }
            }
        }
    }

    private suspend fun collectGetAllPublicSpendingState() {
        publicSpendingViewModel.getAllPublicSpendingState.collect {
            Log.d(TAG, "collectGetAllPublicSpendingState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    Log.e(TAG, "An error has occurred: ${it.message}")
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    Log.d(TAG, "Collected ${it.data.size} public spending items")
                    groupPublicSpendingByCategory()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        publicSpendingViewModel.getAllPublicSpending()
    }

    private fun setPieChart(pieChart: PieChart) {
        pieChart.apply {
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            setCenterTextSize(24f)
            centerText = "Public Spending by Category"
            description.isEnabled = false

            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.setDrawInside(false)

            val colors = ArrayList<Int>()
            for (color in ColorTemplate.MATERIAL_COLORS) {
                colors.add(color)
            }

            for (color in ColorTemplate.VORDIPLOM_COLORS) {
                colors.add(color)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun groupPublicSpendingByCategory() {
        val groupByCategory = publicSpendingViewModel.listPublicSpending.filterNotNull()
            .groupingBy {
                it.category[DEFAULT_LANGUAGE]
            }.aggregate { _, accumulator: Double?, element, _ ->
                (element.value ?: 0.0) + (accumulator ?: 0.0)
            }
        populatePieChart(groupByCategory)
    }

    private fun populatePieChart(groupedPublicSpending: Map<String?, Double>) {
        val entries = ArrayList<PieEntry>()
        groupedPublicSpending.forEach {
            entries.add(PieEntry(it.value.toFloat(), it.key))
        }

        val colors = ArrayList<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataset = PieDataSet(entries, "")
        dataset.colors = colors

        val data = PieData(dataset)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.invalidate()
        pieChart.setOnHoverListener { view, motionEvent ->
            toastMessage("Hovering")
            true
        }

        pieChart.animateY(1000, Easing.EaseInOutQuad)
    }

    fun viewPublicSpendingPieChartByCategory() {

    }

    fun viewPublicSpendingPieChartBySourceOfFunding() {

    }

    fun viewPublicSpendingList() {
        val action =
            PublicSpendingFragmentDirections.actionPublicSpendingFragmentToPublicSpendingListFragment()
        findNavController().navigate(action)
    }
}