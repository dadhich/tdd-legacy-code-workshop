package com.legacycode.loan.external

import org.springframework.stereotype.Component
import kotlin.random.Random

object ExternalCreditBureau {
    fun getCreditScore(taxId: String): Int {
        println("Connecting to external Credit Bureau API...")
        Thread.sleep(500) // Latency simulation

        if (taxId.startsWith("999")) {
            throw RuntimeException("Credit Bureau API Connection Timeout!")
        }

        return Random.nextInt(300, 851)
    }
}

// CHANGED: Added @Component so Spring can autowire it into our Service
@Component
class EmailService() {
    // In a real legacy app, this might need configuration properties.
    // We'll hardcode the server inside for simplicity or default it.
    private val smtpServer = "smtp.corporate.com"

    fun sendConfirmation(to: String, message: String) {
        if (smtpServer.isEmpty()) throw IllegalArgumentException("SMTP Server not configured")
        println(">>> SENDING EMAIL TO: $to VIA $smtpServer")
        println(">>> BODY: $message")
        Thread.sleep(200)
    }
}