package nz.co.test.transactions.data

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getTransactionsStream(): Flow<List<Transaction>>

    suspend fun refreshTransactions()
}