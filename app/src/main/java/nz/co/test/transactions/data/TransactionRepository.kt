package nz.co.test.transactions.data

import nz.co.test.transactions.util.Result
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getTransactions(): Flow<Result<List<Transaction>>>
}