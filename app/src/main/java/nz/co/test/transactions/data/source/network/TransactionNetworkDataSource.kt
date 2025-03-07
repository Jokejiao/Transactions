package nz.co.test.transactions.data.source.network


import nz.co.test.transactions.api.TransactionService
import javax.inject.Inject

/**
 * The Remote datasource to call the API to fetch the transactions.
 * Any exceptions will be thrown to and handled by the repository.
 */
class TransactionNetworkDataSource @Inject constructor(
    private val networkApi: TransactionService
) : NetworkDataSource {

    override suspend fun loadTransactions(): List<NetworkTransaction> = networkApi.fetchTransactions()
}