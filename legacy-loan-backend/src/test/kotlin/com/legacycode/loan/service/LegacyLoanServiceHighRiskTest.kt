package com.legacycode.loan.service

import com.legacycode.loan.external.ExternalCreditBureau
import com.legacycode.loan.model.LoanRequestDTO
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class LegacyLoanServiceHighRiskTest {

    @Autowired
    private lateinit var loanService: LegacyLoanService

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should return HIGH_RISK_REQUIRED when applicant age is under 25 and loan amount exceeds 50000`() {
        // Arrange: Mock the time to be within business hours (avoids the "Time Bomb")
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now() } returns LocalDateTime.of(2026, 4, 10, 10, 0)

        // Arrange: Mock the External Credit Bureau to return a controlled score
        mockkObject(ExternalCreditBureau)
        every { ExternalCreditBureau.getCreditScore(any()) } returns 750

        val request = LoanRequestDTO(
            applicantName = "Young Applicant",
            taxId = "123456",
            amount = 60000.0,
            applicantAge = 22
        )

        // Act
        val response = loanService.processApplication(request)

        // Assert
        assertEquals("HIGH_RISK_REQUIRED", response.status)
        assertEquals("Manual approval needed", response.message)
    }
}
