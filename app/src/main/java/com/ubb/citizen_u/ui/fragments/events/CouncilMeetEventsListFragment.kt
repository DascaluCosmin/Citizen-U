package com.ubb.citizen_u.ui.fragments.events

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
import com.ubb.citizen_u.databinding.FragmentCouncilMeetEventsListBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.adapters.CouncilMeetEventsAdapter
import com.ubb.citizen_u.ui.fragments.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CouncilMeetEventsListFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-CouncilMeetEventsListFragment"
    }

    private var _binding: FragmentCouncilMeetEventsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CouncilMeetEventsAdapter

    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCouncilMeetEventsListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CouncilMeetEventsAdapter {
            Log.d(TAG, "onViewCreated: Clicked on council meet ${it.id}")
        }

        binding.apply {
            eventsRecyclerview.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    collectGetAllCouncilMeetEventsState()
                }
            }
        }
    }

    private suspend fun collectGetAllCouncilMeetEventsState() {
        eventViewModel.getAllCouncilMeetEventsState.collect {
            when (it) {
                is Response.Error -> {
                    Log.d(
                        TAG,
                        "collectGetAllCouncilMeetEventsState: Collecting response $it. An error has occurred ${it.message}"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                Response.Loading -> {
                    Log.d(TAG, "collectGetAllCouncilMeetEventsState: Collecting response $it")
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    Log.d(
                        TAG,
                        "collectGetAllCouncilMeetEventsState: Successfully collected ${it.data.size} council meet events"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.getAllCouncilMeetEventsOrderedByDate()
    }
}