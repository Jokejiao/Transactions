package nz.co.test.transactions.transactiondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nz.co.test.transactions.R
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.util.LoadingContent
import nz.co.test.transactions.util.TransactionDetailTopAppBar

@Composable
fun TransactionDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TransactionDetailTopAppBar(
                onBack
            )
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TransactionContent(
            loading = uiState.isLoading,
            empty = uiState.transaction == null && !uiState.isLoading,
            transaction = uiState.transaction,
            modifier = Modifier.padding(paddingValues)
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { userMessage ->
            val snackbarText = stringResource(userMessage)
            LaunchedEffect(snackbarHostState, viewModel, userMessage, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
            }
        }
    }
}

@Composable
private fun TransactionContent(
    loading: Boolean,
    empty: Boolean,
    transaction: Transaction?,
    modifier: Modifier
) {
    LoadingContent(
        loading = loading,
        empty = empty,
        emptyContent = {
            Text(
                text = stringResource(id = R.string.no_data),
            )
        },
        onRefresh = {}
    ) {
        if (transaction != null) {
            TransactionDetail(transaction, modifier)
        }
    }
}

@Composable
fun TransactionDetail(transaction: Transaction, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = transaction.formattedDate,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = transaction.summary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Amount", fontSize = 16.sp)
                    Text(
                        text = if (transaction.debit > 0) "-$${transaction.debit}" else "+$${transaction.credit}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = transaction.amountColor
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "GST (15%)", fontSize = 16.sp)
                    Text(
                        text = "$${transaction.formattedGst}",
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Transaction ID", fontSize = 16.sp)
                    Text(
                        text = transaction.id.toString(),
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
