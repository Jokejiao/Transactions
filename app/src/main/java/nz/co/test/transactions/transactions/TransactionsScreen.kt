package nz.co.test.transactions.transactions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nz.co.test.transactions.R
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.util.LoadingContent
import nz.co.test.transactions.util.TransactionsTopAppBar

@Composable
fun TransactionsScreen(
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TransactionsTopAppBar(
                onRefresh = { viewModel.refresh() }
            )
        },
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TransactionsContent(
            loading = uiState.isLoading,
            transactions = uiState.items,
            noTransactionsLabel = uiState.noTransactionsUiInfo.noTransactionsLabel,
            noTransactionsIconRes = uiState.noTransactionsUiInfo.noTransactionsIconRes,
            onRefresh = viewModel::refresh,
            onTransactionClick = onTransactionClick,
            modifier = Modifier.padding(paddingValues)
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarHostState, viewModel, message, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }
    }
}

@Composable
private fun TransactionsContent(
    loading: Boolean,
    transactions: List<Transaction>,
    @StringRes noTransactionsLabel: Int,
    @DrawableRes noTransactionsIconRes: Int,
    onRefresh: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    LoadingContent(
        loading = loading,
        empty = transactions.isEmpty() && !loading,
        emptyContent = {
            TransactionsEmptyContent(
                noTransactionsLabel,
                noTransactionsIconRes,
                modifier
            )
        },
        onRefresh = onRefresh
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
        ) {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onTransactionClick = onTransactionClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onTransactionClick: (Transaction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.formattedDate,
                    fontSize = 14.sp,
                    color = Color.Gray,
                )

                Text(
                    text = transaction.summary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.formattedDebit,
                    color = transaction.amountColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = transaction.formattedCredit,
                    color = transaction.amountColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "GST (15%): ${transaction.formattedGst}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun TransactionsEmptyContent(
    @StringRes noTransactionsLabel: Int,
    @DrawableRes noTransactionsIconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noTransactionsIconRes),
            contentDescription = stringResource(R.string.no_transactions_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = noTransactionsLabel))
    }
}