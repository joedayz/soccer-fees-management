package com.soccerfees.common.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

enum class PaymentType {
    MEMBER_FEE,
    GUEST_FEE
}

data class Payment(
    val id: UUID,
    val playerId: UUID?,
    val paidAt: LocalDate,
    val amount: BigDecimal,
    val type: PaymentType
)
