package ru.dars.darsapp

import androidx.lifecycle.liveData
import ru.dars.darsapp.domain.models.local.CustomAddress
import ru.dars.darsapp.domain.remote.models.address.Address
import ru.dars.darsapp.domain.remote.models.address.Building
import ru.dars.darsapp.domain.remote.models.address.ResidentialComplex
import ru.dars.darsapp.domain.remote.models.supplier.Supplier
import ru.dars.darsapp.domain.remote.models.supplier.SupplierAccount

val ADDR_1 = CustomAddress(
    Address(101, "number-101", 1, "account_1001", 100.0,
        Building(
            "building-address-101",
            "building-entrance-101",
            ResidentialComplex("complex-1"),
            listOf(201, 202)
        )),
    "test-addr-1"
)

val ADDR_2 = CustomAddress(
    Address(102, "number-102", 1, "account_1002", 200.0,
        Building(
            "building-address-102",
            "building-entrance-102",
            ResidentialComplex("complex-1"),
            listOf(202)
        )),
    "test-addr-2"
)

val ADDR_3 = CustomAddress(
    Address(103, "number-103", 1, "account_1003", 300.0,
        Building(
            "building-address-103",
            "building-entrance-103",
            ResidentialComplex("complex-1"),
            listOf(202)
        )),
    "test-addr-3"
)

val ADDRESSES = liveData { emit(listOf(ADDR_1, ADDR_2, ADDR_3)) }

val SUP_1 = Supplier(201, "supplier-201", "", "", "", "")
val SUP_2 = Supplier(202, "supplier-202", "", "", "", "")

val SUPPLIERS = listOf(SUP_1, SUP_2)

var RETURN_SET_2 = false

val ACC_1 = SupplierAccount(301, "acc-number-301", 101, 0,
    Supplier(201, "supplier-201", "", "", "", ""),
    "", ""
)

val ACC_2 = SupplierAccount(302, "acc-number-302", 102, 0,
    Supplier(202, "supplier-202", "", "", "", ""),
    "", ""
)

val ACCOUNTS_1 = listOf(ACC_1, ACC_2)

val SUPPLIER_ACCOUNT = SupplierAccount(399, "1000999", 103, 0,
    Supplier(202, "supplier-202", "", "", "", ""),
    "", ""
)

val ACCOUNTS_2 = listOf(ACC_1, ACC_2, SUPPLIER_ACCOUNT)