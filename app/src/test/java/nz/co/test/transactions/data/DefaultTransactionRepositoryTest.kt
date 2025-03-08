package nz.co.test.transactions.data

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.data.source.local.FakeTransactionDao
import nz.co.test.transactions.data.source.network.FakeNetworkDataSource
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class DefaultTransactionRepositoryTest {

    private val transaction1 = Transaction(
        id = 1,
        transactionDate = "2022-02-06T12:41:09",
        summary = "Jones, Moen and Kirlin",
        debit = 805.41,
        credit = 0.0
    )
    private val transaction2 = Transaction(
        id = 2,
        transactionDate = "2021-02-15T19:56:33",
        summary = "Effertz, Johnson and Spencer",
        debit = 465.27,
        credit = 0.0
    )
    private val transaction3 = Transaction(
        id = 3,
        transactionDate = "2021-12-06T01:36:13",
        summary = "Russel, Bradtke and VonRueden",
        debit = 0.0,
        credit = 8680.85
    )

    private val networkTransactions = listOf(transaction1, transaction2, transaction3).toNetwork()
    private val localTransactions = listOf(transaction1, transaction2).toLocal()

    // Test dependencies
    private lateinit var networkDataSource: FakeNetworkDataSource
    private lateinit var localDataSource: FakeTransactionDao

    private var testDispatcher = UnconfinedTestDispatcher()

    // Class under test
    private lateinit var transactionRepository: DefaultTransactionRepository

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        networkDataSource = FakeNetworkDataSource(networkTransactions.toMutableList())
        localDataSource = FakeTransactionDao(localTransactions)
        // Get a reference to the class under test
        transactionRepository = DefaultTransactionRepository(
            networkDataSource = networkDataSource,
            localDataSource = localDataSource,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun getTransactionsStream_localDataAvailable_returnsTransactions() = runTest {
        val result = transactionRepository.getTransactionsStream().first()

        assertThat(result).hasSize(2)
        assertThat(result[0].summary).isEqualTo("Jones, Moen and Kirlin")
        assertThat(result[1].summary).isEqualTo("Effertz, Johnson and Spencer")
    }

    @Test
    fun getTransactionsStream_localDataNotAvailable_returnsNoTransactions() = runTest {
        localDataSource.deleteAll()
        val result = transactionRepository.getTransactionsStream().first()

        assertThat(result).isEmpty()
    }

    @Test
    fun getTransactionStream_localDataAvailable_returnsTransaction() = runTest {
        val result = transactionRepository.getTransactionStream("1").first()

        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(1)
        assertThat(result?.summary).isEqualTo("Jones, Moen and Kirlin")
    }

    @Test
    fun getTransactionStream_localDataNotAvailable_returnsNull() = runTest {
        localDataSource.deleteAll()
        val result = transactionRepository.getTransactionStream("1").first()

        assertThat(result).isNull()
    }

    @Test
    fun refresh_remoteDataAvailable_insertsIntoDb() = runTest {
        transactionRepository.refresh()
        val result = transactionRepository.getTransactionsStream().first()

        assertThat(result).hasSize(3)
        assertThat(result[0].summary).isEqualTo("Jones, Moen and Kirlin")
        assertThat(result[1].summary).isEqualTo("Russel, Bradtke and VonRueden")
        assertThat(result[2].summary).isEqualTo("Effertz, Johnson and Spencer")
    }
}
