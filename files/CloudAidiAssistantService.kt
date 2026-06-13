package com.malha.app.core.ai

import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Aidi assistant service backed by the Anthropic Messages API.
 *
 * The API key must be stored in local.properties as `anthropic.api.key`
 * and injected at build time via BuildConfig (see app/build.gradle.kts).
 *
 * Falls back gracefully to [LocalAidiAssistantService] if no key is present
 * or if the network request fails, so the app always remains usable offline.
 */
class CloudAidiAssistantService(
    private val apiKey: String,
    private val fallback: AidiAssistantService = LocalAidiAssistantService()
) : AidiAssistantService {

    companion object {
        private const val API_URL = "https://api.anthropic.com/v1/messages"
        private const val MODEL = "claude-sonnet-4-6"
        private const val MAX_TOKENS = 512

        private val SYSTEM_PROMPT = """
            You are Aidi, a calm and knowledgeable knitting and crochet assistant inside the Malha app.
            
            Your role:
            - Help users plan projects, understand pattern steps, choose materials, and track progress.
            - Explain knitting and crochet techniques clearly and accessibly.
            - Give honest, cautious advice — always remind users to verify stitch counts, gauge, and sizing themselves.
            - Keep responses concise (2–4 sentences unless more detail is genuinely needed).
            - Never make up pattern instructions or present AI output as verified truth.
            - Be warm, calm, and non-judgmental. Crafting is personal and relaxing — match that energy.
            
            Language: respond in the same language the user writes in. Default to English if unclear.
        """.trimIndent()
    }

    override suspend fun generateReply(prompt: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext fallback.generateReply(prompt)

        runCatching {
            val requestBody = JSONObject().apply {
                put("model", MODEL)
                put("max_tokens", MAX_TOKENS)
                put("system", SYSTEM_PROMPT)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }.toString()

            val url = java.net.URL(API_URL)
            val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("x-api-key", apiKey)
                setRequestProperty("anthropic-version", "2023-06-01")
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 30_000
            }

            connection.outputStream.use { it.write(requestBody.toByteArray()) }

            val responseCode = connection.responseCode
            val responseText = if (responseCode == 200) {
                connection.inputStream.bufferedReader().readText()
            } else {
                connection.errorStream?.bufferedReader()?.readText() ?: ""
            }
            connection.disconnect()

            if (responseCode != 200) {
                return@runCatching fallback.generateReply(prompt)
            }

            val json = JSONObject(responseText)
            val content = json.getJSONArray("content")
            if (content.length() == 0) return@runCatching fallback.generateReply(prompt)

            content.getJSONObject(0).getString("text").trim()
        }.getOrElse {
            // Network unavailable or parse error — silent fallback keeps app functional offline
            fallback.generateReply(prompt)
        }
    }

    override suspend fun parsePattern(text: String): Pattern {
        // Pattern parsing is compute-intensive; delegate to local parser for now.
        // A future version could send the full text to the API for structured extraction.
        return fallback.parsePattern(text)
    }
}
