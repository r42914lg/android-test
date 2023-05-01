package ru.dars.darsapp.supplieraccount

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
import ru.dars.darsapp.ui.main.mainPage.profile.accounts.AccountAction
import ru.dars.darsapp.ui.main.mainPage.profile.accounts.SupplierAccountViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class SupplierAccountVmTest {

    private lateinit var vm: SupplierAccountViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
            SupplierAccountCreate("99998", 101, 0, 202), any())
        } returns Resource.success(SUPPLIER_ACCOUNT)

        coEvery { sendSupplierAccountUseCase.execute(
            SupplierAccountCreate("99999", 101, 0, 202), any())
        } returns Resource.error("wrong account")

        val viewModel = SupplierAccountViewModel(
            getStoredJwt,
            getSuppliersUseCase,
            getSupplierAccountsUseCase,
            sendSupplierAccountUseCase
        )

        viewModel.setAddresses(ADDRESSES)
        vm = viewModel
    }

    @Test
    fun `#1 - when init state Loading state ShowAccountList`() = runTest() {
        vm.state.test {

            assertEquals(SupplierAccountViewModel.AccountState.Loading, awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                    listOf(ACC_1), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_1, true), awaitItem())

            cancel()
        }
    }

    @Test
    fun `#2 - when address selected state ShowAccountList with address`() = runTest {
        vm.state.test {

            assertEquals(SupplierAccountViewModel.AccountState.Loading, awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_1), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_1, true), awaitItem())

            vm.onAction(AccountAction.AddressSelected(ADDR_2))
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_2), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_2, false), awaitItem())

            cancel()
        }
    }

    @Test
    fun `#3 - when BSD minimized state Loading then ShowAccountList`() = runTest {
        vm.state.test {

            assertEquals(SupplierAccountViewModel.AccountState.Loading, awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_1), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_1, true), awaitItem())

            vm.onAction(AccountAction.EnterAccount(ADDR_1))
            skipItems(1)

            vm.onAction(AccountAction.BsdDismissed)
            skipItems(1)
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_1), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_1, true), awaitItem())

            cancel()
        }
    }

    @Test
    fun `#4 - first wrong account then correct - check proper state`() = runTest {
        vm.state.test {

            assertEquals(SupplierAccountViewModel.AccountState.Loading, awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_1), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_1, true), awaitItem())

            vm.onAction(AccountAction.EnterAccount(ADDR_1))
            assertEquals(SupplierAccountViewModel.AccountState.ShowSupplierList(listOf(SUP_2), ADDR_1), awaitItem())

            vm.onAction(AccountAction.SupplierSelected(SUP_2, ADDR_1))
            assertEquals(SupplierAccountViewModel.AccountState.CreateAccount(listOf(ADDR_1, ADDR_3), ADDR_1, SUP_2, "", SupplierAccountViewModel.AccountState.NewAccountState.ENTER_NEW), awaitItem())

            vm.onAction(AccountAction.CheckAccount("99999", SUP_2, ADDR_1))
            assertEquals(SupplierAccountViewModel.AccountState.CreateAccount(listOf(ADDR_1, ADDR_3), ADDR_1, SUP_2, "99999", SupplierAccountViewModel.AccountState.NewAccountState.CHECKING), awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.CreateAccount(listOf(ADDR_1, ADDR_3), ADDR_1, SUP_2, "99999", SupplierAccountViewModel.AccountState.NewAccountState.WRONG), awaitItem())

            vm.onAction(AccountAction.CheckAccount("99998", SUP_2, ADDR_1))
            assertEquals(SupplierAccountViewModel.AccountState.CreateAccount(listOf(ADDR_1, ADDR_3), ADDR_1, SUP_2, "99998", SupplierAccountViewModel.AccountState.NewAccountState.CHECKING), awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.CreateAccount(listOf(ADDR_1, ADDR_3), ADDR_1, SUP_2, "99998", SupplierAccountViewModel.AccountState.NewAccountState.CREATED), awaitItem())

            cancel()
        }
    }

    @Test
    fun `#5 - check accounts list changes on address selected`() = runTest {
        vm.state.test {

            assertEquals(SupplierAccountViewModel.AccountState.Loading, awaitItem())
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_1), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_1, true), awaitItem())

            vm.onAction(AccountAction.AddressSelected(ADDR_2))
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(ACC_2), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_2, false), awaitItem())

            vm.onAction(AccountAction.AddressSelected(ADDR_3))
            assertEquals(SupplierAccountViewModel.AccountState.ShowAccountList(
                listOf(), listOf(ADDR_1, ADDR_2, ADDR_3), ADDR_3, true), awaitItem())

            cancel()
        }
    }

}