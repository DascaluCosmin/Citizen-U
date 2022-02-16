package com.ubb.citizen_u.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val listIncidentPhotoUri: MutableList<Uri> = mutableListOf()
    private val _listIncidentPhotoUriLiveData = MutableLiveData<MutableList<Uri>>()
    val listIncidentPhotoUriLiveData: LiveData<MutableList<Uri>> = _listIncidentPhotoUriLiveData

    private val _addReportIncidentState: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val addReportIncidentState: SharedFlow<Response<Boolean>> = _addReportIncidentState

    var incidentAddress: String = ""

    fun addIncidentPhoto(uri: Uri) {
        listIncidentPhotoUri.add(uri)
        _listIncidentPhotoUriLiveData.value = listIncidentPhotoUri
    }

    fun removeLatestPhoto() {
        listIncidentPhotoUri.removeLast()
        _listIncidentPhotoUriLiveData.value = listIncidentPhotoUri
    }

    @ExperimentalCoroutinesApi
    fun reportIncident(description: String, citizenId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.reportIncidentUseCase(
                incident = Incident(
                    description = description,
                    sentDate = Date(),
                    address = incidentAddress,
                ),
                citizenId = citizenId,
                listIncidentPhotoUri = listIncidentPhotoUri,
            ).collect { response ->
                if (response is Response.Success) {
                    listIncidentPhotoUri.clear()
                }
                _addReportIncidentState.tryEmit(response)
                _addReportIncidentState.resetReplayCache()
            }
        }
    }
}