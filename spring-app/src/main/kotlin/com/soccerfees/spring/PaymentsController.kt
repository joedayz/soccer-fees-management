package com.soccerfees.spring

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.YearMonth

@RestController
@RequestMapping("/api")
class PaymentsController(private val repository: PaymentsRepository) {
    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf("status" to "ok")

    @GetMapping("/players")
    fun players() = repository.listPlayers()

    @PostMapping("/players")
    fun createPlayer(@RequestBody request: CreatePlayerRequest) =
        repository.createPlayer(request.name, request.joinedAt)

    @GetMapping("/payments")
    fun payments(@RequestParam year: Int, @RequestParam month: Int) =
        repository.listPayments(YearMonth.of(year, month))

    @PostMapping("/payments")
    fun createPayment(@RequestBody request: CreatePaymentRequest) =
        repository.recordPayment(request.playerId, request.paidAt, request.amount, request.type)

    @PostMapping("/expenses")
    fun createExpense(@RequestBody request: CreateExpenseRequest) =
        repository.recordExpense(request.spentAt, request.amount, request.description)

    @GetMapping("/summary")
    fun summary(@RequestParam year: Int, @RequestParam month: Int) =
        repository.monthlySummary(YearMonth.of(year, month))
}
