package nz.co.test.transactions.data


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import nz.co.test.transactions.data.source.local.TransactionDao
import nz.co.test.transactions.data.source.network.TransactionNetworkDataSource
import nz.co.test.transactions.util.Result
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTransactionRepository @Inject constructor(
    private val networkDataSource: TransactionNetworkDataSource,
    private val localDataSource: TransactionDao,
) : TransactionRepository {

    override fun getTransactions(): Flow<Result<List<Transaction>>> = flow {
        emit(Result.Loading)
        emitAll(localDataSource.observeTransactions().map { Result.Success(it.toExternal()) })

        try {
            val transactions = networkDataSource.loadTransactions()
            localDataSource.upsertTransactions(transactions.toLocal())
        } catch (e: IOException) {
            emit(Result.Error(IOException("Network error. Check your connection.", e)))
        } catch (e: HttpException) {
            emit(Result.Error(Exception("Server error. Try again later.", e)))
        } catch (e: Exception) {
            emit(Result.Error(Exception("Something went wrong.", e)))
        }
    }
}