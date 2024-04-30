package com.fluxtah.ask.api.clients.openai.assistants

import com.fluxtah.ask.api.clients.openai.assistants.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit


const val HTTP_LOG_SIZE = 10

val HTTP_LOG = mutableListOf<String>()

fun addHttpLog(message: String) {
    HTTP_LOG.add(message)
    if (HTTP_LOG.size > HTTP_LOG_SIZE) {
        HTTP_LOG.removeAt(0)
    }
}

fun HttpRequestBuilder.openAiAssistantsBetaHeader() {
    header("OpenAI-Beta", "assistants=v1")
}

class AssistantsApi(
    private val client: HttpClient = httpClient,
    private val baseUri: String = "https://api.openai.com",
    private val version: String = "v1",
    private val apiKeyProvider: () -> String
) {
    val threads by lazy { ThreadsApiClient(client, baseUri, version, apiKeyProvider) }
    val runs by lazy { RunsApiClient(client, baseUri, version, apiKeyProvider) }
    val messages by lazy { MessagesApiClient(client, baseUri, version, apiKeyProvider) }
    val assistants by lazy { AssistantsApiClient(client, baseUri, version, apiKeyProvider) }
}

class AssistantsApiClient(
    private val client: HttpClient,
    private val baseUri: String = "https://api.openai.com",
    private val version: String = "v1",
    private val apiKeyProvider: () -> String
) {
    suspend fun createAssistant(request: CreateAssistantRequest): Assistant {
        val response = client.post("$baseUri/$version/assistants") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            setBody(request)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<Assistant>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun getAssistant(assistantId: String): Assistant {
        val response = client.get("$baseUri/$version/assistants/$assistantId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<Assistant>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }
}

class RunsApiClient(
    private val client: HttpClient,
    private val baseUri: String = "https://api.openai.com",
    private val version: String = "v1",
    private val apiKeyProvider: () -> String
) {
    suspend fun createRun(threadId: String, request: RunRequest): AssistantRun {
        val response = client.post("$baseUri/$version/threads/$threadId/runs") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
            setBody(request)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantRun>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun getRun(threadId: String, runId: String): AssistantRun {
        val response = client.get("$baseUri/$version/threads/$threadId/runs/$runId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantRun>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun listRuns(threadId: String): AssistantRunList {
        val response = client.get("$baseUri/$version/threads/$threadId/runs") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantRunList>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun listRunSteps(threadId: String, runId: String): AssistantRunStepList {
        val response = client.get("$baseUri/$version/threads/$threadId/runs/$runId/steps") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantRunStepList>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun submitToolOutputs(threadId: String, runId: String, request: SubmitToolOutputsRequest): AssistantRun {
        val response = client.post("$baseUri/$version/threads/$threadId/runs/$runId/submit_tool_outputs") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
            setBody(request)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantRun>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }
}

class ThreadsApiClient(
    private val client: HttpClient,
    private val baseUri: String = "https://api.openai.com",
    private val version: String = "v1",
    private val apiKeyProvider: () -> String,
) {
    suspend fun createThread(): AssistantThread {
        val response = client.post("$baseUri/$version/threads") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantThread>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun getThread(threadId: String): AssistantThread {
        val response = client.get("$baseUri/$version/threads/$threadId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantThread>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun listThreads(): List<AssistantThread> {
        val response = client.get("$baseUri/$version/threads") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<List<AssistantThread>>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun deleteThread(threadId: String): AssistantThreadDeletionStatus {
        val response = client.delete("$baseUri/$version/threads/$threadId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantThreadDeletionStatus>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }
}

class MessagesApiClient(
    private val client: HttpClient,
    private val baseUri: String = "https://api.openai.com",
    private val version: String = "v1",
    private val apiKeyProvider: () -> String
) {
    suspend fun createUserMessage(threadId: String, content: String): Message {
        val response = client.post("$baseUri/$version/threads/$threadId/messages") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
            setBody(mapOf("role" to "user", "content" to content))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<Message>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun getMessage(threadId: String, messageId: String): Message {
        val response = client.get("$baseUri/$version/threads/$threadId/messages/$messageId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<Message>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun modifyMessage(threadId: String, messageId: String, metadata: Map<String, String>): Message {
        val response = client.post("$baseUri/$version/threads/$threadId/messages/$messageId") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
            setBody(mapOf("metadata" to metadata))
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<Message>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }

    suspend fun listMessages(
        threadId: String,
        afterId: String? = null,
        beforeId: String? = null
    ): AssistantMessageList {
        val response = client.get("$baseUri/$version/threads/$threadId/messages") {
            if (afterId != null) url.parameters.append("after_id", afterId)
            if (beforeId != null) url.parameters.append("before_id", beforeId)
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${apiKeyProvider.invoke()}")
            openAiAssistantsBetaHeader()
        }


        when (response.status) {
            HttpStatusCode.OK -> {
                return response.body<AssistantMessageList>()
            }

            else -> throw IllegalStateException(response.bodyAsText())
        }
    }
}

private val httpClient = HttpClient(OkHttp) {
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
