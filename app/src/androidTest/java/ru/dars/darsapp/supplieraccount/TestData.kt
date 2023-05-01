package ru.dars.darsapp

import androidx.lifecycle.liveData
import ru.dars.darsapp.domain.models.local.CustomAddress
import ru.dars.darsapp.domain.remote.models.address.Address
import ru.dars.darsapp.domain.remote.models.address.Building
import ru.dars.darsapp.domain.remote.models.address.ResidentialComplex
import ru.dars.darsapp.domain.remote.models.supplier.Supplier
import ru.dars.darsapp.domain.remote.models.supplier.SupplierAccount

val ADDRESSES = liveData { emit(listOf(
    CustomAddress(
        Address(101, "number-101", 1, "account_1001", 100.0,
            Building(
                "building-address-101",
                "building-entrance-101",
                ResidentialComplex("complex-1"),
                listOf(201, 202)
            )),
        "test-addr-1"
    ),
    CustomAddress(
        Address(102, "number-102", 1, "account_1002", 200.0,
            Building(
                "building-address-102",
                "building-entrance-102",
                ResidentialComplex("complex-1"),
                listOf(202)
            )),
        "test-addr-2"
    ),
    CustomAddress(
        Address(103, "number-103", 1, "account_1003", 300.0,
            Building(
                "building-address-103",
                "building-entrance-103",
                ResidentialComplex("complex-1"),
                listOf(202)
            )),
        "test-addr-3"
    ),
)) }

val SUPPLIERS = listOf(
    Supplier(201, "supplier-201", "", "", "", ""),
    Supplier(202, "supplier-202", "", "", "", ""),
)

var RETURN_SET_2 = false

val ACCOUNTS_1 = listOf(
    SupplierAccount(301, "acc-number-301", 101, 0,
        Supplier(201, "supplier-201", "", "", "", ""),
        "", ""
    ),
    SupplierAccount(302, "acc-number-302", 102, 0,
        Supplier(202, "supplier-202", "", "", "", ""),
        "", ""
    )
)

val SUPPLIER_ACCOUNT = SupplierAccount(399, "1000999", 103, 0,
    Supplier(202, "supplier-202", "", "", "", ""),
    "", ""
)

val ACCOUNTS_2 = listOf(
    SupplierAccount(301, "acc-number-301", 101, 0,
        Supplier(201, "supplier-201", "", "", "", ""),
        "", ""
    ),
    SupplierAccount(302, "acc-number-302", 102, 0,
        Supplier(202, "supplier-202", "", "", "", ""),
        "", ""
    ),
    SUPPLIER_ACCOUNT
)