package com.fluxtah.ask.api.clients

import com.fluxtah.ask.api.clients.openai.assistants.addHttpLog
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

val httpClient = HttpClient(OkHttp) {
    engine {
        clientCacheSize = 0
        config {
            retryOnConnectionFailure(true)
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }
    }
    install(HttpRequestRetry) {
        retryIf(5) { _, response ->
            response.status.value.let { it == 429 || it in 500..599 }
        }
        exponentialDelay()
    }
    install(ContentNegotiation) {
        json(json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        })
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                addHttpLog(message)
            }
        }
        level = LogLevel.ALL
    }
}