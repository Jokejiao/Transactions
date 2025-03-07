package nz.co.test.transactions.data.source.network

import kotlinx.coroutines.delay
import javax.inject.Inject

class TransactionNetworkDataSource @Inject constructor() : NetworkDataSource {

    private val transactions = listOf(
        NetworkTransaction(
            id = 1,
            transactionDate = "2022-01-01",
            summary = "Summary 1",
            debit = 100.0,
            credit = 0.0
        ),
        NetworkTransaction(
            id = 2,
            transactionDate = "2022-01-02",
            summary = "Summary 2",
            debit = 0.0,
            credit = 200.0
        )
    )

    override suspend fun loadTransactions(): List<NetworkTransaction> {
        delay(1000)
        return transactions
    }

}