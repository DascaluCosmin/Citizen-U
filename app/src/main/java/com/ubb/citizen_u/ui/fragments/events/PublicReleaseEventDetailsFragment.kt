package com.ubb.citizen_u.ui.fragments.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.databinding.FragmentPublicReleaseEventDetailsBinding
import com.ubb.citizen_u.ui.viewmodels.EventViewModel

class PublicReleaseEventDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicReleaseEventDetailsFragment"
    }

    private var _binding: FragmentPublicReleaseEventDetailsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()
    private val args: PublicReleaseEventDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicReleaseEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            publicReleaseEventDetailsFragment = this@PublicReleaseEventDetailsFragment
        }
    }
}