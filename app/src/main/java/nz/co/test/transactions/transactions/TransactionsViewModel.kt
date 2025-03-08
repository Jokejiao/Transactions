package nz.co.test.transactions.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.co.test.transactions.R
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.util.Async
import nz.co.test.transactions.util.WhileUiSubscribed
import javax.inject.Inject

data class TransactionsUiState(
    val items: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val noTransactionsUiInfo: NoTransactionsUiInfo = NoTransactionsUiInfo(),
    val userMessage: Int? = null
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _transactionsAsync = repository.getTransactionsStream()
        .map { Async.Success(it) as Async<List<Transaction>> }
        .onStart { emit(Async.Loading) }
        .catch { emit(Async.Error(R.string.loading_transactions_error)) }

    val uiState: StateFlow<TransactionsUiState> = combine(
        _transactionsAsync,
        _isLoading,
        _userMessage
    ) { transactionAsync, isLoading, userMessage ->
        when (transactionAsync) {
            Async.Loading -> {
                TransactionsUiState(isLoading = true)
            }

            is Async.Error -> {
                TransactionsUiState(userMessage = transactionAsync.errorMessage)
            }

            is Async.Success -> {
                TransactionsUiState(
                    items = transactionAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TransactionsUiState(isLoading = true)
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.refresh()
                if (!result) showSnackbarMessage(R.string.refresh_transactions_error)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }
}

data class NoTransactionsUiInfo(
    val noTransactionsLabel: Int = R.string.transactions_are_being_loaded,
    val noTransactionsIconRes: Int = R.drawable.logo_no_fill,
)