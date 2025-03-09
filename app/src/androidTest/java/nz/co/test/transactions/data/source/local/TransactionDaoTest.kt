package nz.co.test.transactions.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TransactionDaoTest {

    // using an in-memory database because the information stored here disappears when the
    // process is killed
    private lateinit var database: TransactionDatabase

    private val transaction1 = LocalTransaction(
        id = 1,
        transactionDate = "2022-02-06T12:41:09",
        summary = "Jones, Moen and Kirlin",
        debit = 805.41,
        credit = 0.0
    )
    private val transaction2 = LocalTransaction(
        id = 2,
        transactionDate = "2021-02-15T19:56:33",
        summary = "Effertz, Johnson and Spencer",
        debit = 465.27,
        credit = 0.0
    )
    private val transaction3 = LocalTransaction(
        id = 3,
        transactionDate = "2021-12-06T01:36:13",
        summary = "Russel, Bradtke and VonRueden",
        debit = 0.0,
        credit = 8680.85
    )

    // Ensure that we use a new database for each test.
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TransactionDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertSingleTransaction() = runTest {
        val transactionDao = database.transactionDao()

        transactionDao.upsertAll(listOf(transaction1))
        val transactions = transactionDao.getAll()

        assertEquals(1, transactions.size)
        assertEquals(transaction1, transactions[0])
    }

    @Test
    fun upsertUpdatesExistingTransaction() = runTest {
        val transactionDao = database.transactionDao()

        transactionDao.upsertAll(listOf(transaction1))
        assertEquals(1, transactionDao.getAll().size)

        val updatedTransaction1 = transaction1.copy(debit = 999.99)
        transactionDao.upsertAll(listOf(updatedTransaction1))

        val transactions = transactionDao.getAll()
        assertEquals(1, transactions.size)
        assertEquals(999.99, transactions[0].debit)
    }

    @Test
    fun upsertMultipleTransactions() = runTest {
        val transactionDao = database.transactionDao()

        transactionDao.upsertAll(listOf(transaction1, transaction2, transaction3))
        val transactions = transactionDao.getAll()

        assertEquals(3, transactions.size)
        assertTrue(transactions.contains(transaction1))
        assertTrue(transactions.contains(transaction2))
        assertTrue(transactions.contains(transaction3))
    }

    @Test
    fun insertTransactionsThenDeleteAll() = runTest {
        val transactionDao = database.transactionDao()

        transactionDao.upsertAll(listOf(transaction1, transaction2, transaction3))
        assertEquals(transactionDao.getAll().size, 3)

        transactionDao.deleteAll()
        assertEquals(transactionDao.getAll().size, 0)
    }

    @Test
    fun deleteAllWhenDatabaseIsEmpty() = runTest {
        val transactionDao = database.transactionDao()

        assertEquals(transactionDao.getAll().size, 0)

        transactionDao.deleteAll()

        assertEquals(transactionDao.getAll().size, 0)
    }

    @Test
    fun deleteAllThenInsertNewTransactions() = runTest {
        val transactionDao = database.transactionDao()

        transactionDao.upsertAll(listOf(transaction1, transaction2))
        assertEquals(transactionDao.getAll().size, 2)

        transactionDao.deleteAll()
        assertEquals(transactionDao.getAll().size, 0)

        transactionDao.upsertAll(listOf(transaction3))
        assertEquals(transactionDao.getAll().size, 1)
    }

}
