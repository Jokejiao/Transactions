package nz.co.test.transactions.data.source.network

interface NetworkDataSource {

    suspend fun loadTransactions(): List<NetworkTransaction>
}