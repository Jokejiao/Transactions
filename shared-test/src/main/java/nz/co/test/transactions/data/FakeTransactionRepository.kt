package nz.co.test.transactions.data

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Implementation of a transactions repository with static access to the data for easy testing.
 */
class FakeTransactionRepository : TransactionRepository {

    private var shouldThrowError = false

    private val _savedTransactions = MutableStateFlow(LinkedHashMap<Long, Transaction>())
    val savedTransactions: StateFlow<LinkedHashMap<Long, Transaction>> = _savedTransactions.asStateFlow()

    private val observableTransactions: Flow<List<Transaction>> = savedTransactions.map {
        if (shouldThrowError) {
            throw Exception("Test exception")
        } else {
            it.values.toList()
        }
    }

    override suspend fun refresh(): Boolean {
        // Transactions already refreshed
        return true
    }
    
    override fun getTransactionsStream(): Flow<List<Transaction>> = observableTransactions

    override fun getTransactionStream(transactionId: String): Flow<Transaction?> {
        return observableTransactions.map { transactions ->
            transactions.firstOrNull { it.id == transactionId.toLong() }
        }
    }

    fun setShouldThrowError(value: Boolean) {
        shouldThrowError = value
    }

    @VisibleForTesting
    fun addTransactions(vararg transactions: Transaction) {
        _savedTransactions.update { oldTransactions ->
            val newTransactions = LinkedHashMap<Long, Transaction>(oldTransactions)
            for (transaction in transactions) {
                newTransactions[transaction.id] = transaction
            }
            newTransactions
        }
    }
}
