package nz.co.test.transactions.data.source.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class FakeTransactionDao(initialTransactions: List<LocalTransaction>? = emptyList()) :
    TransactionDao {

    private val _transactions =
        MutableStateFlow(
            initialTransactions?.associateBy { it.id }?.toMutableMap() ?: mutableMapOf()
        )

    var transactions: List<LocalTransaction>
        get() = _transactions.value.values.toList()
        set(newTransactions) {
            _transactions.value = newTransactions.associateBy { it.id }.toMutableMap()
        }

    override suspend fun upsertAll(transactions: List<LocalTransaction>) {
        _transactions.value = _transactions.value.toMutableMap().apply {
            putAll(transactions.associateBy { it.id })
        }
    }

    override suspend fun deleteAll() {
        _transactions.value = mutableMapOf()
    }

    override fun observeAll(): Flow<List<LocalTransaction>> {
        return _transactions.map { it.values.sortedByDescending { transaction -> transaction.transactionDate } }
    }

    override fun observeById(transactionId: Long): Flow<LocalTransaction?> {
        return _transactions.map { it[transactionId] }
    }
}
