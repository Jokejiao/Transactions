package nz.co.test.transactions.data

import kotlinx.coroutines.flow.Flow


interface TransactionRepository {

    fun getTransactionsStream(): Flow<List<Transaction>>

    suspend fun refresh(): Boolean

    fun getTransactionStream(transactionId: String): Flow<Transaction?>
}