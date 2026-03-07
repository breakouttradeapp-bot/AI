package com.aikundli.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aikundli.model.*
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.navigation.Screen
import com.aikundli.ui.theme.*
import com.aikundli.viewmodel.KundliViewModel

@Composable
fun MatchKundliScreen(
    navController: NavController,
    viewModel: KundliViewModel = viewModel()
) {
    val state by viewModel.compatState.collectAsState()

    // Person 1
    var name1 by remember { mutableStateOf("") }
    var dob1  by remember { mutableStateOf("") }
    var tob1  by remember { mutableStateOf("") }
    var lat1  by remember { mutableStateOf("") }
    var lng1  by remember { mutableStateOf("") }

    // Person 2
    var name2 by remember { mutableStateOf("") }
    var dob2  by remember { mutableStateOf("") }
    var tob2  by remember { mutableStateOf("") }
    var lat2  by remember { mutableStateOf("") }
    var lng2  by remember { mutableStateOf("") }

    LaunchedEffect(state.result) {
        if (state.result != null) navController.navigate(Screen.MatchResult.route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
            }
            Text("Match Kundli", style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar, fontWeight = FontWeight.Bold)
        }

        // Person 1 card
        PersonCard(
            title = "👤 Person 1 (Groom / Partner A)",
            name = name1, onNameChange = { name1 = it },
            dob  = dob1,  onDobChange  = { dob1  = it },
            tob  = tob1,  onTobChange  = { tob1  = it },
            lat  = lat1,  onLatChange  = { lat1  = it },
            lng  = lng1,  onLngChange  = { lng1  = it }
        )

        // Hearts divider
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("💕", style = MaterialTheme.typography.displayLarge)
        }

        // Person 2 card
        PersonCard(
            title = "👤 Person 2 (Bride / Partner B)",
            name = name2, onNameChange = { name2 = it },
            dob  = dob2,  onDobChange  = { dob2  = it },
            tob  = tob2,  onTobChange  = { tob2  = it },
            lat  = lat2,  onLatChange  = { lat2  = it },
            lng  = lng2,  onLngChange  = { lng2  = it }
        )

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = {
                viewModel.checkCompatibility(
                    CompatibilityRequest(
                        person1 = KundliRequest(name1, "Male", dob1, tob1,
                            lat1.toDoubleOrNull() ?: 0.0, lng1.toDoubleOrNull() ?: 0.0, "Asia/Kolkata"),
                        person2 = KundliRequest(name2, "Female", dob2, tob2,
                            lat2.toDoubleOrNull() ?: 0.0, lng2.toDoubleOrNull() ?: 0.0, "Asia/Kolkata")
                    )
                )
            },
            modifier = Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 16.dp),
            shape    = RoundedCornerShape(16.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = NebulaPink),
            enabled  = !state.isLoading
        ) {
            if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("💕 Check Compatibility", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun PersonCard(
    title: String,
    name: String, onNameChange: (String) -> Unit,
    dob: String,  onDobChange: (String) -> Unit,
    tob: String,  onTobChange: (String) -> Unit,
    lat: String,  onLatChange: (String) -> Unit,
    lng: String,  onLngChange: (String) -> Unit,
) {
    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = GoldenStar)
            KundliTextField(name, onNameChange, "Full Name", "👤")
            KundliTextField(dob, onDobChange, "Date of Birth (YYYY-MM-DD)", "📅")
            KundliTextField(tob, onTobChange, "Time of Birth (HH:MM)", "⏰")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KundliTextField(lat, onLatChange, "Latitude", "🌐", Modifier.weight(1f))
                KundliTextField(lng, onLngChange, "Longitude", "🌐", Modifier.weight(1f))
            }
        }
    }
}

// ── Match Result ──────────────────────────────────────────────────────────

@Composable
fun MatchResultScreen(
    navController: NavController,
    viewModel: KundliViewModel = viewModel()
) {
    val state  = viewModel.compatState.collectAsState().value
    val result = state.result ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp).padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
            }
            Text("Compatibility Report", style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar, fontWeight = FontWeight.Bold)
        }

        // Score circle
        GlassCard(Modifier.padding(16.dp)) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Compatibility Score", color = StarlightWhite.copy(0.7f),
                    style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                AnimatedScore(score = result.compatibilityPercent)
                Spacer(Modifier.height(16.dp))
                Text(
                    getCompatibilityLabel(result.compatibilityPercent),
                    color = GoldenStar, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center
                )
            }
        }

        // Guna Milan score
        GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Guna Milan: ${result.gunaMilanScore}/36",
                    style = MaterialTheme.typography.titleLarge, color = GoldenStar)
                Spacer(Modifier.height(12.dp))
                result.details.forEach { detail ->
                    GunaMilanRow(detail)
                }
            }
        }

        // Manglik status
        GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("🔴 Manglik Dosha", style = MaterialTheme.typography.titleLarge, color = GoldenStar)
                Spacer(Modifier.height(8.dp))
                Text("Person 1: ${if (result.isManglik1) "Manglik ⚠️" else "Non-Manglik ✅"}",
                    color = StarlightWhite)
                Text("Person 2: ${if (result.isManglik2) "Manglik ⚠️" else "Non-Manglik ✅"}",
                    color = StarlightWhite, modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Advice
        GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("💡 Marriage Advice", style = MaterialTheme.typography.titleLarge, color = GoldenStar)
                Spacer(Modifier.height(8.dp))
                Text(result.advice, color = StarlightWhite.copy(0.85f),
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun AnimatedScore(score: Int) {
    val animatedScore by animateIntAsState(
        targetValue    = score,
        animationSpec  = tween(1500, easing = FastOutSlowInEasing),
        label          = "score"
    )
    val color = when {
        score >= 75 -> Color(0xFF4CAF50)
        score >= 50 -> GoldenStar
        else        -> Color(0xFFFF5722)
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
        CircularProgressIndicator(
            progress = animatedScore / 100f,
            modifier = Modifier.fillMaxSize(),
            color    = color,
            strokeWidth = 10.dp,
            trackColor  = GlassBorder
        )
        Text("$animatedScore%", style = MaterialTheme.typography.displayLarge,
            color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GunaMilanRow(detail: GunaMilanDetail) {
    val animated by animateFloatAsState(
        targetValue   = detail.score.toFloat() / detail.maxScore.toFloat(),
        animationSpec = tween(800), label = "bar"
    )
    Column(Modifier.padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(detail.name, color = StarlightWhite, style = MaterialTheme.typography.bodyMedium)
            Text("${detail.score}/${detail.maxScore}", color = GoldenStar,
                fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress  = animated,
            modifier  = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color     = MysticViolet,
            trackColor = GlassBorder
        )
    }
}

fun getCompatibilityLabel(score: Int) = when {
    score >= 90 -> "Exceptional Match 💕"
    score >= 75 -> "Very Good Match ❤️"
    score >= 60 -> "Good Match 💛"
    score >= 40 -> "Average Match 💙"
    else        -> "Needs Attention ⚠️"
}
