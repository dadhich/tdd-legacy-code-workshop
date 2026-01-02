package com.legacycode.loan

import com.legacycode.loan.model.LoanRepository
import com.legacycode.loan.model.LoanRequestDTO
import com.legacycode.loan.service.LegacyLoanService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@SpringBootTest
class RefactoringSafetyNetTest {

    // 1. Inject the REAL repository into the test context
    @Autowired
    lateinit var realRepository: LoanRepository

    // The Golden Master: We capture the EXACT current behaviour
    @Test
    fun `GOLDEN MASTER - existing behaviour for standard applicant`() {
        // Arrange
        // Inject a fixed time (10 AM)
        val fixedClock = Clock.fixed(
            Instant.parse("2026-01-01T10:00:00Z"),
            ZoneId.of("UTC")
        )

        val service = LegacyLoanService(realRepository, fixedClock)

        val request = LoanRequestDTO(
            applicantName = "John Doe",
            taxId = "123456", // Safe ID
            amount = 1000.0,
            applicantAge = 30
        )

        // Act
        val response = service.processApplication(request)

        // Assert
        assertEquals("APPROVED", response.status)
    }
}