package com.ubb.citizen_u.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentSignedInMockupBinding
import com.ubb.citizen_u.model.Citizen
import com.ubb.citizen_u.util.FirebaseSingleton
import com.ubb.citizen_u.util.UNDEFINED_DOC
import com.ubb.citizen_u.util.USERS_COL

class SignedInMockupFragment : Fragment() {

    companion object {
        const val TAG = "SignedInMockupFragment"
    }

    private val firestore = FirebaseSingleton.FIREBASE.firestore
    private val firebaseAuth = FirebaseSingleton.FIREBASE.auth

    private var _binding: FragmentSignedInMockupBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignedInMockupBinding.inflate(inflater, container, false)

        binding.apply {
            signedInMockupFragment = this@SignedInMockupFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = firebaseAuth.currentUser
        var citizen: Citizen?
        firestore.collection(USERS_COL)
            .document(currentUser?.uid ?: UNDEFINED_DOC)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    citizen = task.result?.toObject(Citizen::class.java)
                    if (citizen != null) {
                        binding.welcomeTextview.text =
                            getString(
                                R.string.signed_in_mockup_your_account_textview_params,
                                citizen?.firstName
                            )
                    }
                } else {
                    Log.e(TAG, "Error at getting the user ${currentUser?.uid}: ${task.exception}")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * TODO: Improve the signOut. Scenarios:
     * - The apps closes unexpectedly.
     *      - If you call signOut on the onStop callback, watch for Configuration Changes, e.g. Device Rotation
     * - The User uses the Home button. Question: what happens then?
     * - The User presses the Back button.
     */
    fun signOut() {
        FirebaseSingleton.FIREBASE.auth.signOut()
        findNavController().navigate(R.id.action_signedInMockupFragment_to_welcomeFragment)
    }
}