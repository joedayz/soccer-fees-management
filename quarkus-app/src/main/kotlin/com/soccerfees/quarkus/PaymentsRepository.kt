package com.soccerfees.quarkus

import com.soccerfees.common.domain.Expense
import com.soccerfees.common.domain.Payment
import com.soccerfees.common.domain.PaymentType
import com.soccerfees.common.domain.Player
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.math.BigDecimal
import java.sql.Connection
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import javax.sql.DataSource

@ApplicationScoped
class PaymentsRepository @Inject constructor(private val dataSource: DataSource) {
    fun createPlayer(name: String, joinedAt: LocalDate): Player {
        val player = Player(UUID.randomUUID(), name, joinedAt)
        dataSource.useConnection { connection ->
            connection.prepareStatement(
                "INSERT INTO players (id, name, joined_at) VALUES (?, ?, ?)"
            ).use { stmt ->
                stmt.setObject(1, player.id)
                stmt.setString(2, player.name)
                stmt.setObject(3, player.joinedAt)
                stmt.executeUpdate()
            }
        }
        return player
    }

    fun listPlayers(): List<Player> = dataSource.useConnection { connection ->
        connection.prepareStatement(
            "SELECT id, name, joined_at FROM players ORDER BY name"
        ).use { stmt ->
            stmt.executeQuery().use { rs ->
                val results = mutableListOf<Player>()
                while (rs.next()) {
                    results.add(
                        Player(
                            rs.getObject("id", UUID::class.java),
                            rs.getString("name"),
                            rs.getObject("joined_at", LocalDate::class.java)
                        )
                    )
                }
                results
            }
        }
    }

    fun recordPayment(playerId: UUID?, paidAt: LocalDate, amount: BigDecimal, type: PaymentType): Payment {
        val payment = Payment(UUID.randomUUID(), playerId, paidAt, amount, type)
        dataSource.useConnection { connection ->
            connection.prepareStatement(
                "INSERT INTO payments (id, player_id, paid_at, amount, type) VALUES (?, ?, ?, ?, ?)"
            ).use { stmt ->
                stmt.setObject(1, payment.id)
                stmt.setObject(2, payment.playerId)
                stmt.setObject(3, payment.paidAt)
                stmt.setBigDecimal(4, payment.amount)
                stmt.setString(5, payment.type.name)
                stmt.executeUpdate()
            }
        }
        return payment
    }

    fun listPayments(yearMonth: YearMonth): List<PaymentResponse> = dataSource.useConnection { connection ->
        connection.prepareStatement(
            """
            SELECT p.id, p.player_id, p.paid_at, p.amount, p.type, pl.name AS player_name
            FROM payments p
            LEFT JOIN players pl ON p.player_id = pl.id
            WHERE EXTRACT(YEAR FROM paid_at) = ? AND EXTRACT(MONTH FROM paid_at) = ?
            ORDER BY paid_at
            """.trimIndent()
        ).use { stmt ->
            stmt.setInt(1, yearMonth.year)
            stmt.setInt(2, yearMonth.monthValue)
            stmt.executeQuery().use { rs ->
                val results = mutableListOf<PaymentResponse>()
                while (rs.next()) {
                    results.add(
                        PaymentResponse(
                            rs.getObject("id", UUID::class.java),
                            rs.getObject("player_id", UUID::class.java),
                            rs.getString("player_name"),
                            rs.getObject("paid_at", LocalDate::class.java),
                            rs.getBigDecimal("amount"),
                            PaymentType.valueOf(rs.getString("type"))
                        )
                    )
                }
                results
            }
        }
    }

    fun recordExpense(spentAt: LocalDate, amount: BigDecimal, description: String): Expense {
        val expense = Expense(UUID.randomUUID(), spentAt, amount, description)
        dataSource.useConnection { connection ->
            connection.prepareStatement(
                "INSERT INTO expenses (id, spent_at, amount, description) VALUES (?, ?, ?, ?)"
            ).use { stmt ->
                stmt.setObject(1, expense.id)
                stmt.setObject(2, expense.spentAt)
                stmt.setBigDecimal(3, expense.amount)
                stmt.setString(4, expense.description)
                stmt.executeUpdate()
            }
        }
        return expense
    }

    fun monthlySummary(yearMonth: YearMonth): MonthlySummaryResponse = dataSource.useConnection { connection ->
        val income = sumAmount(connection, "payments", "paid_at", yearMonth)
        val expenses = sumAmount(connection, "expenses", "spent_at", yearMonth)
        MonthlySummaryResponse(
            yearMonth.year,
            yearMonth.monthValue,
            income,
            expenses,
            income.subtract(expenses)
        )
    }

    private fun sumAmount(connection: Connection, table: String, dateColumn: String, yearMonth: YearMonth): BigDecimal {
        connection.prepareStatement(
            """
            SELECT COALESCE(SUM(amount), 0)
            FROM $table
            WHERE EXTRACT(YEAR FROM $dateColumn) = ? AND EXTRACT(MONTH FROM $dateColumn) = ?
            """.trimIndent()
        ).use { stmt ->
            stmt.setInt(1, yearMonth.year)
            stmt.setInt(2, yearMonth.monthValue)
            stmt.executeQuery().use { rs ->
                return if (rs.next()) rs.getBigDecimal(1) else BigDecimal.ZERO
            }
        }
    }
}

private fun <T> DataSource.useConnection(block: (Connection) -> T): T {
    return connection.use(block)
}
