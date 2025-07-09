package com.example.komiconnect.network

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import io.ktor.serialization.kotlinx.json.*

@Serializable
data class LoginRequest(val username: String, val password: String)
@Serializable
data class RegisterRequest(val username: String, val password: String, val secret: String)

const val BASE_URL = "https://komi-connect.aisjabaglioni.workers.dev"

class NetworkAPI(val client: HttpClient = HttpClient(CIO){
    install(ContentNegotiation) {
        json()
    }
}){
    suspend fun Login(username: String, password: String): Pair<String, Boolean> {
        var status: Boolean
        var message: String
        try {
            val response = client.post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }
            status = response.status.isSuccess()
            message = response.bodyAsText()
        } catch(e: Exception) {
            message = e.message ?: "Unknown error"
            status = false
        }
        return Pair(message, status)
    }

    suspend fun Register(username: String, password: String, secret: String): Pair<String, Boolean> {
        var message: String
        var status: Boolean
        try {
            val response = client.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, password, secret))
            }
            status = response.status.isSuccess()
            message = response.bodyAsText()
        } catch (e: Exception) {
            message = e.message ?: "Unknown error"
            status = false
        }
        return Pair(message, status)
    }
}



