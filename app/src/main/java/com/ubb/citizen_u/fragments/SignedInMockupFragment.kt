package com.ubb.citizen_u.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentSignedInMockupBinding
import com.ubb.citizen_u.model.Citizen
import com.ubb.citizen_u.util.CollectionConstants
import com.ubb.citizen_u.util.DocumentConstants
import com.ubb.citizen_u.util.FirebaseSingleton

class SignedInMockupFragment : Fragment() {

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
        val citizen = firestore.collection(CollectionConstants.USERS)
            .document(currentUser?.uid ?: DocumentConstants.UNDEFINED_DOCUMENT)
            .get().result?.toObject(Citizen::class.javaObjectType)

        binding.welcomeTextview.text =
            getString(R.string.signed_in_mockup_your_account_textview, citizen?.firstName)
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