package nz.co.test.transactions.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalTransaction::class], version = 1, exportSchema = false)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}