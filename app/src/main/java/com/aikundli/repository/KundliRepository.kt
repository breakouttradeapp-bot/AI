package com.aikundli.repository

import com.aikundli.BuildConfig
import com.aikundli.model.*
import com.aikundli.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class KundliRepository {

    private val kundliApi   = RetrofitClient.kundliApi
    private val cerebrasApi = RetrofitClient.cerebrasApi
    private val gson        = Gson()

    // ── Generate Kundli ───────────────────────────────────────────────────

    suspend fun generateKundli(request: KundliRequest): Result<KundliResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = kundliApi.generateKundli(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error("Failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "Network error")
            }
        }

    // ── AI Horoscope Interpretation ───────────────────────────────────────

    suspend fun getAiHoroscope(
        name: String,
        planets: List<PlanetPosition>,
        ascendant: String
    ): Result<HoroscopeResult> = withContext(Dispatchers.IO) {
        try {
            val planetSummary = planets.joinToString(", ") {
                "${it.planet} in ${it.sign} (House ${it.house})"
            }
            val systemPrompt = """
                You are an expert Vedic astrologer. Analyze the given birth chart and provide 
                a detailed, personalized horoscope reading. Be positive, insightful, and specific.
                Return your response as a JSON object with exactly these keys:
                personality, career, marriage, finance, health, luckyNumbers, luckyColors.
                Each value should be 2-3 sentences. No markdown, pure JSON only.
            """.trimIndent()

            val userPrompt = """
                Name: $name
                Ascendant: $ascendant
                Planetary Positions: $planetSummary
                
                Generate a complete Vedic horoscope reading.
            """.trimIndent()

            val request = CerebrasRequest(
                model    = "gpt-oss-120b",
                messages = listOf(
                    ChatMessage("system", systemPrompt),
                    ChatMessage("user", userPrompt)
                )
            )

            val response = cerebrasApi.chatCompletion(
                authorization = "Bearer ${BuildConfig.CEREBRAS_API_KEY}",
                request       = request
            )

            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                    ?: return@withContext Result.Error("Empty AI response")
                // Strip markdown fences if present
                val json = content.replace("```json", "").replace("```", "").trim()
                val result = gson.fromJson(json, HoroscopeResult::class.java)
                Result.Success(result)
            } else {
                Result.Error("AI API error: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "AI error")
        }
    }

    // ── Compatibility ─────────────────────────────────────────────────────

    suspend fun checkCompatibility(request: CompatibilityRequest): Result<CompatibilityResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = kundliApi.checkCompatibility(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "Network error")
            }
        }

    // ── Daily Horoscope ───────────────────────────────────────────────────

    suspend fun getAllHoroscopes(): Result<List<ZodiacHoroscope>> =
        withContext(Dispatchers.IO) {
            try {
                val response = kundliApi.getAllDailyHoroscopes()
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "Network error")
            }
        }
}
