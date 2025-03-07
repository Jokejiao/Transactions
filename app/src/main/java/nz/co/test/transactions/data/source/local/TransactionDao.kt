package nz.co.test.transactions.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TransactionDao {
    /**
     * Get list of transactions.
     *
     * @return all transactions
     */
    @Query("SELECT * FROM transactions")
    suspend fun getTransactions(): List<LocalTransaction>

    /**
     * Insert or update transactions in the database. If a transaction already exists, replace it.
     *
     * @param transactions - the transactions to be inserted or updated
     *
     */
    @Upsert
    suspend fun upsertTransactions(transactions: List<LocalTransaction>)
}