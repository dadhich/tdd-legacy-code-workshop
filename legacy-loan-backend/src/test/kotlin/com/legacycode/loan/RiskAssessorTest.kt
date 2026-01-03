package com.legacycode.loan

import com.legacycode.loan.service.RiskAssessor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RiskAssessorTest {

    private val assessor = RiskAssessor()

    @Test
    fun `should flag as high risk if applicant is under 25 and amount is over 50k`() {
        val result = assessor.isHighRisk(age = 24, amount = 50_001.0)
        assertTrue(result, "Should be high risk")
    }

    @Test
    fun `should NOT flag if applicant is 25 or older`() {
        val result = assessor.isHighRisk(age = 25, amount = 50_001.0)
        assertFalse(result, "Age 25 is not high risk")
    }

    @Test
    fun `should NOT flag if amount is 50k or less`() {
        val result = assessor.isHighRisk(age = 20, amount = 50_000.0)
        assertFalse(result, "Amount 50k is not high risk")
    }

    @Test
    fun `should NOT flag if both conditions are safe`() {
        val result = assessor.isHighRisk(age = 30, amount = 10_000.0)
        assertFalse(result, "Safe applicant should not be flagged")
    }
}