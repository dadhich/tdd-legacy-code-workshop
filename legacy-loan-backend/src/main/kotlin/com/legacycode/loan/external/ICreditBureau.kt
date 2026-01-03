package com.legacycode.loan.external

import org.springframework.stereotype.Component

// 1. The Interface (The Seam)
interface ICreditBureau {
    fun getCreditScore(taxId: String): Int
}

// 2. The Implementation (The Wrapper)
// This implementation is what Spring uses in Production.
// It simply delegates to the ugly static legacy code.
@Component
class DefaultCreditBureau : ICreditBureau {
    override fun getCreditScore(taxId: String): Int {
        return ExternalCreditBureau.getCreditScore(taxId)
    }
}