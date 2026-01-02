package com.legacycode.loan.controller

import com.legacycode.loan.model.LoanRequestDTO
import com.legacycode.loan.model.LoanResponseDTO
import com.legacycode.loan.service.LegacyLoanService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = ["http://localhost:5173"])
class LoanController(private val loanService: LegacyLoanService) {

    @PostMapping("/apply")
    fun applyForLoan(@RequestBody request: LoanRequestDTO): LoanResponseDTO {
        return loanService.processApplication(request)
    }
}