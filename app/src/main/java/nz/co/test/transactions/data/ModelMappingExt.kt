package nz.co.test.transactions.data

import nz.co.test.transactions.data.source.local.LocalTransaction


fun LocalTransaction.toExternal() = Transaction(
    id = id,
    transactionDate = transactionDate,
    summary = summary,
    debit = debit,
    credit = credit
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localToExternal")
fun List<LocalTransaction>.toExternal() = map(LocalTransaction::toExternal)