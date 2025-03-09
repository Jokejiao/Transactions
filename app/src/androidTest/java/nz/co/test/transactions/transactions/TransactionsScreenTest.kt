package nz.co.test.transactions.transactions

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.HiltTestActivity
import nz.co.test.transactions.R
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
 * Integration test for the Transaction List screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class TransactionsScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var repository: TransactionRepository

    private val transaction1 = Transaction(
        id = 1,
        transactionDate = "2022-02-06T12:41:09",
        summary = "Jones, Moen and Kirlin",
        debit = 805.41,
        credit = 0.0
    )
    private val transaction2 = Transaction(
        id = 2,
        transactionDate = "2021-02-15T19:56:33",
        summary = "Effertz, Johnson and Spencer",
        debit = 465.27,
        credit = 0.0
    )
    private val transaction3 = Transaction(
        id = 3,
        transactionDate = "2021-12-06T01:36:13",
        summary = "Russel, Bradtke and VonRueden",
        debit = 0.0,
        credit = 8680.85
    )

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun displayTransaction_whenRepositoryHasData() = runTest {
        // GIVEN - One transaction already in the repository
        (repository as FakeTransactionRepository).addTransactions(transaction1)

        // WHEN - On startup
        setContent()

        // THEN - Verify transaction is displayed on screen
        composeTestRule.onNodeWithText(transaction1.summary).assertIsDisplayed()
    }

    @Test
    fun showAllTransactions() = runTest {
        // GIVEN - Three transactions already in the repository
        (repository as FakeTransactionRepository).addTransactions(
            transaction1,
            transaction2,
            transaction3
        )

        setContent()

        // Verify if there are three items in the list
        composeTestRule.onNodeWithText(transaction1.summary).assertIsDisplayed()
        composeTestRule.onNodeWithText(transaction2.summary).assertIsDisplayed()
        composeTestRule.onNodeWithText(transaction3.summary).assertIsDisplayed()
    }

    @Test
    fun showIconAndPrompt_whenRepositoryHasNoData() = runTest {
        setContent()

        // Verify if there are no item in the list
        composeTestRule.onAllNodesWithText("summary").assertCountEquals(0)
        // Verify if the icon is displayed
        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.no_transactions_image_content_description))
            .assertExists()
        // Verify if the prompt is displayed
        composeTestRule.onNodeWithText(activity.getString(R.string.transactions_are_being_loaded))
            .assertIsDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent {
            TransactionsTheme {
                Surface {
                    TransactionsScreen(
                        onTransactionClick = {}
                    )
                }
            }
        }
    }
}
