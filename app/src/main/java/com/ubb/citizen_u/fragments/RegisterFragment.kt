package com.ubb.citizen_u.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentRegisterBinding

/**
 * lateinit works with only non-null var, lateinit works only with non-primitives
 * var needs to be initialized, regardless of nullable or non-nullable
 */
class RegisterFragment : Fragment() {

    companion object {
        private const val INVALID_USERNAME_ERROR_MESSAGE = "Please enter a valid username!"
        private const val INVALID_PASSWORD_ERROR_MESSAGE = "Please enter a valid password!"
        private const val SUCCESSFUL_REGISTER_MESSAGE = "You have been successfully registered!"
        private const val FAILED_REGISTER_MESSAGE = "The registration has failed! Please try again!"
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
                Toast.makeText(context, INVALID_USERNAME_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
            }

            TextUtils.isEmpty(
                binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(context, INVALID_PASSWORD_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
            }

            else -> {
                val email =
                    binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
                val password =
                    binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, SUCCESSFUL_REGISTER_MESSAGE, Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.action_registerFragment_to_welcomeFragment)
                        } else {
                            Toast.makeText(context, FAILED_REGISTER_MESSAGE, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }
}