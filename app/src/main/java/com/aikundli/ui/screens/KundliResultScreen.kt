package com.aikundli.ui.screens
import androidx.compose.ui.draw.clip
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aikundli.model.HoroscopeResult
import com.aikundli.model.PlanetPosition
import com.aikundli.ui.components.BannerAdView
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.theme.*
import com.aikundli.util.PdfGenerator
import com.aikundli.viewmodel.KundliViewModel

@Composable
fun KundliResultScreen(
    navController: NavController,
    viewModel: KundliViewModel = viewModel()
) {
    val state   = viewModel.kundliState.collectAsState().value
    val context = LocalContext.current
    val result  = state.kundliResult ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldenStar)
            }

            Text(
                "Kundli Report",
                style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            IconButton(onClick = {
                PdfGenerator.generateAndShare(context, result, state.horoscope)
            }) {
                Icon(Icons.Default.PictureAsPdf, "Download PDF", tint = GoldenStar)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            result.chartImageBase64?.let { base64 ->
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                GlassCard(Modifier.padding(16.dp)) {
                    Column(Modifier.padding(16.dp)) {

                        Text(
                            "📊 Birth Chart",
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldenStar,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Kundli Chart",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoChip("☀️ Sun", result.sunSign)
                    InfoChip("🌙 Moon", result.moonSign)
                    InfoChip("⬆️ Lagna", result.ascendant)
                }
            }

            PlanetPositionsCard(planets = result.planets)

            state.horoscope?.let { HoroscopeCard(it) }
                ?: GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MysticViolet)
                    }
                }

            Spacer(Modifier.height(16.dp))
        }

        BannerAdView(
            adUnitId = "ca-app-pub-3940256099942544/6300978111",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label,
            style = MaterialTheme.typography.labelSmall,
            color = StarlightWhite.copy(alpha = 0.6f)
        )

        Text(value,
            style = MaterialTheme.typography.titleLarge,
            color = GoldenStar,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlanetPositionsCard(planets: List<PlanetPosition>) {

    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text(
                "🪐 Planetary Positions",
                style = MaterialTheme.typography.titleLarge,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            planets.forEach { planet ->

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {

                    Text(
                        "${planetEmoji(planet.planet)} ${planet.planet}",
                        color = StarlightWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        "${planet.sign} ${planet.degree.toInt()}° H${planet.house}${if (planet.isRetro) " (R)" else ""}",
                        color = GoldenStar.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Divider(color = GlassBorder, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun HoroscopeCard(horoscope: HoroscopeResult) {

    val sections = listOf(
        "👤 Personality"   to horoscope.personality,
        "💼 Career"        to horoscope.career,
        "💑 Marriage"      to horoscope.marriage,
        "💰 Finance"       to horoscope.finance,
        "🏥 Health"        to horoscope.health,
        "🔢 Lucky Numbers" to horoscope.luckyNumbers,
        "🎨 Lucky Colors"  to horoscope.luckyColors
    )

    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Column(Modifier.padding(16.dp)) {

            Text(
                "✨ AI Horoscope Reading",
                style = MaterialTheme.typography.titleLarge,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            sections.forEach { (title, content) ->
                ExpandableSection(title = title, content = content)
            }
        }
    }
}

@Composable
fun ExpandableSection(title: String, content: String) {

    var expanded by remember { mutableStateOf(false) }

    Column {

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {

            Text(
                title,
                color = StarlightWhite,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MysticViolet
            )
        }

        AnimatedVisibility(visible = expanded) {

            Text(
                content,
                color   = StarlightWhite.copy(alpha = 0.8f),
                style   = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Divider(color = GlassBorder, thickness = 0.5.dp)
    }
}

fun planetEmoji(name: String) = when (name.lowercase()) {
    "sun"     -> "☀️"
    "moon"    -> "🌙"
    "mars"    -> "♂️"
    "mercury" -> "☿️"
    "jupiter" -> "♃"
    "venus"   -> "♀️"
    "saturn"  -> "♄"
    "rahu"    -> "☊"
    "ketu"    -> "☋"
    else      -> "⭐"
}
