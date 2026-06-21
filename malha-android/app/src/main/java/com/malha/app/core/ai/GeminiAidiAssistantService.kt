package com.malha.app.core.ai

import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Aidi assistant service backed by the Google Gemini API.
 *
 * The API key must be stored in local.properties as `GEMINI_API_KEY`
 * and injected at build time via BuildConfig (see app/build.gradle.kts).
 *
 * Falls back gracefully to [LocalAidiAssistantService] if no key is present
 * or if the network request fails, so the app always remains usable offline.
 */
class GeminiAidiAssistantService(
    private val apiKey: String,
    private val temperature: Float = 0.4f,
    private val maxTokens: Int = 800,
    private val fallback: AidiAssistantService = LocalAidiAssistantService()
) : AidiAssistantService {

    companion object {
        private const val MODEL = "gemini-3.5-flash"
        private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

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
        if (apiKey.isBlank()) {
            android.util.Log.w("GeminiService", "Gemini API key is blank! Falling back to offline assistant.")
            return@withContext fallback.generateReply(prompt)
        }

        runCatching {
            // Build Gemini request body
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", SYSTEM_PROMPT)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", temperature)
                    put("maxOutputTokens", maxTokens)
                })
            }.toString()

            val url = java.net.URL("$API_URL?key=$apiKey")
            val connection = (url.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
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
                android.util.Log.e("GeminiService", "HTTP error $responseCode: $responseText")
                return@runCatching fallback.generateReply(prompt)
            }

            // Parse Gemini response
            val json = JSONObject(responseText)
            val candidates = json.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                android.util.Log.e("GeminiService", "No candidates found in response: $responseText")
                return@runCatching fallback.generateReply(prompt)
            }
            val content = candidates.getJSONObject(0).optJSONObject("content")
            if (content == null) {
                android.util.Log.e("GeminiService", "No content found in first candidate: $responseText")
                return@runCatching fallback.generateReply(prompt)
            }
            val parts = content.optJSONArray("parts")
            if (parts == null || parts.length() == 0) {
                android.util.Log.e("GeminiService", "No parts found in candidate content: $responseText")
                return@runCatching fallback.generateReply(prompt)
            }
            parts.getJSONObject(0).getString("text").trim()
        }.getOrElse { throwable ->
            android.util.Log.e("GeminiService", "Exception in generateReply", throwable)
            fallback.generateReply(prompt)
        }
    }

    override suspend fun parsePattern(text: String): Pattern {
        return fallback.parsePattern(text)
    }
}
