package com.example.weatherapp.ui.alarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.services.AlarmService
import com.example.weatherapp.utils.scheduleSnooze

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alertId = intent.getIntExtra("alert_id", -1)
        val condition = intent.getStringExtra("condition") ?: "Weather Alert"

        // Request Overlay Permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val overlayIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(overlayIntent)
        }

        // Configure activity to show over lock screen and wake device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            AlarmScreen(
                condition = condition,
                onDismiss = {
                    stopService(Intent(this@AlarmActivity, AlarmService::class.java))
                    finish()
                },
                onSnooze = {
                    stopService(Intent(this@AlarmActivity, AlarmService::class.java))
                    if (alertId != -1) {
                        scheduleSnooze(this@AlarmActivity, alertId)
                    }
                    finish()
                }
            )
        }
    }
}

@Composable
fun AlarmScreen(
    condition: String,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val gradientColors = when (condition.lowercase()) {
        "rain" -> listOf(Color(0xFF1A237E), Color(0xFF0D47A1), Color(0xFF1565C0))
        "snow" -> listOf(Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF78909C))
        "wind" -> listOf(Color(0xFF004D40), Color(0xFF00695C), Color(0xFF00897B))
        "clear" -> listOf(Color(0xFFE65100), Color(0xFFFF6D00), Color(0xFFFF9100))
        "clouds" -> listOf(Color(0xFF424242), Color(0xFF616161), Color(0xFF757575))
        else -> listOf(Color(0xFF311B92), Color(0xFF4527A0), Color(0xFF5E35B1))
    }

    val weatherEmoji = when (condition.lowercase()) {
        "rain" -> "🌧️"
        "snow" -> "❄️"
        "wind" -> "💨"
        "clear" -> "☀️"
        "clouds" -> "☁️"
        "thunderstorm" -> "⛈️"
        "drizzle" -> "🌦️"
        else -> "⚠️"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather icon
            Text(
                text = weatherEmoji,
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Weather Alarm!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Condition
            Text(
                text = "$condition detected in your area",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Dismiss Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1A237E)
                )
            ) {
                Text(
                    text = "Dismiss",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Snooze Button
            OutlinedButton(
                onClick = onSnooze,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0.7f))
                    )
                )
            ) {
                Text(
                    text = "Snooze (10 min)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
