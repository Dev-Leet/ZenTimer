package com.example.zenfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenfocus.ui.theme.ClassicFont
import com.example.zenfocus.ui.theme.DigitalFont
import com.example.zenfocus.ui.theme.ModernFont
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.text.font.FontFamily

enum class TimerFont(val fontFamily: FontFamily, val label: String) {
    MODERN(ModernFont, "Modern"),
    DIGITAL(DigitalFont, "Digital"),
    CLASSIC(ClassicFont, "Classic")
}

data class TimerUiState(
    val timeRemainingMillis: Long = 25 * 60 * 1000L, // 25 Minutes Default
    val isRunning: Boolean = false,
    val selectedFont: TimerFont = TimerFont.MODERN,
    val currentTimeString: String = "",
    val showSettingsSheet: Boolean = false,
    val initialTimeMillis: Long = 25 * 60 * 1000L,
    val isDarkTheme: Boolean = true // Default to dark theme
)

class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var clockJob: Job? = null

    init {
        startClock()
    }

    private fun startClock() {
        clockJob = viewModelScope.launch {
            while (isActive) {
                val now = LocalTime.now()
                val formatter = DateTimeFormatter.ofPattern("h:mm a")
                _uiState.update { it.copy(currentTimeString = now.format(formatter).uppercase()) }
                delay(1000)
            }
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (isActive) {
                val currentRemaining = _uiState.value.timeRemainingMillis
                if (currentRemaining <= 0) {
                    _uiState.update { it.copy(isRunning = false, timeRemainingMillis = 0) }
                    break
                }
                delay(1000) // Simple 1 second delay
                _uiState.update { it.copy(timeRemainingMillis = it.timeRemainingMillis - 1000L) }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        pauseTimer()
        _uiState.update { 
            it.copy(timeRemainingMillis = it.initialTimeMillis) 
        }
    }

    fun setTime(minutes: Int) {
        val newTime = minutes * 60 * 1000L
        _uiState.update {
            it.copy(
                initialTimeMillis = newTime,
                timeRemainingMillis = newTime,
                isRunning = false
            )
        }
        timerJob?.cancel()
    }

    fun updateFont(font: TimerFont) {
        _uiState.update { it.copy(selectedFont = font) }
    }

    fun toggleTheme(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }

    fun showSettings(show: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = show) }
    }
    
    // Quick helpers for preset times if needed, or used by Edit dialog
    fun setCustomTime(hours: Int, minutes: Int, seconds: Int) {
         val totalMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
         _uiState.update {
            it.copy(
                initialTimeMillis = totalMillis,
                timeRemainingMillis = totalMillis,
                isRunning = false
            )
        }
        timerJob?.cancel()
    }
}
