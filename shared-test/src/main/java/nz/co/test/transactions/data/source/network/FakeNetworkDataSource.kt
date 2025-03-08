package nz.co.test.transactions.data.source.network

class FakeNetworkDataSource(
    var transactions: MutableList<NetworkTransaction>? = mutableListOf()
) : NetworkDataSource {
    override suspend fun loadTransactions() = transactions ?: throw Exception("Transaction list is null")
}