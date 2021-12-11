package com.ubb.citizen_u.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentRegisterBinding
import com.ubb.citizen_u.util.AuthenticationConstants
import com.ubb.citizen_u.util.ValidationConstants

/**
 * lateinit works with only non-null var, lateinit works only with non-primitives
 * var needs to be initialized, regardless of nullable or non-nullable
 */
class RegisterFragment : Fragment() {

    companion object {
        private const val TAG = "RegisterFragment"
    }

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            registerFragment = this@RegisterFragment
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun createAccount() {
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
                Log.d(TAG, "Registering user")
                binding.registerProgressbar.visibility = View.VISIBLE
                val email =
                    binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
                val password =
                    binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }

                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.sendEmailVerification()
                            Toast.makeText(
                                context,
                                AuthenticationConstants.SUCCESSFUL_REGISTER_MESSAGE,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            findNavController().navigate(R.id.action_registerFragment_to_welcomeFragment)
                        } else {
                            binding.registerProgressbar.visibility = View.GONE

                            // TODO: Show to the User the Fail Registration cause
                            Toast.makeText(
                                context,
                                AuthenticationConstants.FAILED_REGISTER_MESSAGE,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
            }
        }
    }
}