package nz.co.test.transactions.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions"
)
data class LocalTransaction(
    @PrimaryKey val id: Int,
    val transactionDate: String,
    val summary: String,
    val debit: Double,
    val credit: Double
)
