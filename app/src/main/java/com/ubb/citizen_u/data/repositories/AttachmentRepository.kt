package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.Attachment

interface AttachmentRepository {

    suspend fun saveAttachment(
        attachment: Attachment,
        citizenId: String,
        projectProposalId: String,
    )
}