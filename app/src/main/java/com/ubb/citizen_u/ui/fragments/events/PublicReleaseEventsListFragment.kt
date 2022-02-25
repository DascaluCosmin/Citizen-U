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
import com.ubb.citizen_u.databinding.FragmentPublicReleaseEventsListBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.adapters.PublicReleaseEventAdapter
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PublicReleaseEventsListFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicReleaseEventsListFragment"
    }

    private var _binding: FragmentPublicReleaseEventsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PublicReleaseEventAdapter

    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicReleaseEventsListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PublicReleaseEventAdapter {
            Log.d(TAG, "onViewCreated: Clicked on public release event ${it.id}")
        }

        binding.apply {
            eventsRecyclerview.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    collectGetAllPublicReleaseEventsState()
                }
            }
        }
    }

    private suspend fun collectGetAllPublicReleaseEventsState() {
        eventViewModel.getAllPublicReleaseEventsState.collect {
            when (it) {
                is Response.Error -> {
                    Log.d(
                        TAG,
                        "collectGetAllPublicReleaseEventsState: Collecting response $it. An error has occurred ${it.message}"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                Response.Loading -> {
                    Log.d(TAG, "collectGetAllPublicReleaseEventsState: Collecting response $it")
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    Log.d(
                        TAG,
                        "collectGetAllPublicReleaseEventsState: Successfully collected ${it.data.size} public release events"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.getAllPublicReleaseEventsOrderedByDate()
    }
}