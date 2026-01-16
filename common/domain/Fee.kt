package com.soccerfees.common.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

object Fees {
    val memberMonthly: BigDecimal = BigDecimal("15.00")
    val guestFee: BigDecimal = BigDecimal("5.00")
    val fieldRent: BigDecimal = BigDecimal("40.00")
}

data class Expense(
    val id: UUID,
    val spentAt: LocalDate,
    val amount: BigDecimal,
    val description: String
)
