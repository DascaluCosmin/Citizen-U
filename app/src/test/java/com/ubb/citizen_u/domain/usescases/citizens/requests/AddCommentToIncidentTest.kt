package com.ubb.citizen_u.domain.usescases.citizens.requests

import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_TESTING_ERROR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AddCommentToIncidentTest {

    @Mock
    private lateinit var citizenRequestRepository: CitizenRequestRepository

    @InjectMocks
    private lateinit var testInstance: AddCommentToIncident

    private lateinit var incident: Incident
    private lateinit var comment: Comment

    @Before
    fun setUp() {
        incident = Incident()
        comment = Comment()
    }

    @Test
    fun shouldAddCommentToIncident() = runTest {
        `when`(citizenRequestRepository.addCommentToIncident(incident, comment)).thenReturn(
            flow {
                emit(Response.Success(true))
            })

        val result = testInstance(incident, comment)

        result.collect {
            assertTrue(it is Response.Success)
        }
        verify(citizenRequestRepository).addCommentToIncident(incident, comment)
    }

    @Test
    fun shouldNotAddCommentToIncidentIfTheresAnError() = runTest {
        `when`(citizenRequestRepository.addCommentToIncident(incident, comment)).thenReturn(flow
        {
            emit(Response.Error(DEFAULT_TESTING_ERROR))
        })

        val result = testInstance(incident, comment)

        result.collect {
            assertTrue(it is Response.Error)

            val resultMessage = (it as Response.Error).message
            assertEquals(DEFAULT_TESTING_ERROR, resultMessage)
        }
        verify(citizenRequestRepository).addCommentToIncident(incident, comment)
    }
}