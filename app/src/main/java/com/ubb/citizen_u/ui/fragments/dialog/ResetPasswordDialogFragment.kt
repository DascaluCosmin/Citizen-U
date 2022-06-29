package com.ubb.citizen_u.ui.fragments.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentDialogResetPasswordBinding
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ResetPasswordDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "UBB-ResetPasswordDialogFragment"
    }

    private lateinit var binding: FragmentDialogResetPasswordBinding

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

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
                getString(R.string.INVALID_EMAIL_ERROR_MESSAGE),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            authenticationViewModel.sendEmailResetUserPassword(email)
        }
    }
}