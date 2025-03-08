package nz.co.test.transactions.transactiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import nz.co.test.transactions.R
import nz.co.test.transactions.TransactionDestinationsArgs
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.util.Async
import nz.co.test.transactions.util.WhileUiSubscribed
import javax.inject.Inject


data class TransactionDetailUiState(
    val transaction: Transaction? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    repository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transactionId: String =
        savedStateHandle[TransactionDestinationsArgs.TRANSACTION_ID_ARG]!!

    private val _transactionAsync = repository.getTransactionStream(transactionId)
        .map { handleTransaction(it) }
        .onStart { emit(Async.Loading) }
        .catch { emit(Async.Error(R.string.loading_transaction_error)) }

    val uiState: StateFlow<TransactionDetailUiState> = _transactionAsync.map { transactionAsync ->
        when (transactionAsync) {
            Async.Loading -> {
                TransactionDetailUiState(isLoading = true)
            }

            is Async.Error -> {
                TransactionDetailUiState(
                    userMessage = transactionAsync.errorMessage
                )
            }

            is Async.Success -> {
                TransactionDetailUiState(
                    transaction = transactionAsync.data,
                    isLoading = false
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TransactionDetailUiState(isLoading = true)
        )

    private fun handleTransaction(transaction: Transaction?): Async<Transaction?> {
        if (transaction == null) {
            return Async.Error(R.string.transaction_not_found)
        }
        return Async.Success(transaction)
    }
}