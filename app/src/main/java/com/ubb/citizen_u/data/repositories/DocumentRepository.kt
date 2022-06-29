package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.Pdf

interface DocumentRepository {

    suspend fun getAllProposedProjectDocuments(
        citizenId: String,
        proposedProjectId: String,
    ): MutableList<Pdf?>
}