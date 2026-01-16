package com.soccerfees.micronaut

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import java.time.YearMonth

@Controller("/api")
class PaymentsController(private val repository: PaymentsRepository) {
    @Get("/health")
    fun health(): Map<String, String> = mapOf("status" to "ok")

    @Get("/players")
    fun players() = repository.listPlayers()

    @Post("/players")
    fun createPlayer(@Body request: CreatePlayerRequest) =
        repository.createPlayer(request.name, request.joinedAt)

    @Get("/payments")
    fun payments(@QueryValue year: Int, @QueryValue month: Int) =
        repository.listPayments(YearMonth.of(year, month))

    @Post("/payments")
    fun createPayment(@Body request: CreatePaymentRequest) =
        repository.recordPayment(request.playerId, request.paidAt, request.amount, request.type)

    @Post("/expenses")
    fun createExpense(@Body request: CreateExpenseRequest) =
        repository.recordExpense(request.spentAt, request.amount, request.description)

    @Get("/summary")
    fun summary(@QueryValue year: Int, @QueryValue month: Int) =
        repository.monthlySummary(YearMonth.of(year, month))
}
