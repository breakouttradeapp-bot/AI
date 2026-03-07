package com.aikundli.model

import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey

// ── Kundli Request / Response ─────────────────────────────────────────────

data class KundliRequest(
    val name        : String,
    val gender      : String,
    @SerializedName("date_of_birth") val dateOfBirth : String,   // "YYYY-MM-DD"
    @SerializedName("time_of_birth") val timeOfBirth : String,   // "HH:MM"
    val latitude    : Double,
    val longitude   : Double,
    val timezone    : String                                       // "Asia/Kolkata"
)

data class KundliResponse(
    val success          : Boolean,
    val chartImageBase64 : String?,
    val planets          : List<PlanetPosition>,
    val houses           : List<HouseInfo>,
    val ascendant        : String,
    val moonSign         : String,
    val sunSign          : String
)

data class PlanetPosition(
    val planet  : String,
    val sign    : String,
    val degree  : Double,
    val house   : Int,
    val isRetro : Boolean
)

data class HouseInfo(
    val house : Int,
    val sign  : String,
    val degree: Double
)

// ── AI Horoscope ──────────────────────────────────────────────────────────

data class CerebrasRequest(
    val model      : String = "gpt-oss-120b",
    @SerializedName("max_tokens") val maxTokens: Int = 2048,
    val messages   : List<ChatMessage>
)

data class ChatMessage(
    val role    : String,   // "system" | "user" | "assistant"
    val content : String
)

data class CerebrasResponse(
    val id      : String,
    val choices : List<Choice>
)

data class Choice(val message: ChatMessage)

data class HoroscopeResult(
    val personality  : String = "",
    val career       : String = "",
    val marriage     : String = "",
    val finance      : String = "",
    val health       : String = "",
    val luckyNumbers : String = "",
    val luckyColors  : String = ""
)

// ── Compatibility ─────────────────────────────────────────────────────────

data class CompatibilityRequest(
    val person1: KundliRequest,
    val person2: KundliRequest
)

data class CompatibilityResponse(
    val gunaMilanScore      : Int,
    val compatibilityPercent: Int,
    val isManglik1          : Boolean,
    val isManglik2          : Boolean,
    val advice              : String,
    val details             : List<GunaMilanDetail>
)

data class GunaMilanDetail(
    val name    : String,
    val maxScore: Int,
    val score   : Int
)

// ── Saved Report (Room entity) ────────────────────────────────────────────

@Entity(tableName = "saved_reports")
data class SavedReport(
    @PrimaryKey(autoGenerate = true) val id          : Int    = 0,
    val name                                          : String,
    val dateOfBirth                                   : String,
    val ascendant                                     : String,
    val moonSign                                      : String,
    val planetsJson                                   : String, // JSON string
    val horoscopeJson                                 : String, // JSON string
    val pdfPath                                       : String?,
    val createdAt                                     : Long = System.currentTimeMillis()
)

// ── Daily Horoscope ───────────────────────────────────────────────────────

data class ZodiacHoroscope(
    val sign        : String,
    val symbol      : String,
    val text        : String,
    val luckyNumber : Int,
    val luckyColor  : String
)
