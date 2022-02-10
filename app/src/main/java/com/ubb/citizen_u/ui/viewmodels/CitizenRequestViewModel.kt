package com.ubb.citizen_u.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.citizens.requests.CitizenRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CitizenRequestViewModel @Inject constructor(
    private val citizenRequestUseCase: CitizenRequestUseCase,
) : ViewModel() {

    private val _addReportIncidentState: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val addReportIncidentState: SharedFlow<Response<Boolean>> = _addReportIncidentState

    private val listIncidentPhotoUri = mutableListOf<Uri>()

    fun saveIncidentPhoto(uri: Uri) {
        listIncidentPhotoUri.add(uri)
    }

    fun getIncidentPhotos() =
        listIncidentPhotoUri

    @ExperimentalCoroutinesApi
    fun reportIncident(description: String, citizenId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.reportIncidentUseCase(
                incident = Incident(
                    description = description,
                    sentDate = Date()
                ),
                citizenId = citizenId,
                listIncidentPhotoUri = listIncidentPhotoUri,
            ).collect {
                if (it is Response.Success) {
                    listIncidentPhotoUri.clear()
                }
                _addReportIncidentState.tryEmit(it)
                _addReportIncidentState.resetReplayCache()
            }
        }
    }
}