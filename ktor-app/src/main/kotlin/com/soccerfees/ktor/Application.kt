package com.soccerfees.ktor

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.serialization.jackson.jackson
import java.time.YearMonth

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "invalid request")))
        }
    }

    val repository = PaymentsRepository(DatabaseFactory.createDataSource())

    routing {
        route("/api") {
            get("/health") {
                call.respond(mapOf("status" to "ok"))
            }

            get("/players") {
                call.respond(repository.listPlayers())
            }

            post("/players") {
                val request = call.receive<CreatePlayerRequest>()
                call.respond(repository.createPlayer(request.name, request.joinedAt))
            }

            get("/payments") {
                val yearMonth = call.requireYearMonth()
                call.respond(repository.listPayments(yearMonth))
            }

            post("/payments") {
                val request = call.receive<CreatePaymentRequest>()
                call.respond(repository.recordPayment(request.playerId, request.paidAt, request.amount, request.type))
            }

            post("/expenses") {
                val request = call.receive<CreateExpenseRequest>()
                call.respond(repository.recordExpense(request.spentAt, request.amount, request.description))
            }

            get("/summary") {
                val yearMonth = call.requireYearMonth()
                call.respond(repository.monthlySummary(yearMonth))
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.requireYearMonth(): YearMonth {
    val year = request.queryParameters["year"]?.toIntOrNull()
    val month = request.queryParameters["month"]?.toIntOrNull()
    if (year == null || month == null) {
        throw IllegalArgumentException("year and month query params are required")
    }
    return YearMonth.of(year, month)
}
