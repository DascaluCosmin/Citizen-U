package com.ubb.citizen_u.domain.usescases.events

import com.ubb.citizen_u.data.model.events.PeriodicEvent
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_TESTING_ERROR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GetAllPeriodicEventsUseCaseTest {

    @Mock
    private lateinit var eventRepository: EventRepository

    @InjectMocks
    private lateinit var testInstance: GetAllPeriodicEventsUseCase

    @Test
    fun shouldGetAllPeriodicEventsIfThereArePeriodicEvents() = runTest {
        val periodicEvent = PeriodicEvent()
        `when`(eventRepository.getAllPeriodicEvents()).thenReturn(flow {
            emit(Response.Success(listOf(periodicEvent)))
        })

        val result = testInstance()

        result.collect {
            assertTrue(it is Response.Success)

            val resultData = (it as Response.Success).data
            assertTrue(resultData.isNotEmpty())
        }
        verify(eventRepository).getAllPeriodicEvents()
    }

    @Test
    fun shouldNotGetAnyPeriodicEventsIfThereIsAnError() = runTest {
        `when`(eventRepository.getAllPeriodicEvents()).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val result = testInstance()

        result.collect {
            assertTrue(it is Response.Error)

            val resultMessage = (it as Response.Error).message
            assertEquals(DEFAULT_TESTING_ERROR, resultMessage)
        }
        verify(eventRepository).getAllPeriodicEvents()
    }
}