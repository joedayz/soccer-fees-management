package com.soccerfees.quarkus

import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import java.time.YearMonth

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PaymentsResource @Inject constructor(private val repository: PaymentsRepository) {
    @GET
    @Path("/health")
    fun health(): Map<String, String> = mapOf("status" to "ok")

    @GET
    @Path("/players")
    fun players() = repository.listPlayers()

    @POST
    @Path("/players")
    fun createPlayer(request: CreatePlayerRequest) =
        repository.createPlayer(request.name, request.joinedAt)

    @GET
    @Path("/payments")
    fun payments(@QueryParam("year") year: Int, @QueryParam("month") month: Int) =
        repository.listPayments(YearMonth.of(year, month))

    @POST
    @Path("/payments")
    fun createPayment(request: CreatePaymentRequest) =
        repository.recordPayment(request.playerId, request.paidAt, request.amount, request.type)

    @POST
    @Path("/expenses")
    fun createExpense(request: CreateExpenseRequest) =
        repository.recordExpense(request.spentAt, request.amount, request.description)

    @GET
    @Path("/summary")
    fun summary(@QueryParam("year") year: Int, @QueryParam("month") month: Int) =
        repository.monthlySummary(YearMonth.of(year, month))
}
