package nz.co.test.transactions.data.source.network


import nz.co.test.transactions.api.TransactionService
import javax.inject.Inject

class TransactionNetworkDataSource @Inject constructor(
    private val networkApi: TransactionService
) : NetworkDataSource {

    override suspend fun loadTransactions(): List<NetworkTransaction> = networkApi.fetchTransactions()
}

