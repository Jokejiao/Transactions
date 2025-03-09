package nz.co.test.transactions.transactiondetail

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import nz.co.test.transactions.HiltTestActivity
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
 * Integration test for the Transaction Details screen.
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@ExperimentalCoroutinesApi
class TransactionDetailScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: TransactionRepository

    private val transaction = Transaction(
        id = 1,
        transactionDate = "2022-02-06T12:41:09",
        summary = "Jones, Moen and Kirlin",
        debit = 805.41,
        credit = 0.0
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun activeTransactionDetails_DisplayedInUi() = runTest {
        (repository as FakeTransactionRepository).addTransactions(transaction)

        setContent(transaction.id.toString())

        composeTestRule.onNodeWithText(transaction.summary).assertIsDisplayed()
    }

    private fun setContent(transactionId: String) {
        composeTestRule.setContent {
            TransactionsTheme {
                Surface {
                    TransactionDetailScreen(
                        viewModel = TransactionDetailViewModel(
                            repository,
                            SavedStateHandle(mapOf("transactionId" to transactionId))
                        ),
                        onBack = { },
                    )
                }
            }
        }
    }
}
