package ru.dars.darsapp.supplieraccount

import androidx.activity.ComponentActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.dars.darsapp.*
import ru.dars.darsapp.core.util.Resource
import ru.dars.darsapp.domain.local.usecase.GetStoredJwt
import ru.dars.darsapp.domain.remote.models.supplier.SupplierAccountCreate
import ru.dars.darsapp.domain.remote.usecase.GetSupplierAccountsUseCase
import ru.dars.darsapp.domain.remote.usecase.GetSuppliersUseCase
import ru.dars.darsapp.domain.remote.usecase.SendSupplierAccountUseCase
import ru.dars.darsapp.ui.main.mainPage.profile.accounts.SupplierAccountViewModel
import ru.dars.darsapp.ui.main.mainPage.profile.accounts.SupplierAccounts

class SupplierAccountUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        RETURN_SET_2 = false

        val getStoredJwt = mockk<GetStoredJwt>()
        every { getStoredJwt.execute() } returns "test_token"

        val getSuppliersUseCase = mockk<GetSuppliersUseCase>()
        coEvery { getSuppliersUseCase.execute(any()) } returns Resource.success(SUPPLIERS)

        val getSupplierAccountsUseCase = mockk<GetSupplierAccountsUseCase>()
        coEvery { getSupplierAccountsUseCase.execute(any()) } answers {
            Resource.success(
                if (RETURN_SET_2)
                    ACCOUNTS_2
                else
                    ACCOUNTS_1
            )
        }

        val sendSupplierAccountUseCase = mockk<SendSupplierAccountUseCase>()

        coEvery { sendSupplierAccountUseCase.execute(
            SupplierAccountCreate("99998", 103, 0, 202), any())
        } returns Resource.success(SUPPLIER_ACCOUNT)

        coEvery { sendSupplierAccountUseCase.execute(
            SupplierAccountCreate("99999", 103, 0, 202), any())
        } returns Resource.error("wrong account")

        val viewModel = SupplierAccountViewModel(
            getStoredJwt,
            getSuppliersUseCase,
            getSupplierAccountsUseCase,
            sendSupplierAccountUseCase
        )

        viewModel.setAddresses(ADDRESSES)

        composeTestRule.setContent {
            SupplierAccounts(
                supplierAccountViewModel = viewModel,
                backClickAction = {}
            )
        }
    }

    @Test
    fun app_launches() {
        // Check that the screen is visible on launch
        composeTestRule.onNodeWithTag("ModalBottomSheetLayout_supplierAccounts").assertIsDisplayed()
    }

    @Test
    fun first_address_has_missing_account() {
        // Check if button with text is visible
        findCompWithText("Добавить лицевой счет").assertIsDisplayed()
    }

    @Test
    fun second_address_all_accounts_set() {
        // scroll to 2nd address
        findCompWithText("test-addr-2").performClick()

        // Check if button with text is NOT visible
        findCompWithText("Добавить лицевой счет").assertDoesNotExist()
    }

    @Test
    fun account_enter_field_cleared_when_address_changed() {
        // start add account flow
        findCompWithText("Добавить лицевой счет").performClick()

        // choose supplier
        findCompWithText("supplier-202").performClick()

        // enter account number
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("123456")

        // check if enter field is empty
        composeTestRule.onNodeWithTag("EnterAccountField").assertTextEquals("123456")

        // switch to another address
        findCompWithText("test-addr-3").performClick()

        // check if enter field is empty
        composeTestRule.onNodeWithTag("EnterAccountField").assertTextEquals("")
    }

    @Test
    fun wrong_account_then_edit_then_valid() {
        // switch to address #3
        findCompWithText("test-addr-3").performClick()

        // start add account flow
        findCompWithText("Добавить лицевой счет").performClick()

        // choose supplier
        findCompWithText("supplier-202").performClick()

        // enter wrong account number & send
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("99999")
        findCompWithText("Готово").performClick()

        // check if no account displayed
        findCompWithText("Лицевой счет не найден").assertIsDisplayed()

        // enter correct account number & send
        composeTestRule.onNodeWithTag("EnterAccountField").performTextClearance()
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("99998")
        findCompWithText("Готово").performClick()

        // check if account added displayed
        findCompWithText("Лицевой счет добавлен").assertIsDisplayed()
    }

    @Test
    fun when_account_added_address_disappears() {
        // switch to address #3
        findCompWithText("test-addr-3").performClick()

        // start add account flow
        findCompWithText("Добавить лицевой счет").performClick()

        // choose supplier
        findCompWithText("supplier-202").performClick()

        // check if both accounts displayed
        findCompWithText("test-addr-1").assertIsDisplayed()
        findCompWithText("test-addr-3").assertIsDisplayed()

        // choose address-3, enter correct account number & send
        findCompWithText("test-addr-3").performClick()
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("99998")

        RETURN_SET_2 = true
        findCompWithText("Готово").performClick()

        // check if account created displayed
        findCompWithText("Лицевой счет добавлен").assertIsDisplayed()

        // choose address-1 and & check address-3 disappears from scroll
        findCompWithText("test-addr-1").performClick()
        findCompWithText("test-addr-3").assertDoesNotExist()
    }

    @Test
    fun when_account_added_account_edit_disabled() {
        // switch to address #3
        findCompWithText("test-addr-3").performClick()

        // start add account flow
        findCompWithText("Добавить лицевой счет").performClick()

        // choose supplier
        findCompWithText("supplier-202").performClick()

        // check if both accounts displayed
        findCompWithText("test-addr-1").assertIsDisplayed()
        findCompWithText("test-addr-3").assertIsDisplayed()

        // choose address-3, enter correct account number & send
        findCompWithText("test-addr-3").performClick()
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("99998")

        RETURN_SET_2 = true
        findCompWithText("Готово").performClick()

        // check if account created displayed and text edit is diabled
        findCompWithText("Лицевой счет добавлен").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EnterAccountField").assertIsNotEnabled()
    }

    @Test
    fun added_and_is_in_the_list_after_bsd_closed() {
        // switch to address #3
        findCompWithText("test-addr-3").performClick()

        // start add account flow
        findCompWithText("Добавить лицевой счет").performClick()

        // choose supplier
        findCompWithText("supplier-202").performClick()

        // enter wrong account number & send
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("99999")
        findCompWithText("Готово").performClick()

        // check if no account displayed
        findCompWithText("Лицевой счет не найден").assertIsDisplayed()

        // enter correct account number & send
        composeTestRule.onNodeWithTag("EnterAccountField").performTextClearance()
        composeTestRule.onNodeWithTag("EnterAccountField").performTextInput("99998")

        RETURN_SET_2 = true
        findCompWithText("Готово").performClick()

        // check if no account displayed
        findCompWithText("Лицевой счет добавлен").assertIsDisplayed()

        // minimize BSD and check new account in the list
        composeTestRule.onNodeWithTag("EnterAccountField").performTouchInput {
            this.swipe(
                start = this.center,
                end = Offset(this.center.x, this.center.y + 500),
                durationMillis = 500
            )
        }

        findCompWithText("99998").assertIsDisplayed()
    }

    private fun findCompWithText(text: String) =
        composeTestRule.onNodeWithText(
            text,
            useUnmergedTree = true
        )

}