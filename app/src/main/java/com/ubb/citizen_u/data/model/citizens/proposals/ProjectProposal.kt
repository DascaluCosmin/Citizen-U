package com.ubb.citizen_u.data.model.citizens.proposals

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.model.Photo
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
            category = $category, number of votes = $numberOfVotes"""
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectProposal

        if (id != other.id) return false
        if (proposedBy != other.proposedBy) return false
        if (proposedOn != other.proposedOn) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (motivation != other.motivation) return false
        if (category != other.category) return false
        if (location != other.location) return false
        if (numberOfVotes != other.numberOfVotes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (proposedBy?.hashCode() ?: 0)
        result = 31 * result + (proposedOn?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (motivation?.hashCode() ?: 0)
        result = 31 * result + (category?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (numberOfVotes ?: 0)
        return result
    }


}

data class ProjectProposalData(
    var comments: MutableList<Comment?> = mutableListOf(),
    var photos: MutableList<Photo?> = mutableListOf(),
    var documents: MutableList<Pdf?> = mutableListOf(),
) : ProjectProposal()

class ProjectProposalBasicDetails(
    @DocumentId var id: String = "",
    var proposedBy: Citizen? = null,
    var proposedOn: Date? = null,
    var title: String? = null,
    var category: String? = null,
    var location: String? = null,
    var thumbnailPhoto: Photo? = null,
    var numberOfVotes: Int? = 0,
) {
    override fun toString(): String {
        return """ID = $id, proposed by = ${proposedBy?.getFullName()} on $proposedOn
           title = $title, location = $location, 
           category =  $category, number of votes = $numberOfVotes"""
    }
}