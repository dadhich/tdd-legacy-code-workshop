package com.legacycode.loan.service

import com.legacycode.loan.external.EmailService
import com.legacycode.loan.external.ICreditBureau
import com.legacycode.loan.model.LoanEntity
import com.legacycode.loan.model.LoanRepository
import com.legacycode.loan.model.LoanRequestDTO
import com.legacycode.loan.model.LoanResponseDTO
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class LegacyLoanService(
    private val loanRepository: LoanRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
    private val emailService: EmailService,
    private val creditBureau: ICreditBureau,
    // CHANGED: Inject the new clean component
    private val riskAssessor: RiskAssessor
) {

    fun processApplication(request: LoanRequestDTO): LoanResponseDTO {
        println("Processing application for ${request.applicantName}")

        val currentHour = LocalDateTime.now(clock).hour

        if (currentHour < 8 || currentHour > 18) {
            return LoanResponseDTO("ERROR", "System only available between 8am and 6pm.")
        }

        // NEW FEATURE: High Risk Check
        // We do this BEFORE the expensive/fragile credit check
        if (riskAssessor.isHighRisk(request.applicantAge, request.amount)) {
            println("Application flagged as High Risk")
            return LoanResponseDTO(
                status = "HIGH_RISK_REQUIRED",
                message = "Manual approval needed"
            )
        }

        if (request.amount > 1000000) {
            return LoanResponseDTO("REJECTED", "Loan amount too high for auto-approval.")
        }
        if (request.applicantAge < 18) {
            return LoanResponseDTO("REJECTED", "Applicant too young.")
        }

        val creditScore = creditBureau.getCreditScore(request.taxId)
        println("Credit score received: $creditScore")

        var status = "PENDING"
        val message: String

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

        val savedEntity = loanRepository.save(
            LoanEntity(
                applicantName = request.applicantName,
                amount = request.amount,
                status = status
            )
        )

        if (status == "APPROVED") {
            try {
                emailService.sendConfirmation("admin@bank.com", "Loan ${savedEntity.id} approved.")
            } catch (e: Exception) {
                println("Failed to send email, but continuing anyway: ${e.message}")
            }
        }

        return LoanResponseDTO(status, message, savedEntity.id)
    }
}