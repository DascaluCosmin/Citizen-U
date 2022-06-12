package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.citizens.requests.CitizenRequestUseCase
import com.ubb.citizen_u.ui.model.PhotoWithSource
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

@ExperimentalCoroutinesApi
@HiltViewModel
class CitizenRequestViewModel @Inject constructor(
    private val citizenRequestUseCase: CitizenRequestUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "UBB-CitizenRequestViewModel"
    }

    var listIncidentCategories: MutableList<String> = mutableListOf()

    private val addedIncidentPhotos: MutableList<Photo> = mutableListOf()
    private val listIncidentPhotosWithSource: MutableList<PhotoWithSource> = mutableListOf()

    private val _listIncidentPhotoWithSourceLiveData =
        MutableLiveData<MutableList<PhotoWithSource>>()
    val listIncidentPhotoWithSourceLiveData: LiveData<MutableList<PhotoWithSource>> =
        _listIncidentPhotoWithSourceLiveData


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

    private val _getAllReportedIncidentsState: MutableSharedFlow<Response<List<Incident?>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getAllReportedIncidentsState: SharedFlow<Response<List<Incident?>>> =
        _getAllReportedIncidentsState
    //endregion

    //region Comments
    private val _addCommentToIncidentState: MutableSharedFlow<Response<Incident>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val addCommentToIncidentState: SharedFlow<Response<Incident>> = _addCommentToIncidentState
    //endregion

    //region Incident Categories
    private val _incidentCategoriesState: MutableSharedFlow<Response<List<String>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val incidentCategoriesState: SharedFlow<Response<List<String>>> = _incidentCategoriesState
    //endregion

    var incidentAddress: String = ""
    var incidentLongitude: Double = 0.0
    var incidentLatitude: Double = 0.0

    var currentSelectedIncident: Incident? = null
    var currentSelectedIncidentPhotoIndex = 0
    var currentSelectedIncidentCommentIndex = 0

    fun addIncidentPhoto(photoWithSource: PhotoWithSource) {
        listIncidentPhotosWithSource.add(photoWithSource)
        _listIncidentPhotoWithSourceLiveData.postValue(listIncidentPhotosWithSource)

        addedIncidentPhotos.add(photoWithSource.photo!!)
    }

    fun removeLatestPhoto() {
        listIncidentPhotosWithSource.removeLast()
        addedIncidentPhotos.removeLast()
        _listIncidentPhotoWithSourceLiveData.value = listIncidentPhotosWithSource
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

    @Synchronized
    fun getNextIncidentComment(): Comment? {
        currentSelectedIncident?.comments?.let {
            val comment = it[currentSelectedIncidentCommentIndex++]
            if (currentSelectedIncidentCommentIndex >= it.size) {
                currentSelectedIncidentCommentIndex = 0
            }
            return comment
        }
        return null
    }

    @Synchronized
    fun getPreviousIncidentComment(): Comment? {
        currentSelectedIncident?.comments?.let {
            val comment = it[currentSelectedIncidentCommentIndex--]
            if (currentSelectedIncidentCommentIndex == -1) {
                currentSelectedIncidentCommentIndex = it.size - 1
            }
            return comment
        }
        return null
    }

    @ExperimentalCoroutinesApi
    fun reportIncident(description: String, headline: String, citizenId: String, citizen: Citizen) {
        // TODO: Validate Address
        Log.d(TAG, "Adding incident for citizen $citizenId...")
        viewModelScope.launch(Dispatchers.IO) {
            val listIncidentPhoto = listIncidentPhotosWithSource.map { it.photo }.toList()

            citizenRequestUseCase.reportIncidentUseCase(
                incident = Incident(
                    citizen = citizen,
                    description = description,
                    headline = headline,
                    sentDate = Date(),
                    address = incidentAddress,
                    longitude = incidentLongitude,
                    latitude = incidentLatitude,
                    photos = listIncidentPhoto.toMutableList()
                ),
                listIncidentPhotos = listIncidentPhoto.filterNotNull(),
            ).collect { response ->
                if (response is Response.Success) {
                    listIncidentPhotosWithSource.clear()
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

    fun getAllReportedIncidents(incidentCategory: String? = null) {
        Log.d(TAG,
            "getAllReportedIncidents: Getting all reported incidents for category $incidentCategory...")
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.getAllReportedIncidents(incidentCategory).collect {
                _getAllReportedIncidentsState.tryEmit(it)

                _getAllReportedIncidentsState.resetReplayCache()
            }
        }
    }

    fun getAllIncidentCategories() {
        Log.d(TAG, "getAllIncidentCategories: Getting all incident categories...")
        viewModelScope.launch(Dispatchers.IO) {
            citizenRequestUseCase.getAllIncidentCategories().collect {
                _incidentCategoriesState.tryEmit(it)
                if (it is Response.Success) {
                    listIncidentCategories = it.data.toMutableList()
                }
            }
        }
    }

    fun addCommentToCurrentIncident(comment: Comment) {
        Log.d(TAG,
            "addCommentToCurrentIncident: Adding comment ${comment.text} to incident ${currentSelectedIncident?.id}...")
        viewModelScope.launch(Dispatchers.IO) {
            currentSelectedIncident?.let { incident ->
                citizenRequestUseCase.addCommentToIncident(incident, comment).collect {
                    if (it is Response.Success) {
                        currentSelectedIncident?.comments?.run {
                            add(comment)
                            currentSelectedIncidentCommentIndex = size - 1
                        }
                    }

                    when (it) { // Mapping from Response<Boolean> to Response<Incident>
                        Response.Loading -> {
                            _addCommentToIncidentState.tryEmit(Response.Loading)
                        }
                        is Response.Error -> {
                            _addCommentToIncidentState.tryEmit(Response.Error(it.message))
                        }
                        is Response.Success -> {
                            _addCommentToIncidentState.tryEmit(Response.Success(
                                currentSelectedIncident!!))
                        }
                    }
                    _addCommentToIncidentState.resetReplayCache()
                }
            }
        }
    }
}