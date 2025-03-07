package nz.co.test.transactions.data

import nz.co.test.transactions.data.source.local.LocalTransaction
import nz.co.test.transactions.data.source.network.NetworkTransaction


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

// Network to Local
fun NetworkTransaction.toLocal() = LocalTransaction(
    id = id,
    transactionDate = transactionDate,
    summary = summary,
    debit = debit,
    credit = credit
)

@JvmName("networkToLocal")
fun List<NetworkTransaction>.toLocal() = map(NetworkTransaction::toLocal)