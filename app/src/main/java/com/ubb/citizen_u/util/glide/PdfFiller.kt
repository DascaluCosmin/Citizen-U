package com.ubb.citizen_u.util.glide

import android.view.View
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.storage.StorageReference
import com.ubb.citizen_u.ui.util.toastErrorMessage
import java.io.File

object PdfFiller {

    fun fill(
        fragment: Fragment,
        progressBar: ProgressBar,
        pdfView: PDFView,
        pdfStorageReference: StorageReference?,
    ) {
        if (pdfStorageReference == null) {
            fragment.toastErrorMessage()
            return
        }
        progressBar.visibility = View.VISIBLE

        val localFile = File.createTempFile("documents", "pdf")
        pdfStorageReference.getFile(localFile).addOnSuccessListener {
            pdfView.fromUri(localFile.toUri()).load()
            progressBar.visibility = View.GONE
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            fragment.toastErrorMessage()
        }
    }
}