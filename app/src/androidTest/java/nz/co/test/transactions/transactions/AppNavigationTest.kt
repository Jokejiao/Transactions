package nz.co.test.transactions.transactions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.HiltTestActivity
import nz.co.test.transactions.R
import nz.co.test.transactions.TransactionNavGraph
import nz.co.test.transactions.TransactionsTheme
import nz.co.test.transactions.data.FakeTransactionRepository
import nz.co.test.transactions.data.Transaction
import nz.co.test.transactions.data.TransactionRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Tests for scenarios that requires navigating within the app.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class AppNavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes transactions in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private val transaction1 = Transaction(
        id = 1,
        transactionDate = "2022-02-06T12:41:09",
        summary = "Jones, Moen and Kirlin",
        debit = 805.41,
        credit = 0.0
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun transactionDetailScreen_backButton() = runTest {
        (transactionRepository as FakeTransactionRepository).addTransactions(transaction1)

        setContent()

        // Click on the transaction on the list
        composeTestRule.onNodeWithText(transaction1.summary).assertIsDisplayed()
        composeTestRule.onNodeWithText(transaction1.summary).performClick()

        // The Transaction detail screen is shown
        composeTestRule.onNodeWithText("Transaction ID").assertIsDisplayed()
        // Confirm that if we click "<-" once, we end up back at the transaction details page
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.menu_back))
            .performClick()
        composeTestRule.onNodeWithText("Refresh").assertIsDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent {
            TransactionsTheme {
                TransactionNavGraph()
            }
        }
    }
}
