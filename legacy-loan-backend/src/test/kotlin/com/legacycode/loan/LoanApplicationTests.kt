package com.legacycode.loan

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LoanApplicationTests {

    @Test
    fun contextLoads() {
        // This test ensures that the Spring Application Context can start.
        // Even in our "Legacy" code, this should pass because:
        // 1. We have the H2 database dependency.
        // 2. Spring can successfully inject the LoanRepository.
        // 3. It doesn't execute any business logic (like the time-sensitive code) yet.
    }

}