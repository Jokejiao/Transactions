package nz.co.test.transactions.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Transaction(
    val id: Int,
    val transactionDate: String,
    val summary: String,
    val debit: Double,
    val credit: Double
) {
    val formattedDate : String
        get() {
            return try {
                val parsedDate = LocalDateTime.parse(transactionDate, DateTimeFormatter.ISO_DATE_TIME)
                parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                transactionDate
            }
        }
}
