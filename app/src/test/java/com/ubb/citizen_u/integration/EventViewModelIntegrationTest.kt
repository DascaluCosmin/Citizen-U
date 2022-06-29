package com.ubb.citizen_u.integration

import app.cash.turbine.test
import com.ubb.citizen_u.data.model.events.PeriodicEvent
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.events.*
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.util.DEFAULT_TESTING_ERROR
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EventViewModelIntegrationTest {

    companion object {
        private const val EVENT_ID = "eventId"
        private const val NON_EXISTENT_EVENT_ID = "nonExistentEventId"
    }

    @Mock
    private lateinit var eventRepository: EventRepository

    @InjectMocks
    private lateinit var getAllPeriodicEventsUseCase: GetAllPeriodicEventsUseCase

    @InjectMocks
    private lateinit var getAllPublicEventsUseCase: GetAllPublicEventsUseCase

    @InjectMocks
    private lateinit var getAllPublicEventsOrderedByDateUseCase: GetAllPublicEventsOrderedByDateUseCase

    @InjectMocks
    private lateinit var getPublicEventDetailsUseCase: GetPublicEventDetailsUseCase

    @InjectMocks
    private lateinit var getAllPublicReleaseEvents: GetAllPublicReleaseEvents

    @InjectMocks
    private lateinit var getAllPublicReleaseEventsOrderedByUseCase: GetAllPublicReleaseEventsOrderedByDateUseCase

    @InjectMocks
    private lateinit var getPublicReleaseDetailsUseCase: GetPublicReleaseEventDetailsUseCase

    private lateinit var eventUseCases: EventUseCases

    private lateinit var testInstance: EventViewModel

    @Before
    fun setUp() {
        eventUseCases = EventUseCases(
            getAllPublicEventsUseCase,
            getAllPublicEventsOrderedByDateUseCase,
            getPublicEventDetailsUseCase,
            getAllPublicReleaseEvents,
            getAllPublicReleaseEventsOrderedByUseCase,
            getPublicReleaseDetailsUseCase,
            getAllPeriodicEventsUseCase
        )

        testInstance = EventViewModel(eventUseCases)
    }

    @Test
    fun shouldGetAllPeriodicEventsIfThereArePeriodicEvents() = runTest {
        val periodicEvent = PeriodicEvent().apply { id = EVENT_ID }
        `when`(eventRepository.getAllPeriodicEvents()).thenReturn(flow {
            emit(Response.Success(listOf(periodicEvent)))
        })

        val job = launch {
            testInstance.getAllPeriodicEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertTrue(resultData.isNotEmpty())
                assertTrue(resultData.contains(periodicEvent))
            }
        }

        val result = testInstance.getAllPeriodicEvents()
        job.join()
        job.cancel()

        verify(eventRepository).getAllPeriodicEvents()
    }

    @Test
    fun shouldNotGetAnyPeriodicEventsIfThereIsAnError() = runTest {
        `when`(eventRepository.getAllPeriodicEvents()).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val job = launch {
            testInstance.getAllPeriodicEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)
            }
        }

        val result = testInstance.getAllPeriodicEvents()
        job.join()
        job.cancel()

        verify(eventRepository).getAllPeriodicEvents()
    }

    @Test
    fun shouldGetAllPublicEventsOrderedByDateIfThereArePublicEvents() = runTest {
        val firstPublicEvent = PublicEvent().apply {
            id = "${EVENT_ID}1"
            startDate = Date()
        }
        val secondPublicEvent = PublicEvent().apply {
            id = "${EVENT_ID}2"
            startDate = Date()
        }
        `when`(eventRepository.getAllEventsOrderedByDate()).thenReturn(flow {
            emit(Response.Success(listOf(firstPublicEvent, secondPublicEvent)))
        })

        val job = launch {
            testInstance.getAllPublicEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertTrue(resultData.isNotEmpty())
                assertTrue(resultData.contains(firstPublicEvent))
                assertTrue(resultData.contains(secondPublicEvent))
                assertEquals(firstPublicEvent, resultData[0])
                assertEquals(secondPublicEvent, resultData[1])
            }
        }

        val result = testInstance.getAllPublicEventsOrderedByDate()
        job.join()
        job.cancel()

        verify(eventRepository).getAllEventsOrderedByDate()
    }

    @Test
    fun shouldNotGetAnyPublicEventsIfThereIsAnError() = runTest {
        `when`(eventRepository.getAllEventsOrderedByDate()).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val job = launch {
            testInstance.getAllPublicEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)
            }
        }

        val result = testInstance.getAllPublicEventsOrderedByDate()
        job.join()
        job.cancel()

        verify(eventRepository).getAllEventsOrderedByDate()
    }

    @Test
    fun shouldGetPublicEventDetailsIfItExists() = runTest {
        val firstPublicEvent = PublicEvent().apply { id = EVENT_ID }
        `when`(eventRepository.getPublicEvent(EVENT_ID)).thenReturn(flow {
            emit(Response.Success(firstPublicEvent))
        })

        val job = launch {
            testInstance.getPublicEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertEquals(firstPublicEvent, resultData)
            }
        }

        val result = testInstance.getPublicEventDetails(EVENT_ID)
        job.join()
        job.cancel()

        verify(eventRepository).getPublicEvent(EVENT_ID)
    }

    @Test
    fun shouldNotGetPublicEventDetailsIfItDoesNotExist() = runTest {
        `when`(eventRepository.getPublicEvent(NON_EXISTENT_EVENT_ID)).thenReturn(flow {
            emit(Response.Success(null))
        })

        val job = launch {
            testInstance.getPublicEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertNull(resultData)
            }
        }

        val result = testInstance.getPublicEventDetails(NON_EXISTENT_EVENT_ID)
        job.join()
        job.cancel()

        verify(eventRepository).getPublicEvent(NON_EXISTENT_EVENT_ID)
    }

    @Test
    fun shouldNotGetPublicEventDetailsIfThereIsAnError() = runTest {
        `when`(eventRepository.getPublicEvent(EVENT_ID)).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val job = launch {
            testInstance.getPublicEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)
            }
        }

        val result = testInstance.getPublicEventDetails(EVENT_ID)
        job.join()
        job.cancel()

        verify(eventRepository).getPublicEvent(EVENT_ID)
    }

    @Test
    fun shouldGetAllPublicReleaseEventsOrderedByDateIfThereArePublicReleaseEvents() = runTest {
        val firstPublicReleaseEvent = PublicReleaseEvent().apply {
            id = "${EVENT_ID}1"
            publicationDate = Date()
        }
        val secondPublicReleaseEvent = PublicReleaseEvent().apply {
            id = "${EVENT_ID}2"
            publicationDate = Date()
        }
        `when`(eventRepository.getAllPublicReleaseEventsOrderedByDate()).thenReturn(flow {
            emit(Response.Success(listOf(firstPublicReleaseEvent, secondPublicReleaseEvent)))
        })

        val job = launch {
            testInstance.getAllPublicReleaseEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertTrue(resultData.isNotEmpty())
                assertTrue(resultData.contains(firstPublicReleaseEvent))
                assertTrue(resultData.contains(secondPublicReleaseEvent))
                assertEquals(firstPublicReleaseEvent, resultData[0])
                assertEquals(secondPublicReleaseEvent, resultData[1])
            }
        }

        val result = testInstance.getAllPublicReleaseEventsOrderedByDate()
        job.join()
        job.cancel()

        verify(eventRepository).getAllPublicReleaseEventsOrderedByDate()
    }

    @Test
    fun shouldNotGetAnyPublicReleaseEventsIfThereIsAnError() = runTest {
        `when`(eventRepository.getAllPublicReleaseEventsOrderedByDate()).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val job = launch {
            testInstance.getAllPublicReleaseEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)
            }
        }

        val result = testInstance.getAllPublicReleaseEventsOrderedByDate()
        job.join()
        job.cancel()

        verify(eventRepository).getAllPublicReleaseEventsOrderedByDate()
    }

    @Test
    fun shouldGetPublicReleaseEventDetailsIfItExists() = runTest {
        val firstPublicReleaseEvent = PublicReleaseEvent().apply { id = EVENT_ID }
        `when`(eventRepository.getPublicReleaseEvent(EVENT_ID)).thenReturn(flow {
            emit(Response.Success(firstPublicReleaseEvent))
        })

        val job = launch {
            testInstance.getPublicReleaseEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertEquals(firstPublicReleaseEvent, resultData)
            }
        }

        val result = testInstance.getPublicReleaseEventDetails(EVENT_ID)
        job.join()
        job.cancel()

        verify(eventRepository).getPublicReleaseEvent(EVENT_ID)
    }

    @Test
    fun shouldNotGetPublicReleaseEventDetailsIfItDoesNotExist() = runTest {
        `when`(eventRepository.getPublicReleaseEvent(NON_EXISTENT_EVENT_ID)).thenReturn(flow {
            emit(Response.Success(null))
        })

        val job = launch {
            testInstance.getPublicReleaseEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertNull(resultData)
            }
        }

        val result = testInstance.getPublicReleaseEventDetails(NON_EXISTENT_EVENT_ID)
        job.join()
        job.cancel()

        verify(eventRepository).getPublicReleaseEvent(NON_EXISTENT_EVENT_ID)
    }

    @Test
    fun shouldNotGetPublicEventReleaseDetailsIfThereIsAnError() = runTest {
        `when`(eventRepository.getPublicReleaseEvent(EVENT_ID)).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val job = launch {
            testInstance.getPublicReleaseEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)
            }
        }

        val result = testInstance.getPublicReleaseEventDetails(EVENT_ID)
        job.join()
        job.cancel()

        verify(eventRepository).getPublicReleaseEvent(EVENT_ID)
    }
}