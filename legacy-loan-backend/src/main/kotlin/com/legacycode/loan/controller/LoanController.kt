package com.legacycode.loan.controller

import com.legacycode.loan.model.LoanRequestDTO
import com.legacycode.loan.model.LoanResponseDTO
import com.legacycode.loan.service.LegacyLoanService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = ["http://localhost:5173"]) // Allow local React app
class LoanController(private val loanService: LegacyLoanService) {

    @PostMapping("/apply")
    fun applyForLoan(@RequestBody request: LoanRequestDTO): LoanResponseDTO {
        // No try-catch here, let exceptions bubble up to the user!
        return loanService.processApplication(request)
    }
}