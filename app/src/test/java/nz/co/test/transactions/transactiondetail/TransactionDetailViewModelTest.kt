package nz.co.test.transactions.transactiondetail

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.test.transactions.MainCoroutineRule
import nz.co.test.transactions.R
import nz.co.test.transactions.TransactionDestinationsArgs.TRANSACTION_ID_ARG
import nz.co.test.transactions.data.FakeTransactionRepository
import nz.co.test.transactions.data.Transaction
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TransactionDetailViewModel]
 */
@ExperimentalCoroutinesApi
class TransactionDetailViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var transactionDetailViewModel: TransactionDetailViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var transactionsRepository: FakeTransactionRepository
    private val transaction = Transaction(
        id = 1,
        transactionDate = "2022-02-06T12:41:09",
        summary = "Jones, Moen and Kirlin",
        debit = 805.41,
        credit = 0.0
    )

    @Before
    fun setupViewModel() {
        transactionsRepository = FakeTransactionRepository()
        transactionsRepository.addTransactions(transaction)

        transactionDetailViewModel = TransactionDetailViewModel(
            transactionsRepository,
            SavedStateHandle(mapOf(TRANSACTION_ID_ARG to "1"))
        )
    }

    @Test
    fun getActiveTransactionFromRepositoryAndLoadIntoView() = runTest {
        val uiState = transactionDetailViewModel.uiState.first()
        // Then verify that the view was notified
        assertThat(uiState.transaction?.id).isEqualTo(transaction.id)
        assertThat(uiState.transaction?.summary).isEqualTo(transaction.summary)
    }
    
    @Test
    fun transactionDetailViewModel_repositoryError() = runTest {
        // Given a repository that throws errors
        transactionsRepository.setShouldThrowError(true)

        // Then the transaction is null and the snackbar shows a loading error message
        assertThat(transactionDetailViewModel.uiState.value.transaction).isNull()
        assertThat(transactionDetailViewModel.uiState.first().userMessage)
            .isEqualTo(R.string.loading_transaction_error)
    }

    @Test
    fun transactionDetailViewModel_transactionNotFound() = runTest {
        // Given an ID for a non existent transaction
        transactionDetailViewModel = TransactionDetailViewModel(
            transactionsRepository,
            SavedStateHandle(mapOf(TRANSACTION_ID_ARG to "2"))
        )

        // The transaction is null and the snackbar shows a "not found" error message
        assertThat(transactionDetailViewModel.uiState.value.transaction).isNull()
        assertThat(transactionDetailViewModel.uiState.first().userMessage)
            .isEqualTo(R.string.transaction_not_found)
    }

    @Test
    fun loadTransaction_loading() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        Dispatchers.setMain(StandardTestDispatcher())

        var isLoading: Boolean? = true
        val job = launch {
            transactionDetailViewModel.uiState.collect {
                isLoading = it.isLoading
            }
        }

        // Then progress indicator is shown
        assertThat(isLoading).isTrue()

        // Execute pending coroutines actions
        advanceUntilIdle()

        // Then progress indicator is hidden
        assertThat(isLoading).isFalse()
        job.cancel()
    }
}
