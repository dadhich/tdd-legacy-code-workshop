package com.legacycode.loan.external

import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.random.Random

/**
 * BLOCKER 1: A static utility class that makes real network calls.
 * This is hard to mock in standard unit tests.
 */
object ExternalCreditBureau {
    fun getCreditScore(taxId: String): Int {
        println("Connecting to external Credit Bureau API...")
        // Simulate network latency
        Thread.sleep(500)

        if (taxId.startsWith("999")) {
            throw RuntimeException("Credit Bureau API Connection Timeout!")
        }

        // In reality, this would be an HTTP call.
        // We simulate a score between 300 and 850.
        return Random.nextInt(300, 851)
    }
}

/**
 * BLOCKER 2: A service that isn't a Spring bean, often instantiated manually.
 */
class EmailService(private val smtpServer: String) {
    fun sendConfirmation(to: String, message: String) {
        if (smtpServer.isEmpty()) throw IllegalArgumentException("SMTP Server not configured")
        println(">>> SENDING EMAIL TO: $to VIA $smtpServer")
        println(">>> BODY: $message")
        // Simulate email sending delay
        Thread.sleep(200)
    }
}