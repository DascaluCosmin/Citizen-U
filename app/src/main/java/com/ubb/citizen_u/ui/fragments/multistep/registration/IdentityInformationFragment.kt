package com.ubb.citizen_u.ui.fragments.multistep.registration

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.databinding.FragmentIdentityInformationBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class IdentityInformationFragment : Fragment() {

    companion object {
        const val TAG = "UBB-IdentityInformationFragment"
    }

    private var _binding: FragmentIdentityInformationBinding? = null

    private val binding get() = _binding!!
    private val args: IdentityInformationFragmentArgs by navArgs()

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdentityInformationBinding.inflate(inflater, container, false)
        binding.apply {
            identityInformationFragment = this@IdentityInformationFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authenticationViewModel.registerUserState.collect {
                    Log.d(TAG, "onCreateView: Collecting response $it")
                    when (it) {
                        Response.Loading -> {
                            binding.multistepRegisterProgressbar.visibility = View.VISIBLE
                        }
                        is Response.Error -> {
                            binding.multistepRegisterProgressbar.visibility = View.GONE
                            Toast.makeText(
                                context,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Response.Success -> {
                            Toast.makeText(
                                context,
                                AuthenticationConstants.SUCCESSFUL_REGISTER_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_identityInformationFragment_to_welcomeFragment)
                        }
                    }
                }
            }
        }
        return binding.root
    }

    fun goNext() {
        val firstName = binding.firstnameTextfield.editText?.text.toString().trim { it <= ' ' }
        val lastName = binding.lastnameTextfield.editText?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(firstName) -> {
                Toast.makeText(
                    context,
                    ValidationConstants.INVALID_FIRST_NAME_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(lastName) -> {
                Toast.makeText(
                    context,
                    ValidationConstants.INVALID_LAST_NAME_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val email = args.email
                val password = args.password
                val citizen = Citizen(firstName, lastName)
                authenticationViewModel.registerUser(email, password, citizen)
            }
        }
    }
}