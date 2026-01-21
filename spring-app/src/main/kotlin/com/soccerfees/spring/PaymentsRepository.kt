package com.soccerfees.spring

import com.soccerfees.common.domain.Expense
import com.soccerfees.common.domain.Payment
import com.soccerfees.common.domain.PaymentType
import com.soccerfees.common.domain.Player
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.Date
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@Repository
class PaymentsRepository(private val jdbcTemplate: JdbcTemplate) {
    fun createPlayer(name: String, joinedAt: LocalDate): Player {
        val player = Player(UUID.randomUUID(), name, joinedAt)
        jdbcTemplate.update(
            "INSERT INTO players (id, name, joined_at) VALUES (?, ?, ?)",
            player.id,
            player.name,
            Date.valueOf(player.joinedAt)
        )
        return player
    }

    fun listPlayers(): List<Player> = jdbcTemplate.query(
        "SELECT id, name, joined_at FROM players ORDER BY name"
    ) { rs, _ ->
        Player(
            rs.getObject("id", UUID::class.java),
            rs.getString("name"),
            rs.getObject("joined_at", LocalDate::class.java)
        )
    }

    fun recordPayment(playerId: UUID?, paidAt: LocalDate, amount: BigDecimal, type: PaymentType): Payment {
        val payment = Payment(UUID.randomUUID(), playerId, paidAt, amount, type)
        jdbcTemplate.update(
            "INSERT INTO payments (id, player_id, paid_at, amount, type) VALUES (?, ?, ?, ?, ?)",
            payment.id,
            payment.playerId,
            Date.valueOf(payment.paidAt),
            payment.amount,
            payment.type.name
        )
        return payment
    }

    fun listPayments(yearMonth: YearMonth): List<PaymentResponse> = jdbcTemplate.query(
        """
        SELECT p.id, p.player_id, p.paid_at, p.amount, p.type, pl.name AS player_name
        FROM payments p
        LEFT JOIN players pl ON p.player_id = pl.id
        WHERE EXTRACT(YEAR FROM paid_at) = ? AND EXTRACT(MONTH FROM paid_at) = ?
        ORDER BY paid_at
        """.trimIndent(),
        { rs, _ ->
            PaymentResponse(
                rs.getObject("id", UUID::class.java),
                rs.getObject("player_id", UUID::class.java),
                rs.getString("player_name"),
                rs.getObject("paid_at", LocalDate::class.java),
                rs.getBigDecimal("amount"),
                PaymentType.valueOf(rs.getString("type"))
            )
        },
        yearMonth.year,
        yearMonth.monthValue
    )

    fun recordExpense(spentAt: LocalDate, amount: BigDecimal, description: String): Expense {
        val expense = Expense(UUID.randomUUID(), spentAt, amount, description)
        jdbcTemplate.update(
            "INSERT INTO expenses (id, spent_at, amount, description) VALUES (?, ?, ?, ?)",
            expense.id,
            Date.valueOf(expense.spentAt),
            expense.amount,
            expense.description
        )
        return expense
    }

    fun monthlySummary(yearMonth: YearMonth): MonthlySummaryResponse {
        val income = sumAmount("payments", "paid_at", yearMonth)
        val expenses = sumAmount("expenses", "spent_at", yearMonth)
        return MonthlySummaryResponse(
            yearMonth.year,
            yearMonth.monthValue,
            income,
            expenses,
            income.subtract(expenses)
        )
    }

    private fun sumAmount(table: String, dateColumn: String, yearMonth: YearMonth): BigDecimal {
        val value = jdbcTemplate.queryForObject(
            """
            SELECT COALESCE(SUM(amount), 0)
            FROM $table
            WHERE EXTRACT(YEAR FROM $dateColumn) = ? AND EXTRACT(MONTH FROM $dateColumn) = ?
            """.trimIndent(),
            BigDecimal::class.java,
            yearMonth.year,
            yearMonth.monthValue
        )
        return value ?: BigDecimal.ZERO
    }
}
