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
import com.ubb.citizen_u.databinding.FragmentEventsListBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.adapters.EventsAdapter
import com.ubb.citizen_u.ui.fragments.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class EventsListFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-EventsListFragment"
    }

    private var _binding: FragmentEventsListBinding? = null
    private val binding get() = _binding!!

    private val adapter = EventsAdapter()
    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            eventsListFragment = this@EventsListFragment
            eventsRecyclerview.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetAllEventsState() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.getAllEvents()
    }

    private suspend fun collectGetAllEventsState() {
        eventViewModel.getAllEventsState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.d(TAG, "collectGetAllEventsState: Collecting response $it")
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.d(
                        TAG,
                        "collectGetAllEventsState: Collecting response $it. An error has occurred ${it.message}"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    Log.d(
                        TAG,
                        "collectGetAllEventsState: Successfully collected ${it.data.size} events "
                    )
                    adapter.submitList(it.data)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}