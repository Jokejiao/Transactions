package nz.co.test.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import nz.co.test.transactions.transactiondetail.TransactionDetailScreen
import nz.co.test.transactions.transactions.TransactionsScreen

@Composable
fun TransactionNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = TransactionDestinations.TRANSACTIONS_ROUTE,
    navActions: TransactionNavigationActions = remember(navController) {
        TransactionNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(TransactionDestinations.TRANSACTIONS_ROUTE) {
            TransactionsScreen(
                onTransactionClick = { transaction ->
                    navActions.navigateToTransactionDetail(
                        transaction.id
                    )
                }
            )
        }
        composable(TransactionDestinations.TRANSACTION_DETAIL_ROUTE) {
            TransactionDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}