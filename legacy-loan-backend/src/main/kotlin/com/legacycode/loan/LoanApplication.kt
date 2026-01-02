package com.legacycode.loan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoanApplication

fun main(args: Array<String>) {
    runApplication<LoanApplication>(*args)
}