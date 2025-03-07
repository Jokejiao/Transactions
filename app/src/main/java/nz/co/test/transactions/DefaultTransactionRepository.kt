package nz.co.test.transactions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.data.source.local.TransactionDao
import nz.co.test.transactions.data.source.network.TransactionNetworkDataSource
import nz.co.test.transactions.data.toExternal
import nz.co.test.transactions.di.ApplicationScope
import nz.co.test.transactions.di.DefaultDispatcher
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DefaultTransactionRepository @Inject constructor(
    private val networkDataSource: TransactionNetworkDataSource,
    private val localDataSource: TransactionDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : TransactionRepository {

    override fun getTransactionsStream(): Flow<List<Transaction>> {
        return localDataSource.observeTransactions().map { transactions ->
            withContext(dispatcher) {
                transactions.toExternal()
            }
        }
    }

    override suspend fun refreshTransactions() {

    }
}