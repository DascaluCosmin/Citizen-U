package com.ubb.citizen_u.data.model.citizens.proposals

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.data.model.citizens.Comment
import java.util.*

open class ProjectProposal(
    @DocumentId var id: String = "",
    var proposedBy: Citizen? = null,
    var proposedOn: Date? = null,
    var title: String? = null,
    var description: String? = null,
    var motivation: String? = null,
    var category: String? = null,
    var location: String? = null,
    var numberOfVotes: Int? = 0,
) {

    override fun toString(): String {
        return """ID = $id, proposed by = ${proposedBy?.getFullName()} on $proposedOn
            title = $title, motivation = $motivation, location = $location, 
            $category = $category, number of votes = $numberOfVotes"""
    }

    fun mapToModelClass(): ProjectProposal {
        return ProjectProposal(
            proposedBy = this.proposedBy,
            proposedOn = this.proposedOn,
            title = this.title,
            description = this.description,
            motivation = this.motivation,
            category = this.category,
            location = this.location,
            numberOfVotes = this.numberOfVotes
        )
    }
}

data class ProjectProposalData(
    var comments: MutableList<Comment?> = mutableListOf(),
    var attachments: MutableList<Attachment?> = mutableListOf(),
) : ProjectProposal()