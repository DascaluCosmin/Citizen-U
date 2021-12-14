package com.ubb.citizen_u.fragments.multistep.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentIdentityInformationBinding

class IdentityInformationFragment : Fragment() {

    private var _binding: FragmentIdentityInformationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentIdentityInformationBinding.inflate(inflater, container, false)
        binding.apply {
            identityInformationFragment = this@IdentityInformationFragment
        }

        return binding.root
    }

    fun goNext() {
        findNavController().navigate(R.id.action_identityInformationFragment_to_welcomeFragment)
    }
}