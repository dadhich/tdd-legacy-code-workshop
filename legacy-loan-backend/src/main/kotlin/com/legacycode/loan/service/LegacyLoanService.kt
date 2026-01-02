package com.legacycode.loan.service

import com.legacycode.loan.external.EmailService
import com.legacycode.loan.external.ExternalCreditBureau
import com.legacycode.loan.model.LoanEntity
import com.legacycode.loan.model.LoanRepository
import com.legacycode.loan.model.LoanRequestDTO
import com.legacycode.loan.model.LoanResponseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.Clock

@Service
class LegacyLoanService(
    // Dependencies...
    private val loanRepository: LoanRepository,
    private val clock: Clock = Clock.systemDefaultZone()
) {

    // Hard Dependency: Direct instantiation instead of Injection
    private val emailService = EmailService("smtp.corporate.com")

    fun processApplication(request: LoanRequestDTO): LoanResponseDTO {
        println("Processing application for ${request.applicantName}")

        // ---- FLAW 1: Hidden Temporal Dependency ----
        // This code behaves differently depending on when you run it.
        // Tests will fail randomly outside business hours.
        val currentHour = LocalDateTime.now(clock).hour
        if (currentHour < 8 || currentHour > 18) {
            // We return HTTP 200 OK but with an error message. Bad API design.
            return LoanResponseDTO("ERROR", "System only available between 8am and 6pm.")
        }

        // ---- FLAW 2: Mixed Concerns (Validation mixed with business logic) ----
        if (request.amount > 1000000) {
            return LoanResponseDTO("REJECTED", "Loan amount too high for auto-approval.")
        }
        if (request.applicantAge < 18) {
            return LoanResponseDTO("REJECTED", "Applicant too young.")
        }

        // ---- FLAW 3: TIGHT COUPLING to Static External Call ----
        // You cannot unit test this method without the CreditBureau "API" being available.
        // If taxId starts with "999", this throws an exception and crashes the flow.
        val creditScore = ExternalCreditBureau.getCreditScore(request.taxId)
        println("Credit score received: $creditScore")


        var status = "PENDING"
        val message: String

        // Core Business Logic
        if (creditScore >= 700) {
            status = "APPROVED"
            message = "Congratulations! Loan approved based on excellent credit."
        } else if (creditScore >= 500 && request.amount < 50000) {
            status = "APPROVED"
            message = "Loan approved based on moderate credit and low amount."
        } else {
            status = "REJECTED"
            message = "Sorry, credit score does not meet requirements."
        }

        // ---- FLAW 4: SIDE EFFECTS (Database Write) ----
        // Mixed in with logic.
        val savedEntity = loanRepository.save(
            LoanEntity(
                applicantName = request.applicantName,
                amount = request.amount,
                status = status
            )
        )

        // ---- FLAW 5: SIDE EFFECTS (Email Notification) & Poor Error Handling ----
        if (status == "APPROVED") {
            try {
                // If this fails, we swallow the error.
                emailService.sendConfirmation("admin@bank.com", "Loan ${savedEntity.id} approved.")
            } catch (e: Exception) {
                // Logging to stdout...
                println("Failed to send email, but continuing anyway: ${e.message}")
            }
        }

        return LoanResponseDTO(status, message, savedEntity.id)
    }
}