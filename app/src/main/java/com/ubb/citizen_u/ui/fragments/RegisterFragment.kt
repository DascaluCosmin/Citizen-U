package com.ubb.citizen_u.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * lateinit works with only non-null var, lateinit works only with non-primitives
 * var needs to be initialized, regardless of nullable or non-nullable
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-RegisterFragment"
        private const val PASSWORD_REGEX = ".{3,}"
    }

    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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

    fun goNext() {
        val email = binding.emailTextfield.editText?.text.toString().trim { it <= ' ' }
        val password = binding.passwordTextfield.editText?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(
                email) -> {
                Toast.makeText(
                    context,
                    getString(R.string.INVALID_EMAIL_ERROR_MESSAGE),
                    Toast.LENGTH_SHORT
                ).show()
            }

            TextUtils.isEmpty(password) -> {
                Toast.makeText(
                    context,
                    getString(R.string.INVALID_PASSWORD_ERROR_MESSAGE),
                    Toast.LENGTH_SHORT
                ).show()
            }

            !password.matches(PASSWORD_REGEX.toRegex()) -> {
                Toast.makeText(
                    context,
                    getString(R.string.INVALID_PASSWORD_ERROR_MESSAGE),
                    Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                Log.d(TAG, "Registering User in Multistep Register Credentials")
                val action =
                    RegisterFragmentDirections.actionRegisterFragmentToIdentityInformationFragment(
                        email = email,
                        password = password
                    )
                findNavController().navigate(action)
            }
        }
    }
}