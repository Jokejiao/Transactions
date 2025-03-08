package nz.co.test.transactions.transactions

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.test.transactions.MainCoroutineRule
import nz.co.test.transactions.data.FakeTransactionRepository
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TransactionsViewModel]
 */
@ExperimentalCoroutinesApi
class TransactionsViewModelTest {

    // Subject under test
    private lateinit var transactionsViewModel: TransactionsViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var transactionsRepository: FakeTransactionRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        // We initialise the transactions to 3, with one active and two completed
        transactionsRepository = FakeTransactionRepository()
        val transaction1 = Transaction(
            id = 1,
            transactionDate = "2022-02-06T12:41:09",
            summary = "Jones, Moen and Kirlin",
            debit = 805.41,
            credit = 0.0
        )
        val transaction2 = Transaction(
            id = 2,
            transactionDate = "2021-02-15T19:56:33",
            summary = "Effertz, Johnson and Spencer",
            debit = 465.27,
            credit = 0.0
        )
        val transaction3 = Transaction(
            id = 3,
            transactionDate = "2021-12-06T01:36:13",
            summary = "Russel, Bradtke and VonRueden",
            debit = 0.0,
            credit = 8680.85
        )

        transactionsViewModel = TransactionsViewModel(transactionsRepository)

        transactionsRepository.addTransactions(transaction1, transaction2, transaction3)
    }

    @Test
    fun loadAllTransactionsFromRepository_loadingTogglesAndDataLoaded() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        Dispatchers.setMain(StandardTestDispatcher())

        // Trigger loading of transactions
        transactionsViewModel.refresh()

        // Then progress indicator is shown
        assertThat(transactionsViewModel.uiState.first().isLoading).isTrue()

        // Execute pending coroutines actions
        advanceUntilIdle()

        // Then progress indicator is hidden
        assertThat(transactionsViewModel.uiState.first().isLoading).isFalse()

        // And data correctly loaded
        assertThat(transactionsViewModel.uiState.first().items).hasSize(3)
    }

    @Test
    fun loadTransactions_error() = runTest {
        // Make the repository throw errors
        transactionsRepository.setShouldThrowError(true)

        // Load transactions
        transactionsViewModel.refresh()

        // Then progress indicator is hidden
        assertThat(transactionsViewModel.uiState.first().isLoading).isFalse()

        // And the list of items is empty
        assertThat(transactionsViewModel.uiState.first().items).isEmpty()
        assertThat(transactionsViewModel.uiState.first().userMessage)
            .isEqualTo(R.string.loading_transactions_error)
    }
}
