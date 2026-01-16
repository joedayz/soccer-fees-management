package com.soccerfees.common.domain

import java.time.LocalDate
import java.util.UUID

data class Player(
    val id: UUID,
    val name: String,
    val joinedAt: LocalDate
)
