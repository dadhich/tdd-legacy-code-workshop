package com.legacycode.loan.service

import org.springframework.stereotype.Component

/**
 * PURE LOGIC.
 * No database, no external calls, no side effects.
 * This is easy to test.
 */
@Component
class RiskAssessor {

    fun isHighRisk(age: Int, amount: Double): Boolean {
        // Requirement: Under 25 years old AND Amount > 50,000
        return age < 25 && amount > 50_000.0
    }
}