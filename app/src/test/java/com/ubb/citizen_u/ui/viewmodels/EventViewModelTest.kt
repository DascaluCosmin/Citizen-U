package com.ubb.citizen_u.ui.viewmodels

import app.cash.turbine.test
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.events.EventUseCases
import com.ubb.citizen_u.domain.usescases.events.GetAllPublicEventsOrderedByDateUseCase
import com.ubb.citizen_u.domain.usescases.events.GetPublicReleaseEventDetailsUseCase
import com.ubb.citizen_u.util.DEFAULT_TESTING_ERROR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class EventViewModelTest {

    companion object {
        private const val PUBLIC_RELEASE_EVENT_ID = "publicReleaseEventId"
        private const val NON_EXISTING_PUBLIC_RELEASE_EVENT_ID = "nonExistingPublicReleaseEventId"
    }

    @Mock
    private lateinit var eventUseCases: EventUseCases

    @InjectMocks
    private lateinit var testInstance: EventViewModel

    private lateinit var getAllPublicEventsOrderedByDateUseCase: GetAllPublicEventsOrderedByDateUseCase
    private lateinit var getPublicReleaseDetailsUseCase: GetPublicReleaseEventDetailsUseCase

    private val firstPublicEvent = PublicEvent().apply { id = "firstPublicEventId" }
    private val secondPublicEvent = PublicEvent().apply { id = "secondPublicEventId" }
    private val publicReleaseEvent = PublicReleaseEvent().apply { id = PUBLIC_RELEASE_EVENT_ID }

    @Before
    fun setUp() {
        getAllPublicEventsOrderedByDateUseCase =
            mock(GetAllPublicEventsOrderedByDateUseCase::class.java)

        getPublicReleaseDetailsUseCase = mock(GetPublicReleaseEventDetailsUseCase::class.java)

        `when`(eventUseCases.getAllPublicEventsOrderedByDateUseCase).thenReturn(
            getAllPublicEventsOrderedByDateUseCase)
        `when`(eventUseCases.getPublicReleaseDetailsUseCase).thenReturn(
            getPublicReleaseDetailsUseCase)
    }

    @Test
    fun shouldGetAllPublicEventsOrderedByDateInCaseOfSuccess() = runTest {
        val job = launch {
            testInstance.getAllPublicEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertTrue(resultData.isNotEmpty())
                assertTrue(resultData.contains(firstPublicEvent))

                cancelAndConsumeRemainingEvents()
            }
        }

        `when`(getAllPublicEventsOrderedByDateUseCase()).thenReturn(flow {
            emit(Response.Success(listOf(firstPublicEvent, secondPublicEvent)))
        })

        var result = testInstance.getAllPublicEventsOrderedByDate()
        job.join()
        job.cancel()

        verify(getAllPublicEventsOrderedByDateUseCase).invoke()
    }

    @Test
    fun shouldNotGetAnyPublicEventOrderedByDateInCaseOfError() = runTest {
        val job = launch {
            testInstance.getAllPublicEventsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)

                cancelAndConsumeRemainingEvents()
            }
        }

        `when`(getAllPublicEventsOrderedByDateUseCase()).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        var result = testInstance.getAllPublicEventsOrderedByDate()
        job.join()
        job.cancel()

        verify(getAllPublicEventsOrderedByDateUseCase).invoke()
    }

    @Test
    fun shouldGetPublicReleaseEventIfItExists() = runTest {
        val job = launch {
            testInstance.getPublicReleaseEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertEquals(publicReleaseEvent, resultData)

                cancelAndConsumeRemainingEvents()
            }
        }

        `when`(getPublicReleaseDetailsUseCase(PUBLIC_RELEASE_EVENT_ID)).thenReturn(flow {
            emit(Response.Success(publicReleaseEvent))
        })

        var result = testInstance.getPublicReleaseEventDetails(PUBLIC_RELEASE_EVENT_ID)
        job.join()
        job.cancel()

        verify(getPublicReleaseDetailsUseCase).invoke(PUBLIC_RELEASE_EVENT_ID)
    }

    @Test
    fun shouldNotGetAnyPublicReleaseEventIfItDoesNotExist() = runTest {
        val job = launch {
            testInstance.getPublicReleaseEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Success)

                val resultData = (emission as Response.Success).data
                assertNull(resultData)

                cancelAndConsumeRemainingEvents()
            }
        }

        `when`(getPublicReleaseDetailsUseCase(NON_EXISTING_PUBLIC_RELEASE_EVENT_ID)).thenReturn(flow {
            emit(Response.Success(null))
        })

        var result = testInstance.getPublicReleaseEventDetails(NON_EXISTING_PUBLIC_RELEASE_EVENT_ID)
        job.join()
        job.cancel()

        verify(getPublicReleaseDetailsUseCase).invoke(NON_EXISTING_PUBLIC_RELEASE_EVENT_ID)
    }

    @Test
    fun shouldNotGetAnyPublicReleaseEventInCaseOfError() = runTest {
        val job = launch {
            testInstance.getPublicReleaseEventDetailsState.test {
                val emission = awaitItem()
                assertTrue(emission is Response.Error)

                val errorMessage = (emission as Response.Error).message
                assertEquals(DEFAULT_TESTING_ERROR, errorMessage)

                cancelAndConsumeRemainingEvents()
            }
        }

        `when`(getPublicReleaseDetailsUseCase(PUBLIC_RELEASE_EVENT_ID)).thenReturn(flow {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val result = testInstance.getPublicReleaseEventDetails(PUBLIC_RELEASE_EVENT_ID)
        job.join()
        job.cancel()

        verify(getPublicReleaseDetailsUseCase).invoke(PUBLIC_RELEASE_EVENT_ID)
    }
}