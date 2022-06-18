package com.ubb.citizen_u.data.repositories.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.ubb.citizen_u.MainCoroutineRule
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.repositories.impl.DocumentRepositoryImpl.Companion.FIREBASE_STORAGE_PROJECT_PROPOSALS
import com.ubb.citizen_u.data.repositories.impl.DocumentRepositoryImpl.Companion.FIREBASE_STORAGE_PROJECT_PROPOSAL_DOCUMENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DocumentRepositoryImplTest {

    companion object {
        private const val CITIZEN_ID = "citizenId"
        private const val PROPOSED_PROJECT_ID = "proposedProjectId"
        private const val PATH_TO_PROJECT_PROPOSAL_DOCUMENT_FOLDER =
            "${FIREBASE_STORAGE_PROJECT_PROPOSALS}/$CITIZEN_ID/$PROPOSED_PROJECT_ID/${FIREBASE_STORAGE_PROJECT_PROPOSAL_DOCUMENT}"
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var firebaseStorage: FirebaseStorage

    @InjectMocks
    private lateinit var testInstance: DocumentRepositoryImpl

    private lateinit var storageReference: StorageReference
    private lateinit var childStorageReference: StorageReference

    @Mock
    private lateinit var documentsListTask: Task<ListResult>
    private lateinit var documentListResult: ListResult

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        storageReference = mock(StorageReference::class.java)
        childStorageReference = mock(StorageReference::class.java)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun shouldGetAllProposedProjectDocuments(): Unit = runBlocking {
        val firstPdf = Pdf().apply { name = "firstPdf" }
        val secondPdf = Pdf().apply { name = "secondPdf" }
        val listDocuments = listOf(firstPdf, secondPdf)

        `when`(firebaseStorage.reference).thenReturn(storageReference)
        `when`(storageReference.child(PATH_TO_PROJECT_PROPOSAL_DOCUMENT_FOLDER))
            .thenReturn(childStorageReference)
        `when`(childStorageReference.listAll()).thenReturn(documentsListTask)
    }
}