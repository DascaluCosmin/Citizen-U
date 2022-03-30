package com.ubb.citizen_u.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.projectproposals.ProjectProposalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectProposalViewModel @Inject constructor(
    private val projectProposalUseCases: ProjectProposalUseCases,
) : ViewModel() {

    companion object {
        const val TAG = "UBB-ProjectProposalViewModel"
    }

    private val listProposedProjectAttachment: MutableList<Attachment> = mutableListOf()

    private val _proposeProjectState: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val proposeProjectState: SharedFlow<Response<Boolean>> = _proposeProjectState

    fun addAttachment(attachment: Attachment) {
        listProposedProjectAttachment.add(attachment)
    }

    fun proposeProject(projectProposal: ProjectProposal) {
        viewModelScope.launch(Dispatchers.IO) {
            projectProposalUseCases.proposeProjectUseCase(projectProposal)
        }
    }
}