package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.event.EventUseCases
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

    private val _getAllEventsState = MutableSharedFlow<Response<List<PublicEvent?>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getAllEventsState: SharedFlow<Response<List<PublicEvent?>>> get() = _getAllEventsState

    private val _getEventDetailsState = MutableSharedFlow<Response<PublicEvent?>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getPublicEventDetailsState: SharedFlow<Response<PublicEvent?>> = _getEventDetailsState

    fun getAllEventsOrderedByDate() {
        Log.d(TAG, "getAllEventsOrderedByDate: Getting all public events ordered by date...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllPublicPublicEventsOrderedByDateUseCase().collect {
                _getAllEventsState.tryEmit(it)
            }
        }
    }

    fun getPublicEventDetails(eventId: String) {
        Log.d(TAG, "getPublicEventDetails: Getting details for public event $eventId")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getPublicEventDetailsUseCase(eventId).collect {
                _getEventDetailsState.tryEmit(it)
            }
        }
    }
}