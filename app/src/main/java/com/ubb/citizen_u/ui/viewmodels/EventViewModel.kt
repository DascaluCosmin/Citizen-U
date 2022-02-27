package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.events.PeriodicEvent
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.events.EventUseCases
import com.ubb.citizen_u.ui.workers.NotificationWorker
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

    var currentPublicEventDetails: PublicEvent? = null
    // endregion

    // region Public Release Events
    private val _getAllPublicReleaseEventsState =
        MutableSharedFlow<Response<List<PublicReleaseEvent?>>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getAllPublicReleaseEventsState: SharedFlow<Response<List<PublicReleaseEvent?>>>
        get() = _getAllPublicReleaseEventsState

    private val _getPublicReleaseEventDetailsState =
        MutableSharedFlow<Response<PublicReleaseEvent?>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getPublicReleaseEventDetailsState: SharedFlow<Response<PublicReleaseEvent?>>
        get() = _getPublicReleaseEventDetailsState

    private var currentPeriodicEventNotificationState =
        NotificationWorker.PeriodicEventNotificationState.UNKNOWN
    // endregion

    //region Periodic Events
    private val _getAllPeriodicEventsState = MutableSharedFlow<Response<List<PeriodicEvent?>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getAllPeriodicEventsState: SharedFlow<Response<List<PeriodicEvent?>>>
        get() = _getAllPeriodicEventsState
    //endregion

    fun getAllPublicEventsOrderedByDate() {
        Log.d(TAG, "getAllEventsOrderedByDate: Getting all public events ordered by date...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllPublicEventsOrderedByDateUseCase().collect {
                _getAllPublicEventsState.tryEmit(it)
            }
        }
    }

    fun getPublicEventDetails(eventId: String) {
        Log.d(TAG, "getPublicEventDetails: Getting details for public event $eventId...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getPublicEventDetailsUseCase(eventId).collect {
                _getPublicEventDetailsState.tryEmit(it)
                if (it is Response.Success) {
                    currentPublicEventDetails = it.data
                }
            }
        }
    }

    fun getAllPublicReleaseEventsOrderedByDate() {
        Log.d(
            TAG,
            "getAllPublicReleaseEventsOrderedByDate: Getting all public releases ordered by date..."
        )
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllPublicReleaseEventsOrderedByUseCase().collect {
                _getAllPublicReleaseEventsState.tryEmit(it)
            }
        }
    }

    fun getPublicReleaseEventDetails(eventId: String) {
        Log.d(TAG,
            "getPublicReleaseEventDetails: Getting details for public release event $eventId...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getPublicReleaseDetailsUseCase(eventId).collect {
                _getPublicReleaseEventDetailsState.tryEmit(it)
            }
        }
    }

    fun getAllPeriodicEvents() {
        Log.d(TAG, "getPeriodicEvents: Getting all periodic events...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllPeriodicEventsUseCase().collect {
                _getAllPeriodicEventsState.tryEmit(it)
            }
        }
    }

    fun getCurrentPeriodicEventNotificationState(): NotificationWorker.PeriodicEventNotificationState {
        return currentPeriodicEventNotificationState
    }

    fun openCurrentPeriodicReleaseEventState() {
        currentPeriodicEventNotificationState =
            NotificationWorker.PeriodicEventNotificationState.OPENED
    }

    fun consumeCurrentPeriodicReleaseEventState() {
        currentPeriodicEventNotificationState =
            NotificationWorker.PeriodicEventNotificationState.CONSUMED
    }
}