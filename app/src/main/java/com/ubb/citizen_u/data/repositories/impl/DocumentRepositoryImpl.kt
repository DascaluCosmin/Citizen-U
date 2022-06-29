package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.repositories.DocumentRepository
import kotlinx.coroutines.tasks.await

class DocumentRepositoryImpl(
    private val firebaseStorage: FirebaseStorage,
) : DocumentRepository {

    companion object {
        const val FIREBASE_STORAGE_PROJECT_PROPOSALS = "proposed_projects"
        const val FIREBASE_STORAGE_PROJECT_PROPOSAL_DOCUMENT = "documents"
    }

    override suspend fun getAllProposedProjectDocuments(
        citizenId: String,
        proposedProjectId: String,
    ): MutableList<Pdf?> {
        val pathToProjectProposalDocumentFolder =
            "$FIREBASE_STORAGE_PROJECT_PROPOSALS/$citizenId/$proposedProjectId/$FIREBASE_STORAGE_PROJECT_PROPOSAL_DOCUMENT"
        val result =
            firebaseStorage.reference.child(pathToProjectProposalDocumentFolder).listAll().await()
        return result.items.map {
            Pdf().apply {
                storageReference = it
            }
        }.toMutableList()
    }
}