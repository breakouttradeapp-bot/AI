@file:OptIn(ExperimentalMaterial3Api::class)

package com.aikundli.ui.screens
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aikundli.model.ZodiacHoroscope
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.theme.*
import com.aikundli.viewmodel.KundliViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyHoroscopeScreen(
    navController: NavController,
    viewModel: KundliViewModel = viewModel()
) {
    val state by viewModel.horoscopeState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadDailyHoroscopes() }

    var selected by remember { mutableStateOf<ZodiacHoroscope?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp).padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
            }
            Text("Daily Horoscope", style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar, fontWeight = FontWeight.Bold)
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MysticViolet)
            }
            return@Column
        }

        AnimatedVisibility(visible = selected != null) {
            selected?.let { sign ->
                GlassCard(Modifier.padding(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sign.symbol, fontSize = 36.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(sign.sign, style = MaterialTheme.typography.titleLarge,
                                    color = GoldenStar, fontWeight = FontWeight.Bold)
                                Text("Today's Reading", color = StarlightWhite.copy(0.6f),
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(sign.text, color = StarlightWhite.copy(0.85f),
                            style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("🔢 Lucky: ${sign.luckyNumber}",
                                color = GoldenStar.copy(0.9f),
                                style = MaterialTheme.typography.bodySmall)
                            Text("🎨 Color: ${sign.luckyColor}",
                                color = GoldenStar.copy(0.9f),
                                style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { selected = null }) {
                            Text("Close", color = MysticViolet)
                        }
                    }
                }
            }
        }

        val horoscopes = if (state.horoscopes.isEmpty()) defaultZodiacList() else state.horoscopes

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(horoscopes) { index, sign ->
                ZodiacCard(sign = sign, delay = index * 50, onClick = { selected = sign })
            }
        }
    }
}

@Composable
fun ZodiacCard(sign: ZodiacHoroscope, delay: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.aspectRatio(0.85f).scale(scale).alpha(alpha),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(sign.symbol, fontSize = 28.sp)
                Spacer(Modifier.height(6.dp))
                Text(sign.sign, color = StarlightWhite, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                Text("🔢 ${sign.luckyNumber}", color = GoldenStar.copy(0.8f),
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

fun defaultZodiacList(): List<ZodiacHoroscope> = listOf(
    ZodiacHoroscope("Aries","♈","Today brings fiery energy and bold opportunities.",9,"Red"),
    ZodiacHoroscope("Taurus","♉","Stability and comfort guide your decisions.",6,"Green"),
    ZodiacHoroscope("Gemini","♊","Communication flows brilliantly today.",5,"Yellow"),
    ZodiacHoroscope("Cancer","♋","Emotional depth brings meaningful connections.",2,"Silver"),
    ZodiacHoroscope("Leo","♌","Your natural charisma shines brightest today.",1,"Gold"),
    ZodiacHoroscope("Virgo","♍","Attention to detail leads to great rewards.",4,"Navy"),
    ZodiacHoroscope("Libra","♎","Balance and beauty are your superpowers today.",7,"Pink"),
    ZodiacHoroscope("Scorpio","♏","Deep transformation brings powerful insights.",8,"Crimson"),
    ZodiacHoroscope("Sagittarius","♐","Adventure and wisdom call your name.",3,"Purple"),
    ZodiacHoroscope("Capricorn","♑","Hard work and discipline pay off generously.",10,"Brown"),
    ZodiacHoroscope("Aquarius","♒","Innovation and originality set you apart.",11,"Blue"),
    ZodiacHoroscope("Pisces","♓","Intuition and creativity flow like water.",12,"Sea Green"),
)
