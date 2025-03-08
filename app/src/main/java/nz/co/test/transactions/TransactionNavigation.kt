package nz.co.test.transactions

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import nz.co.test.transactions.TransactionDestinationsArgs.TRANSACTION_ID_ARG
import nz.co.test.transactions.TransactionScreens.TRANSACTIONS_SCREEN
import nz.co.test.transactions.TransactionScreens.TRANSACTION_DETAIL_SCREEN

/**
 * Screens used in [TransactionDestinations]
 */
private object TransactionScreens {
    const val TRANSACTIONS_SCREEN = "transactions"
    const val TRANSACTION_DETAIL_SCREEN = "transaction"
}

/**
 * Arguments used in [TransactionDestinations] routes
 */
object TransactionDestinationsArgs {
    const val TRANSACTION_ID_ARG = "transactionId"
}

/**
 * Destinations used in the [TransactionActivity]
 */
object TransactionDestinations {
    const val TRANSACTIONS_ROUTE = TRANSACTIONS_SCREEN
    const val TRANSACTION_DETAIL_ROUTE = "$TRANSACTION_DETAIL_SCREEN/{$TRANSACTION_ID_ARG}"
}

/**
 * Models the navigation actions in the app.
 */
class TransactionNavigationActions(private val navController: NavHostController) {

    fun navigateToTransactions() {
        navController.navigate(TRANSACTIONS_SCREEN) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun navigateToTransactionDetail(transactionId: Long) {
        navController.navigate("$TRANSACTION_DETAIL_SCREEN/$transactionId")
    }
}
