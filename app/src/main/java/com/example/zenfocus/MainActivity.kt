package com.example.zenfocus

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenfocus.ui.theme.ZenFocusTheme
import com.example.zenfocus.viewmodel.TimerUiState
import com.example.zenfocus.viewmodel.TimerViewModel
import com.example.zenfocus.viewmodel.TimerFont
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep Screen On
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        setContent {
            ZenFocusTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: TimerViewModel = viewModel()) {
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
                    fontSize = 160.sp, // Massive size
                    fontFamily = uiState.selectedFont.fontFamily,
                    modifier = Modifier.alpha(currentAlpha),
                    textAlign = TextAlign.Center
                )
            }
            
            // Helpful hint for first time users (optional, usually discrete)
            if (!uiState.isRunning && uiState.timeRemainingMillis == uiState.initialTimeMillis) {
                 Text(
                    text = "Tap to Start • Long Press to Edit",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
            
            if (uiState.showSettingsSheet) {
                SettingsSheet(
                    currentFont = uiState.selectedFont,
                    onFontSelected = { viewModel.updateFont(it) },
                    onReset = { 
                        viewModel.resetTimer()
                        viewModel.showSettings(false)
                    },
                    onDismiss = { viewModel.showSettings(false) },
                    onTimeSet = { m ->
                        viewModel.setTime(m)
                        viewModel.showSettings(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currentFont: TimerFont,
    onFontSelected: (TimerFont) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    onTimeSet: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
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
            
            Text("Quick Presets", color = MaterialTheme.colorScheme.secondary)
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

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds)
    }
}
