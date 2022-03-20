package com.ubb.citizen_u.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.requests.Comment
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

    companion object {
        private const val TAG = "UBB-CitizenRequestViewModel"
    }

    private val listIncidentPhotoUri: MutableList<Uri> = mutableListOf()
    private val _listIncidentPhotoUriLiveData = MutableLiveData<MutableList<Uri>>()
    val listIncidentPhotoUriLiveData: LiveData<MutableList<Uri>> = _listIncidentPhotoUriLiveData

    private val _addReportIncidentState: MutableSharedFlow<Response<Boolean>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val addReportIncidentState: SharedFlow<Response<Boolean>> = _addReportIncidentState

    //region Incident Getters
    private val _getReportedIncidentState: MutableSharedFlow<Response<Incident?>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getReportedIncidentState: SharedFlow<Response<Incident?>> = _getReportedIncidentState

    private val _getCitizenReportedIncidentsState: MutableSharedFlow<Response<List<Incident?>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getCitizenReportedIncidentsState: SharedFlow<Response<List<Incident?>>> =
        _getCitizenReportedIncidentsState

    private val _getOthersReportedIncidentsState: MutableSharedFlow<Response<List<Incident?>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getOthersReportedIncidentsState: SharedFlow<Response<List<Incident?>>> =
        _getOthersReportedIncidentsState
    //endregion

    private val _addCommentToIncidentState: MutableSharedFlow<Response<Boolean>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val addCommentToIncidentState: SharedFlow<Response<Boolean>> = _addCommentToIncidentState

    var incidentAddress: String = ""

    var currentSelectedIncident: Incident? = null
    var currentSelectedIncidentPhotoIndex = 0
    var currentSelectedIncidentCommentIndex = 0

    fun addIncidentPhoto(uri: Uri) {
        listIncidentPhotoUri.add(uri)
        _listIncidentPhotoUriLiveData.value = listIncidentPhotoUri
    }

    fun removeLatestPhoto() {
        listIncidentPhotoUri.removeLast()
        _listIncidentPhotoUriLiveData.value = listIncidentPhotoUri
    }

    fun getNextIncidentPhoto(): Photo? {
        currentSelectedIncident?.photos?.let {
            if (currentSelectedIncidentPhotoIndex >= it.size) {
                currentSelectedIncidentPhotoIndex = 0
            }
            return it[currentSelectedIncidentPhotoIndex++]
        }
        return null
    }

    fun getNextIncidentComment(): Comment? {
        currentSelectedIncident?.comments?.let {
            if (currentSelectedIncidentCommentIndex >= it.size) {
                currentSelectedIncidentCommentIndex = 0
            }
            return it[currentSelectedIncidentCommentIndex++]
        }
        return null
    }

    fun getPreviousIncidentComment(): Comment? {
        currentSelectedIncident?.comments?.let {
            if (currentSelectedIncidentCommentIndex == -1) {
                currentSelectedIncidentCommentIndex = it.size - 1
            }
            return it[currentSelectedIncidentCommentIndex--]
        }
        return null
    }

    @ExperimentalCoroutinesApi
    fun reportIncident(description: String, headline: String, citizenId: String) {
        // TODO: Validate Address
        Log.d(TAG, "Adding incident for citizen $citizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.reportIncidentUseCase(
                incident = Incident(
                    description = description,
                    headline = headline,
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

    fun getReportedIncident(citizenId: String, incidentId: String) {
        Log.d(TAG,
            "getReportedIncident: Getting the $incidentId reported incident by $citizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.getReportedIncident(citizenId, incidentId).collect {
                if (it is Response.Success) {
                    currentSelectedIncident = it.data
                }

                _getReportedIncidentState.tryEmit(it)
            }
        }
    }

    fun getCitizenReportedIncidents(citizenId: String) {
        Log.d(TAG,
            "Getting the reported incidents by citizen $citizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.getCitizenReportedIncidents(citizenId).collect {
                _getCitizenReportedIncidentsState.tryEmit(it)
            }
        }
    }

    fun getOthersReportedIncidents(currentCitizenId: String) {
        Log.d(TAG,
            "Getting the reported incidents by others, the current citizen is $currentCitizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.getOthersReportedIncidents(currentCitizenId).collect {
                _getOthersReportedIncidentsState.tryEmit(it)
            }
        }
    }

    fun addCommentToCurrentIncident(comment: Comment) {
        Log.d(TAG,
            "addCommentToCurrentIncident: Adding comment ${comment.text} to incident ${currentSelectedIncident?.id}...")
        viewModelScope.launch(Dispatchers.IO) {
            currentSelectedIncident?.let { incident ->
                citizenRequestUseCase.addCommentToIncident(incident, comment).collect {
                    _addCommentToIncidentState.tryEmit(it)
                }
            }
        }
    }
}