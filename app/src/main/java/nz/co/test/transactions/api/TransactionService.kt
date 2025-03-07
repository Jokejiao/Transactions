package nz.co.test.transactions.api

import nz.co.test.transactions.data.source.network.NetworkTransaction
import retrofit2.http.GET

interface TransactionService {

    @GET("500f2716604dc1e8e2a3c6d31ad01830/raw/4d73acaa7caa1167676445c922835554c5572e82/test-data.json")
    suspend fun fetchTransactions(): List<NetworkTransaction>
}