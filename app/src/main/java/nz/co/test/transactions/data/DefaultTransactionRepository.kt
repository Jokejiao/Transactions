package nz.co.test.transactions.data


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import nz.co.test.transactions.data.source.local.TransactionDao
import nz.co.test.transactions.data.source.network.TransactionNetworkDataSource
import nz.co.test.transactions.di.DefaultDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTransactionRepository @Inject constructor(
    private val networkDataSource: TransactionNetworkDataSource,
    private val localDataSource: TransactionDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : TransactionRepository {

    override fun getTransactionsStream(): Flow<List<Transaction>> {
        return localDataSource.observeAll().map { tasks ->
            withContext(dispatcher) {
                tasks.toExternal()
            }
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteTransactions = networkDataSource.loadTransactions()
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteTransactions.toLocal())
        }
    }
}