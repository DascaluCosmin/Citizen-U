package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.events.Event
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

    private val _getAllEventsState = MutableSharedFlow<Response<List<Event?>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getAllEventsState: SharedFlow<Response<List<Event?>>> get() = _getAllEventsState

    fun getAllEventsOrderedByDate() {
        Log.d(TAG, "getAllEventsOrderedByDate: Getting all events ordered by date...")
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCases.getAllEventsOrderedByDateUseCase().collect {
                _getAllEventsState.tryEmit(it)
            }
        }
    }
}