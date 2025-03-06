package nz.co.test.transactions.data.source.network

data class NetworkTransaction(
    val id: Int,
    val transactionDate: String,
    val summary: String,
    val debit: Double,
    val credit: Double
)
