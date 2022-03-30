package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.repositories.AttachmentRepository
import com.ubb.citizen_u.util.DatabaseConstants
import com.ubb.citizen_u.util.DatabaseConstants.DOCUMENTS_COl
import com.ubb.citizen_u.util.DatabaseConstants.PHOTOS_COL
import com.ubb.citizen_u.util.UNDEFINED
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val firebaseStorage: FirebaseStorage,
) : AttachmentRepository {

    override suspend fun saveAttachment(
        attachment: Attachment,
        citizenId: String,
        projectProposalId: String,
    ): Boolean {
        val attachmentCollection = when (attachment) {
            is Photo -> PHOTOS_COL
            is Pdf -> DOCUMENTS_COl
            else -> UNDEFINED
        }

        val result = usersRef.document(citizenId)
            .collection(DatabaseConstants.PROPOSED_PROJECTS_COL).document(projectProposalId)
            .collection(attachmentCollection)
            .add(attachment).await()
        return true
    }
}