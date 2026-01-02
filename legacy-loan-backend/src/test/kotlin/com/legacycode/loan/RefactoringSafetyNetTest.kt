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

    // Inject the REAL H2 repository from Spring Context
    @Autowired
    lateinit var realRepository: LoanRepository

    @Test
    fun `GOLDEN MASTER - verify behaviour regardless of time`() {
        // 1. Create a Fixed Clock (10:00 AM) to ensure test passes even at night
        val fixedClock = Clock.fixed(
            Instant.parse("2023-10-01T10:00:00Z"),
            ZoneId.systemDefault()
        )

        // 2. Instantiate the service manually using the REAL repository + FAKE clock
        //    (This works because we fixed the Constructor Injection in this branch)
        val service = LegacyLoanService(realRepository, fixedClock)

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