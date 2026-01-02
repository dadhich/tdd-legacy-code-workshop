package com.legacycode.loan.model

import jakarta.persistence.*
import java.util.*

// DTO for incoming requests
data class LoanRequestDTO(
    val applicantName: String,
    val taxId: String,
    val amount: Double,
    val applicantAge: Int
)

// DTO for outgoing responses
data class LoanResponseDTO(
    val status: String,
    val message: String,
    val loanId: Long? = null
)

// Database Entity
@Entity
@Table(name = "loans")
data class LoanEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val applicantName: String,
    val amount: Double,
    val status: String,
    // Using old Java Date just to be annoying
    val processedAt: Date = Date()
)

interface LoanRepository : org.springframework.data.jpa.repository.JpaRepository<LoanEntity, Long>