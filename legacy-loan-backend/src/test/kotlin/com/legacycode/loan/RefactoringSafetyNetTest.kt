package com.legacycode.loan

import com.legacycode.loan.external.EmailService
import com.legacycode.loan.external.ICreditBureau
import com.legacycode.loan.model.LoanRepository
import com.legacycode.loan.model.LoanRequestDTO
import com.legacycode.loan.service.LegacyLoanService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@SpringBootTest
class RefactoringSafetyNetTest {

    @Autowired
    lateinit var realRepository: LoanRepository

    @Test
    fun `GOLDEN MASTER - verify behaviour regardless of time or random credit score`() {
        // 1. Clock
        val fixedClock = Clock.fixed(
            Instant.parse("2023-10-01T10:00:00Z"),
            ZoneId.systemDefault()
        )

        // 2. Mock Email
        val mockEmailService = mock(EmailService::class.java)

        // 3. Fake Credit Bureau (Stub)
        // Instead of mocking, we implement a simple fake that returns what we want.
        // This completely bypasses the static ExternalCreditBureau.
        val fakeCreditBureau = object : ICreditBureau {
            override fun getCreditScore(taxId: String): Int {
                return 750 // Always excellent credit
            }
        }

        // 4. Instantiate Service with all controlled dependencies
        val service = LegacyLoanService(
            realRepository,
            fixedClock,
            mockEmailService,
            fakeCreditBureau
        )

        // Arrange
        val request = LoanRequestDTO(
            applicantName = "John Doe",
            taxId = "123456",
            amount = 1000.0,
            applicantAge = 30
        )

        // Act
        val response = service.processApplication(request)

        // Assert
        assertEquals("APPROVED", response.status)
        assertEquals("Congratulations! Loan approved based on excellent credit.", response.message)
    }
}