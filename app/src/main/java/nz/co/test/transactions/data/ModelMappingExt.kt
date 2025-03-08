package nz.co.test.transactions.data

import nz.co.test.transactions.data.source.local.LocalTransaction
import nz.co.test.transactions.data.source.network.NetworkTransaction


// External to local
fun Transaction.toLocal() = LocalTransaction(
    id = id,
    transactionDate = transactionDate,
    summary = summary,
    debit = debit,
    credit = credit
)

fun List<Transaction>.toLocal() = map(Transaction::toLocal)

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

// Local to Network
fun LocalTransaction.toNetwork() = NetworkTransaction(
    id = id,
    transactionDate = transactionDate,
    summary = summary,
    debit = debit,
    credit = credit
)

fun List<LocalTransaction>.toNetwork() = map(LocalTransaction::toNetwork)

// External to Network
fun Transaction.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Transaction>.toNetwork() = map(Transaction::toNetwork)

// Network to External
fun NetworkTransaction.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<NetworkTransaction>.toExternal() = map(NetworkTransaction::toExternal)