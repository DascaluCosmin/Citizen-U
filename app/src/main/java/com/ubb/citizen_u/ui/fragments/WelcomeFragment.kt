package com.ubb.citizen_u.ui.fragments

import android.os.Bundle
import android.text.TextUtils
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
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentWelcomeBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.fragments.dialog.ResetPasswordDialogFragment
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    companion object {
        const val TAG = "WelcomeFragment"
    }

    private var _binding: FragmentWelcomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding.apply {
            welcomeFragment = this@WelcomeFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Need 2 coroutines, because otherwise the first Flow will suspend the coroutine
                // until it ends. In case of MutableStateFlows, it will complete when it's cancelled
                launch {
                    authenticationViewModel.signInState.collect {
                        when (it) {
                            is Response.Success -> {
                                val user = it.data
                                if (user == null) {
                                    showFailedSignIn()
                                } else {
                                    if (!user.isEmailVerified) {
                                        user.sendEmailVerification()
                                        Toast.makeText(
                                            context,
                                            AuthenticationConstants.FAILED_SIGN_IN_UNVERIFIED_EMAIL_MESSAGE,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        navigateToUserProfile()
                                    }
                                }
                                binding.signInProgressbar.visibility = View.GONE
                            }
                            is Response.Loading -> {
                                binding.signInProgressbar.visibility = View.VISIBLE
                            }
                            is Response.Error -> {
                                binding.signInProgressbar.visibility = View.GONE
                                showFailedSignIn()
                            }
                            is Response.Initial -> {
                                // Used to prevent Hot-Flow first collect for initial value
                            }
                        }
                    }
                }
                launch {
                    authenticationViewModel.currentUserState.collect {
                        if (it != null) {
                            navigateToUserProfile()
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successfulSignIn() {
        when {
            TextUtils.isEmpty(
                binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
            ) -> {
                Toast.makeText(
                    context,
                    ValidationConstants.INVALID_EMAIL_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(
                binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }
            ) -> {
                Toast.makeText(
                    context,
                    ValidationConstants.INVALID_PASSWORD_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val email = binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
                val password =
                    binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }

                authenticationViewModel.signIn(email, password)
            }
        }
    }

    private fun navigateToUserProfile() {
        findNavController().navigate(R.id.action_welcomeFragment_to_signedInMockupFragment)
    }

    private fun showFailedSignIn() {
        Toast.makeText(
            context,
            AuthenticationConstants.FAILED_SIGN_IN_MESSAGE,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun register() {
        findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
    }

    fun resetPassword() {
        ResetPasswordDialogFragment().show(parentFragmentManager, "Reset Password")
    }
}