package nz.co.test.transactions.data

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import nz.co.test.transactions.data.source.local.TransactionDao
import nz.co.test.transactions.data.source.network.NetworkDataSource
import nz.co.test.transactions.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DefaultTransactionRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: TransactionDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : TransactionRepository {

    override fun getTransactionsStream(): Flow<List<Transaction>> =
        localDataSource.observeAll().map { transactions ->
            transactions.toExternal()
        }

    override suspend fun refresh(): Boolean = withContext(dispatcher) {
        runCatching {
            val remoteTransactions = networkDataSource.loadTransactions()
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteTransactions.toLocal())
        }.onFailure { e ->
            Log.e("Repository", "Refresh failed: ${e.message}")
        }.isSuccess
    }

    override fun getTransactionStream(transactionId: String): Flow<Transaction?> =
        localDataSource.observeById(transactionId.toLong()).map { it?.toExternal() }
}