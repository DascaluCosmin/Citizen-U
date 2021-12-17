package com.ubb.citizen_u.fragments.multistep.registration

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentIdentityInformationBinding
import com.ubb.citizen_u.fragments.RegisterFragment
import com.ubb.citizen_u.model.Citizen
import com.ubb.citizen_u.util.*

class IdentityInformationFragment : Fragment() {

    companion object {
        const val TAG = "IdentityInformationFragment"
    }

    private var _binding: FragmentIdentityInformationBinding? = null

    private val binding get() = _binding!!
    private val args: IdentityInformationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentIdentityInformationBinding.inflate(inflater, container, false)
        binding.apply {
            identityInformationFragment = this@IdentityInformationFragment
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
                binding.multistepRegisterProgressbar.visibility = View.VISIBLE

                val email = args.email
                val password = args.password
                val citizen = Citizen(firstName, lastName)

                val auth = FirebaseSingleton.FIREBASE.auth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            currentUser?.sendEmailVerification()

                            FirebaseSingleton.FIREBASE.firestore.collection(USERS_COL)
                                .document(currentUser?.uid ?: UNDEFINED_DOC)
                                .set(citizen)

                            Toast.makeText(
                                context,
                                AuthenticationConstants.SUCCESSFUL_REGISTER_MESSAGE,
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d(TAG, "Registering User in Multistep Register Identity")
                            findNavController().navigate(R.id.action_identityInformationFragment_to_welcomeFragment)
                        } else {
                            binding.multistepRegisterProgressbar.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "${AuthenticationConstants.FAILED_REGISTER_MESSAGE} ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}