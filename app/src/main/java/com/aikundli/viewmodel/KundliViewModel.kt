package com.aikundli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aikundli.model.*
import com.aikundli.repository.KundliRepository
import com.aikundli.repository.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── UI state wrappers ────────────────────────────────────────────────────

data class KundliUiState(
    val isLoading    : Boolean         = false,
    val kundliResult : KundliResponse? = null,
    val horoscope    : HoroscopeResult?= null,
    val error        : String?         = null
)

data class CompatibilityUiState(
    val isLoading    : Boolean                = false,
    val result       : CompatibilityResponse? = null,
    val error        : String?                = null
)

data class HoroscopeUiState(
    val isLoading  : Boolean             = false,
    val horoscopes : List<ZodiacHoroscope> = emptyList(),
    val error      : String?             = null
)

// ── ViewModel ─────────────────────────────────────────────────────────────

class KundliViewModel(
    private val repository: KundliRepository = KundliRepository()
) : ViewModel() {

    private val _kundliState = MutableStateFlow(KundliUiState())
    val kundliState: StateFlow<KundliUiState> = _kundliState.asStateFlow()

    private val _compatState = MutableStateFlow(CompatibilityUiState())
    val compatState: StateFlow<CompatibilityUiState> = _compatState.asStateFlow()

    private val _horoscopeState = MutableStateFlow(HoroscopeUiState())
    val horoscopeState: StateFlow<HoroscopeUiState> = _horoscopeState.asStateFlow()

    // Input fields (shared between form and result)
    var lastKundliRequest: KundliRequest? = null

    // ── Generate Kundli ───────────────────────────────────────────────────

    fun generateKundli(request: KundliRequest) {
        lastKundliRequest = request
        viewModelScope.launch {
            _kundliState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.generateKundli(request)) {
                is Result.Success -> {
                    _kundliState.update {
                        it.copy(isLoading = false, kundliResult = result.data)
                    }
                    // Automatically fetch AI horoscope
                    fetchHoroscope(request.name, result.data.planets, result.data.ascendant)
                }
                is Result.Error -> {
                    _kundliState.update { it.copy(isLoading = false, error = result.message) }
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun fetchHoroscope(name: String, planets: List<PlanetPosition>, ascendant: String) {
        viewModelScope.launch {
            when (val result = repository.getAiHoroscope(name, planets, ascendant)) {
                is Result.Success -> {
                    _kundliState.update { it.copy(horoscope = result.data) }
                }
                is Result.Error -> {
                    // Non-fatal; keep kundli result
                }
                Result.Loading -> Unit
            }
        }
    }

    // ── Compatibility ──────────────────────────────────────────────────────

    fun checkCompatibility(request: CompatibilityRequest) {
        viewModelScope.launch {
            _compatState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.checkCompatibility(request)) {
                is Result.Success -> _compatState.update {
                    it.copy(isLoading = false, result = result.data)
                }
                is Result.Error -> _compatState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    // ── Daily Horoscope ────────────────────────────────────────────────────

    fun loadDailyHoroscopes() {
        viewModelScope.launch {
            _horoscopeState.update { it.copy(isLoading = true) }
            when (val result = repository.getAllHoroscopes()) {
                is Result.Success -> _horoscopeState.update {
                    it.copy(isLoading = false, horoscopes = result.data)
                }
                is Result.Error -> _horoscopeState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _kundliState.update { it.copy(error = null) }
        _compatState.update { it.copy(error = null) }
    }
}
