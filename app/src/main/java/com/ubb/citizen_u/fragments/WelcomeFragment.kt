package com.ubb.citizen_u.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentWelcomeBinding
import com.ubb.citizen_u.fragments.dialog.ResetPasswordDialogFragment
import com.ubb.citizen_u.util.AuthenticationConstants
import com.ubb.citizen_u.util.FirebaseSingleton
import com.ubb.citizen_u.util.ValidationConstants

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successfulSignIn() {
        when {
            TextUtils.isEmpty(
                binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    context,
                    ValidationConstants.INVALID_EMAIL_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(
                binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    context,
                    ValidationConstants.INVALID_PASSWORD_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                binding.signInProgressbar.visibility = View.VISIBLE
                val email = binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
                val password =
                    binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }

                val firebaseAuth = FirebaseSingleton.FIREBASE.auth
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser?.isEmailVerified == false) {
                                currentUser.sendEmailVerification()
                                Toast.makeText(
                                    context,
                                    AuthenticationConstants.FAILED_SIGN_IN_UNVERIFIED_EMAIL_MESSAGE,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.signInProgressbar.visibility = View.GONE
                            } else {
                                findNavController().navigate(R.id.action_welcomeFragment_to_signedInMockupFragment)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                AuthenticationConstants.FAILED_SIGN_IN_MESSAGE,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.signInProgressbar.visibility = View.GONE
                        }
                    }
            }
        }
    }

    fun register() {
        findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
    }

    fun resetPassword() {
        ResetPasswordDialogFragment().show(parentFragmentManager, "Reset Password")
    }
}