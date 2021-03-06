package com.ubb.citizen_u.ui.fragments

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
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentLoginBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.fragments.dialog.ResetPasswordDialogFragment
import com.ubb.citizen_u.ui.util.fadeIn
import com.ubb.citizen_u.ui.util.fadeOut
import com.ubb.citizen_u.ui.util.loadLocale
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        const val TAG = "UBB-LoginFragment"
    }

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()
    private val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        loadLocale()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            loginFragment = this@LoginFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Need multiple coroutines, because otherwise the first Flow will suspend the coroutine
                // until it ends. In case of MutableStateFlows, it will complete when it's cancelled
                launch { collectSignInState() }
                launch { collectCurrentUserState() }
                launch { collectSendEmailResetUserPasswordState() }
            }
        }
        authenticationViewModel.getCurrentUser()
    }

    private suspend fun collectSignInState() {
        authenticationViewModel.signInState.collect {
            Log.d(TAG, "collectSignInState: Collecting response $it")
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
                                getString(R.string.FAILED_SIGN_IN_UNVERIFIED_EMAIL_MESSAGE),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            authenticationViewModel.hasJustLoggedOff = false
                            navigateToUserProfile(user.uid)
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
            }
        }
    }

    private suspend fun collectCurrentUserState() {
        authenticationViewModel.currentUserState.collect {
            Log.d(TAG, "collectCurrentUserState: Collecting response $it")
            when (it) {
                is Response.Error -> {
                    Log.d(
                        TAG,
                        "collectCurrentUserState: Error at fetching the current user: ${it.message}"
                    )
                    binding.splashLayout.fadeOut()
                    binding.mainLayout.fadeIn()
                }
                Response.Loading -> {
                    Log.d(TAG, "collectCurrentUserState: Fetching current user")
                }
                is Response.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, "collectCurrentUserState: The ${it.data.id} is connected")
                        if (!authenticationViewModel.hasJustLoggedOff) {
                            navigateToUserProfile(it.data.id)
                        }
                    } else {
                        Log.d(TAG, "collectCurrentUserState: There is no connected user")
                        binding.splashLayout.fadeOut()
                        binding.mainLayout.fadeIn()
                    }
                }
            }
        }
    }

    private suspend fun collectSendEmailResetUserPasswordState() {
        authenticationViewModel.sendEmailResetUserPasswordState.collect {
            Log.d(TAG, "collectSendEmailResetUserPasswordState: Collecting response $it")
            when (it) {
                true -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.SUCCESSFUL_RESET_PASSWORD_EMAIL_SENT),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                false -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.FAILED_RESET_PASSWORD_EMAIL_NOT_SENT),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun navigateToUserProfile(userId: String) {
        val action = LoginFragmentDirections.actionLoginFragmentToMainActivity(
            citizenId = userId,
            periodicEventDetailsId = args.periodicEventDetailsId
        )
        findNavController().navigate(action)
    }

    private fun showFailedSignIn() {
        Toast.makeText(
            context,
            getString(R.string.FAILED_SIGN_IN_MESSAGE),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun signIn() {
        when {
            TextUtils.isEmpty(
                binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
            ) -> {
                Toast.makeText(
                    context,
                    getString(R.string.INVALID_EMAIL_ERROR_MESSAGE),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(
                binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }
            ) -> {
                Toast.makeText(
                    context,
                    getString(R.string.INVALID_PASSWORD_ERROR_MESSAGE),
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

    fun register() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun resetPassword() {
        ResetPasswordDialogFragment().show(parentFragmentManager, "Reset Password")
    }
}