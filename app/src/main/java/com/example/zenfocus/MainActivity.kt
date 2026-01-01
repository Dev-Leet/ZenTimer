@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.example.zenfocus

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenfocus.ui.theme.ZenFocusTheme
import com.example.zenfocus.viewmodel.TimerUiState
import com.example.zenfocus.viewmodel.TimerViewModel
import com.example.zenfocus.viewmodel.TimerFont
import androidx.compose.material3.LocalTextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep Screen On
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        setContent {
            val viewModel: TimerViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            // Use the dark theme state from the ViewModel
            ZenFocusTheme(darkTheme = uiState.isDarkTheme) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: TimerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Breathing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000), // 2 seconds each way = 4 seconds total cycle roughly
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    // Only breathe when running, otherwise static
    val currentAlpha = if (uiState.isRunning) alpha else 1.0f

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { viewModel.toggleTimer() },
                    onLongPress = { viewModel.showSettings(true) }
                )
            },
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // Top Left Clock
            Text(
                text = uiState.currentTimeString,
                color = MaterialTheme.colorScheme.tertiary, // Pale Blue
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                fontFamily = uiState.selectedFont.fontFamily
            )

            // Massive Timer
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = formatTime(uiState.timeRemainingMillis),
                    color = MaterialTheme.colorScheme.primary, // Soft Teal
                    fontSize = 100.sp, // Reduced size to fit HH:MM:SS
                    fontFamily = uiState.selectedFont.fontFamily,
                    modifier = Modifier.alpha(currentAlpha),
                    textAlign = TextAlign.Center
                )
            }
            
            // Helpful hint for first time users (optional, usually discrete)
            if (!uiState.isRunning && uiState.timeRemainingMillis == uiState.initialTimeMillis) {
                 Text(
                    text = "Tap to Start • Long Press to Edit",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), // Adapted for theme
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
            
            if (uiState.showSettingsSheet) {
                SettingsSheet(
                    currentFont = uiState.selectedFont,
                    isDarkTheme = uiState.isDarkTheme,
                    onFontSelected = { viewModel.updateFont(it) },
                    onThemeChanged = { viewModel.toggleTheme(it) },
                    onReset = { 
                        viewModel.resetTimer()
                        viewModel.showSettings(false)
                    },
                    onDismiss = { viewModel.showSettings(false) },
                    onTimeSet = { m ->
                        viewModel.setTime(m)
                        viewModel.showSettings(false)
                    },
                    onCustomTimeSet = { h, m, s ->
                        viewModel.setCustomTime(h, m, s)
                        viewModel.showSettings(false)
                    },
                    initialTimeMillis = uiState.initialTimeMillis
                )
            }
        }
    }
}

// Helper function to convert millis to HH, MM, SS components
private fun getHMSFromMillis(millis: Long): Triple<Int, Int, Int> {
    val totalSeconds = millis / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()
    return Triple(hours, minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currentFont: TimerFont,
    isDarkTheme: Boolean,
    onFontSelected: (TimerFont) -> Unit,
    onThemeChanged: (Boolean) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    onTimeSet: (Int) -> Unit,
    onCustomTimeSet: (Int, Int, Int) -> Unit,
    initialTimeMillis: Long
) {
    val sheetState = rememberModalBottomSheetState()
    
    // Calculate initial values for digit selectors
    val (initialH, initialM, initialS) = remember(initialTimeMillis) { 
        getHMSFromMillis(initialTimeMillis)
    }

    // States for individual digits (H1 H2 : M1 M2 : S1 S2)
    var h1 by remember { mutableStateOf(initialH / 10) }
    var h2 by remember { mutableStateOf(initialH % 10) }
    var m1 by remember { mutableStateOf(initialM / 10) }
    var m2 by remember { mutableStateOf(initialM % 10) }
    var s1 by remember { mutableStateOf(initialS / 10) }
    var s2 by remember { mutableStateOf(initialS % 10) }

    // Define custom colors for the input selector boxes
    val selectorColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.primary
    )
    
    // Helper to run custom set and dismiss
    val onSetCustomTime = {
        val hours = h1 * 10 + h2
        val minutes = m1 * 10 + m2
        val seconds = s1 * 10 + s2
        if (hours > 0 || minutes > 0 || seconds > 0) {
            onCustomTimeSet(hours, minutes, seconds)
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings", 
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Theme Switcher
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text("Dark Theme", color = MaterialTheme.colorScheme.onSurface)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Select Font", color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row {
                TimerFont.values().forEach { font ->
                    Button(
                        onClick = { onFontSelected(font) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (font == currentFont) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (font == currentFont) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(font.label, fontFamily = font.fontFamily)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Set Timer", color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                val presets = listOf(10, 25, 45, 60)
                presets.forEach { mins ->
                     Button(
                        onClick = { onTimeSet(mins) },
                        colors = ButtonDefaults.buttonColors(
                             containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("${mins}m", color = MaterialTheme.colorScheme.onTertiary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // NEW Custom Time Implementation
            Text("Custom Time", color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hours
                DigitSelector(selectedDigit = h1, onDigitSelected = { h1 = it }, colors = selectorColors)
                DigitSelector(selectedDigit = h2, onDigitSelected = { h2 = it }, colors = selectorColors)
                TimeSeparator()
                // Minutes
                DigitSelector(selectedDigit = m1, onDigitSelected = { m1 = it }, colors = selectorColors, maxDigit = 5) // M1 can only be 0-5
                DigitSelector(selectedDigit = m2, onDigitSelected = { m2 = it }, colors = selectorColors)
                TimeSeparator()
                // Seconds
                DigitSelector(selectedDigit = s1, onDigitSelected = { s1 = it }, colors = selectorColors, maxDigit = 5) // S1 can only be 0-5
                DigitSelector(selectedDigit = s2, onDigitSelected = { s2 = it }, colors = selectorColors)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSetCustomTime,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Set Custom Time", color = MaterialTheme.colorScheme.onPrimary)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reset Timer", color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TimeSeparator() {
    Text(
        text = ":", 
        style = MaterialTheme.typography.titleLarge, 
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DigitSelector(
    selectedDigit: Int,
    onDigitSelected: (Int) -> Unit,
    colors: androidx.compose.material3.TextFieldColors,
    maxDigit: Int = 9
) {
    var expanded by remember { mutableStateOf(false) }
    val digits = remember(maxDigit) { (0..maxDigit).map { it.toString() } }
    val selectedText = selectedDigit.toString()

    Box(
        modifier = Modifier
            .width(48.dp)
            .padding(horizontal = 2.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {}, // Not editable by typing
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = colors,
                modifier = Modifier.menuAnchor(),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                // Limit the width of the dropdown itself
                modifier = Modifier.width(48.dp) 
            ) {
                digits.forEach { digitString ->
                    DropdownMenuItem(
                        text = { Text(digitString, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                        onClick = {
                            onDigitSelected(digitString.toInt())
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    // Always show HH:MM:SS
    return String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds)
}
