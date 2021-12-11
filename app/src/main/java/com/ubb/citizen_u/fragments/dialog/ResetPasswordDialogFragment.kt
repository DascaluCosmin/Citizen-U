package com.ubb.citizen_u.fragments.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentDialogResetPasswordBinding
import com.ubb.citizen_u.util.AuthenticationConstants
import com.ubb.citizen_u.util.ValidationConstants

class ResetPasswordDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogResetPasswordBinding

    companion object {
        const val TAG = "ResetPasswordDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            binding = FragmentDialogResetPasswordBinding.inflate(inflater)

            builder.setView(binding.root)
                .setPositiveButton(R.string.reset_password_dialog_positive_button, null)
            builder.create()
        }

        // Need to override the positive button's handler in order to
        // prevent the Dialog from closing, so that the context is maintained
        dialog?.setOnShowListener {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
                setOnPositiveButtonClickListener()
            }
        }

        return dialog ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setOnPositiveButtonClickListener() {
        Log.d(TAG, "Pressed on send email for password reset")
        val email = binding.emailTextfield.editText?.text.toString()
        if (TextUtils.isEmpty(email.trim { emailValue -> emailValue <= ' ' })) {
            Toast.makeText(
                requireContext(),
                ValidationConstants.INVALID_EMAIL_ERROR_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            AuthenticationConstants.SUCCESSFUL_RESET_PASSWORD_EMAIL_SENT,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            AuthenticationConstants.FAILED_RESET_PASSWORD_EMAIL_NOT_SENT,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}