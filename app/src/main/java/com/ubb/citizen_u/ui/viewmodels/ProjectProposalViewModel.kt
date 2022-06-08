package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposalData
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.projectproposals.ProjectProposalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ProjectProposalViewModel @Inject constructor(
    private val projectProposalUseCases: ProjectProposalUseCases,
) : ViewModel() {

    companion object {
        const val TAG = "UBB-ProjectProposalViewModel"
    }

    //region Propose Project
    private val listProposedProjectAttachment: MutableList<Attachment> = mutableListOf()

    private val _proposeProjectState: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val proposeProjectState: SharedFlow<Response<Boolean>> = _proposeProjectState
    //endregion

    //region Comment to Project Proposal
    private val _addCommentToProjectProposalState: MutableSharedFlow<Response<ProjectProposalData>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val addCommentToProjectProposalState: SharedFlow<Response<ProjectProposalData>> =
        _addCommentToProjectProposalState
    //endregion

    //region Proposed Project Getters
    private val _getProposedProjectState: MutableSharedFlow<Response<ProjectProposalData?>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getProposedProjectState: SharedFlow<Response<ProjectProposalData?>> =
        _getProposedProjectState

    private val _getCitizenProposedProjectsState: MutableSharedFlow<Response<List<ProjectProposalData?>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getCitizenProposedProjectsState: SharedFlow<Response<List<ProjectProposalData?>>> =
        _getCitizenProposedProjectsState

    private val _getOthersProposedProjectsState: MutableSharedFlow<Response<List<ProjectProposalData?>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getOthersProposedProjectsState: SharedFlow<Response<List<ProjectProposalData?>>> =
        _getOthersProposedProjectsState
    //endregion

    var currentSelectedProjectProposal: ProjectProposalData? = null
    var currentSelectedProjectProposalPhotoIndex = 0
    var currentSelectedProjectProposalCommentIndex = 0

    fun addAttachment(attachment: Attachment) {
        listProposedProjectAttachment.add(attachment)
    }

    fun addCommentToCurrentProposedProject(comment: Comment) {
        Log.d(TAG,
            "Adding comment ${comment.text} to project proposal ${currentSelectedProjectProposal?.id}...")
        viewModelScope.launch(Dispatchers.IO) {
            currentSelectedProjectProposal?.let { projectProposal ->
                projectProposalUseCases.addCommentToProjectProposal(projectProposal, comment)
                    .collect {
                        if (it is Response.Success) {
                            currentSelectedProjectProposal?.comments?.run {
                                add(comment)
                                currentSelectedProjectProposalCommentIndex = size - 1
                            }
                        }

                        when (it) {
                            Response.Loading -> {
                                _addCommentToProjectProposalState.tryEmit(Response.Loading)
                            }
                            is Response.Error -> {
                                _addCommentToProjectProposalState.tryEmit(Response.Error(it.message))
                            }
                            is Response.Success -> {
                                _addCommentToProjectProposalState.tryEmit(Response.Success(
                                    currentSelectedProjectProposal!!))
                            }
                        }

                        _addCommentToProjectProposalState.resetReplayCache()
                    }
            }
        }
    }

    fun proposeProject(projectProposal: ProjectProposal) {
        Log.d(TAG, "Proposing project $projectProposal...")
        viewModelScope.launch(Dispatchers.IO) {
            projectProposalUseCases.proposeProjectUseCase(
                projectProposal,
                listProposedProjectAttachment
            ).collect {
                _proposeProjectState.tryEmit(it)
            }
        }
    }

    fun getNextProjectProposalPhoto(): Photo? {
        currentSelectedProjectProposal?.photos?.let {
            if (currentSelectedProjectProposalPhotoIndex >= it.size) {
                currentSelectedProjectProposalPhotoIndex = 0
            }
            return it[currentSelectedProjectProposalPhotoIndex++]
        }
        return null
    }

    @Synchronized
    fun getNextProposedProjectComment(): Comment? {
        currentSelectedProjectProposal?.comments?.let {
            val comment = it[currentSelectedProjectProposalCommentIndex++]
            if (currentSelectedProjectProposalCommentIndex >= it.size) {
                currentSelectedProjectProposalCommentIndex = 0
            }
            return comment
        }
        return null
    }

    @Synchronized
    fun getPreviousProposedProjectComment(): Comment? {
        currentSelectedProjectProposal?.comments?.let {
            val comment = it[currentSelectedProjectProposalCommentIndex--]
            if (currentSelectedProjectProposalCommentIndex == -1) {
                currentSelectedProjectProposalCommentIndex = it.size - 1
            }
            return comment
        }
        return null
    }

    fun getProposedProject(citizenId: String, proposedProjectId: String) {
        Log.d(TAG, "Getting the $proposedProjectId proposed project by citizen $citizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            projectProposalUseCases.getProposedProjectUseCase(citizenId, proposedProjectId)
                .collect {
                    if (it is Response.Success) {
                        currentSelectedProjectProposal = it.data
                    }

                    _getProposedProjectState.tryEmit(it)
                }
        }
    }

    fun getCitizenProposedProjects(citizenId: String) {
        Log.d(TAG, "Getting the proposed projects by citizen $citizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            projectProposalUseCases.getCitizenProposedProjectsUseCase(citizenId).collect {
                _getCitizenProposedProjectsState.tryEmit(it)
            }
        }
    }

    fun getOtherCitizensProposedProjects(currentCitizenId: String) {
        Log.d(TAG,
            "Getting the proposed projects by others, the current citizen is $currentCitizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            projectProposalUseCases.getOthersProposedProjectsUseCase(currentCitizenId).collect {
                _getOthersProposedProjectsState.tryEmit(it)
            }
        }
    }
}