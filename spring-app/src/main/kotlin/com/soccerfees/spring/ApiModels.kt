package com.soccerfees.spring

import com.soccerfees.common.domain.PaymentType
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreatePlayerRequest(
    val name: String,
    val joinedAt: LocalDate
)

data class CreatePaymentRequest(
    val playerId: UUID?,
    val paidAt: LocalDate,
    val amount: BigDecimal,
    val type: PaymentType
)

data class CreateExpenseRequest(
    val spentAt: LocalDate,
    val amount: BigDecimal,
    val description: String
)

data class PaymentResponse(
    val id: UUID,
    val playerId: UUID?,
    val playerName: String?,
    val paidAt: LocalDate,
    val amount: BigDecimal,
    val type: PaymentType
)

data class MonthlySummaryResponse(
    val year: Int,
    val month: Int,
    val income: BigDecimal,
    val expenses: BigDecimal,
    val balance: BigDecimal
)
