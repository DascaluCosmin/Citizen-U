package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposalData
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.projectproposals.ProjectProposalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun addAttachment(attachment: Attachment) {
        listProposedProjectAttachment.add(attachment)
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