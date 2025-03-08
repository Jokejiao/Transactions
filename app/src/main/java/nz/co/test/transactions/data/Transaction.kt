package nz.co.test.transactions.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Transaction(
    val id: Long,
    val transactionDate: String,
    val summary: String,
    val debit: Double,
    val credit: Double
) {
    private val gst: Double
        get() = (debit + credit) * 0.15

    val formattedGst: String
        get() = "$${"%,.2f".format(gst)}"

    val formattedDate : String
        get() {
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val dateTime = LocalDateTime.parse(transactionDate, formatter)
                dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
            } catch (e: Exception) {
                transactionDate
            }
        }

    val formattedDebit: String
        get() = if (debit > 0) "-$${"%,.2f".format(debit)}" else ""

    val formattedCredit: String
        get() = if (credit > 0) "+$${"%,.2f".format(credit)}" else ""

    val amountColor: Color
        get() = when {
            debit > 0 -> Color.Red
            credit > 0 -> Color.Green
            else -> Color.Gray
        }
}
