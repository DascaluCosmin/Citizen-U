package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.events.CouncilMeetEvent
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.events.EventUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventUseCases: EventUseCases,
) : ViewModel() {

    companion object {
        private const val TAG = "UBB-EventViewModel"
    }

    // region Public Events
    private val _getAllPublicEventsState = MutableSharedFlow<Response<List<PublicEvent?>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getAllPublicEventsState: SharedFlow<Response<List<PublicEvent?>>>
        get() = _getAllPublicEventsState

    private val _getPublicEventDetailsState = MutableSharedFlow<Response<PublicEvent?>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getPublicEventDetailsState: SharedFlow<Response<PublicEvent?>>
        get() = _getPublicEventDetailsState
    // endregion

    // region Council Meet Events
    private val _getAllCouncilMeetEventsState =
        MutableSharedFlow<Response<List<CouncilMeetEvent?>>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getAllCouncilMeetEventsState: SharedFlow<Response<List<CouncilMeetEvent?>>>
        get() = _getAllCouncilMeetEventsState

    // endregion

    fun getAllPublicEventsOrderedByDate() {
        Log.d(TAG, "getAllEventsOrderedByDate: Getting all public events ordered by date...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllPublicEventsOrderedByDateUseCase().collect {
                _getAllPublicEventsState.tryEmit(it)
            }
        }
    }

    fun getPublicEventDetails(eventId: String) {
        Log.d(TAG, "getPublicEventDetails: Getting details for public event $eventId")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getPublicEventDetailsUseCase(eventId).collect {
                _getPublicEventDetailsState.tryEmit(it)
            }
        }
    }

    fun getAllCouncilMeetEventsOrderedByDate() {
        Log.d(
            TAG,
            "getAllCouncilMeetEventsOrderedByDate: Getting all council events ordered by date..."
        )
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllCouncilMeetEventsOrderedByUseCase().collect {
                _getAllCouncilMeetEventsState.tryEmit(it)
            }
        }
    }
}