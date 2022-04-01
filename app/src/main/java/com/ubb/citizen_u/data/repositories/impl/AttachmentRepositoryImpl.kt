package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.AttachmentData
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.repositories.AttachmentRepository
import com.ubb.citizen_u.util.DatabaseConstants
import com.ubb.citizen_u.util.DatabaseConstants.DOCUMENTS_COl
import com.ubb.citizen_u.util.DatabaseConstants.PHOTOS_COL
import com.ubb.citizen_u.util.DatabaseConstants.PROPOSED_PROJECTS_COL
import com.ubb.citizen_u.util.FileExtensions
import com.ubb.citizen_u.util.UNDEFINED
import com.ubb.citizen_u.util.UNKNOWN
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val firebaseStorage: FirebaseStorage,
) : AttachmentRepository {

    companion object {
        private const val TAG = "UBB-AttachmentRepositoryImpl"
    }

    override suspend fun saveAttachment(
        attachment: Attachment,
        citizenId: String,
        projectProposalId: String,
    ) {
        val attachmentCollection = when (attachment) {
            is Photo -> PHOTOS_COL
            is Pdf -> DOCUMENTS_COl
            else -> UNDEFINED
        }

        val attachmentModel = Attachment(
            name = attachment.name,
            description = attachment.description
        )

        val result = usersRef.document(citizenId)
            .collection(DatabaseConstants.PROPOSED_PROJECTS_COL).document(projectProposalId)
            .collection(attachmentCollection)
            .add(attachmentModel).await()
        attachment.id = result.id

        saveAttachmentFile(
            attachment = attachment,
            citizenId = citizenId,
            projectProposalId = projectProposalId
        )
    }

    private suspend fun saveAttachmentFile(
        attachment: Attachment,
        citizenId: String,
        projectProposalId: String,
    ) {
        val fileExtension: String
        val attachmentCollection: String
        when (attachment) {
            is Photo -> {
                fileExtension = FileExtensions.PHOTO_FILE_EXTENSION
                attachmentCollection = PHOTOS_COL
            }
            is Pdf -> {
                fileExtension = FileExtensions.PDF_FILE_EXTENSION
                attachmentCollection = DOCUMENTS_COl
            }
            else -> {
                fileExtension = FileExtensions.PHOTO_FILE_EXTENSION
                attachmentCollection = UNKNOWN
            }
        }

        val path =
            "$PROPOSED_PROJECTS_COL/$citizenId/$projectProposalId/$attachmentCollection/${attachment.id}$fileExtension"
        (attachment as AttachmentData).uri?.let {
            firebaseStorage.getReference(path).putFile(it).await()
        }
    }
}