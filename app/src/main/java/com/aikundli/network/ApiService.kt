package com.aikundli.network

import com.aikundli.model.*
import retrofit2.Response
import retrofit2.http.*

// ── Backend (FastAPI) ─────────────────────────────────────────────────────

interface KundliApiService {

    @POST("kundli/generate")
    suspend fun generateKundli(
        @Body request: KundliRequest
    ): Response<KundliResponse>

    @POST("kundli/compatibility")
    suspend fun checkCompatibility(
        @Body request: CompatibilityRequest
    ): Response<CompatibilityResponse>

    @GET("horoscope/daily/{sign}")
    suspend fun getDailyHoroscope(
        @Path("sign") sign: String
    ): Response<ZodiacHoroscope>

    @GET("horoscope/all")
    suspend fun getAllDailyHoroscopes(): Response<List<ZodiacHoroscope>>
}

// ── Cerebras AI API ───────────────────────────────────────────────────────

interface CerebrasApiService {

    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: CerebrasRequest
    ): Response<CerebrasResponse>
}
