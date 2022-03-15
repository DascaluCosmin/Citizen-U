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
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.databinding.FragmentPublicEventsListBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.adapters.PublicEventsAdapter
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class PublicEventsListFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicEventsListFragment"
    }

    private var _binding: FragmentPublicEventsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PublicEventsAdapter

    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicEventsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PublicEventsAdapter {
            val action =
                PublicEventsListFragmentDirections.actionEventsListFragmentToEventDetailsFragment(
                    eventId = it.id
                )
            findNavController().navigate(action)
        }

        binding.apply {
            eventsListFragment = this@PublicEventsListFragment
            eventsRecyclerview.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetAllPublicEventsState() }
                launch { collectGetAllPeriodicEventsState() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.getAllPublicEventsOrderedByDate()
        eventViewModel.getAllPeriodicEvents()
    }

    private suspend fun collectGetAllPublicEventsState() {
        eventViewModel.getAllPublicEventsState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.d(TAG, "collectGetAllPublicEventsState: Collecting response $it")
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(
                        TAG,
                        "collectGetAllPublicEventsState: Collecting response $it. An error has occurred ${it.message}"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    Log.d(
                        TAG,
                        "collectGetAllPublicEventsState: Successfully collected ${it.data.size} public events "
                    )
                    adapter.submitList(it.data)
                }
            }
        }
    }

    private suspend fun collectGetAllPeriodicEventsState() {
        eventViewModel.getAllPeriodicEventsState.collect {
            when (it) {
                Response.Loading -> {
                    Log.d(TAG, "collectGetAllPeriodicEventsState: Collecting response $it")
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    Log.d(TAG, "collectGetAllPeriodicEventsState: Successfully collected ${it.data.size} periodic events")
                    binding.mainProgressbar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}