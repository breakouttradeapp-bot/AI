@file:OptIn(ExperimentalMaterial3Api::class)

package com.aikundli.ui.screens
import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aikundli.ui.components.BannerAdView
import com.aikundli.ui.navigation.Screen
import com.aikundli.ui.theme.*
import kotlinx.coroutines.delay

data class MenuCard(
    val title: String,
    val subtitle: String,
    val icon: String,
    val gradient: List<Color>,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val menuItems = listOf(
        MenuCard("Generate Kundli","Birth chart & predictions","🔮",
            listOf(Color(0xFF6A0572), Color(0xFF1565C0)), Screen.GenerateKundli.route),
        MenuCard("Match Kundli","Compatibility analysis","💑",
            listOf(Color(0xFFE91E8C), Color(0xFF6A0572)), Screen.MatchKundli.route),
        MenuCard("Daily Horoscope","Today's cosmic insights","⭐",
            listOf(Color(0xFFFF6F00), Color(0xFFE91E8C)), Screen.DailyHoroscope.route),
        MenuCard("Saved Reports","View past analyses","📋",
            listOf(Color(0xFF00695C), Color(0xFF1565C0)), Screen.SavedReports.route),
        MenuCard("Premium","Unlock all features","👑",
            listOf(Color(0xFFFFD700), Color(0xFFFF6F00)), Screen.Premium.route),
        MenuCard("Settings","App preferences","⚙️",
            listOf(Color(0xFF37474F), Color(0xFF1A237E)), Screen.Settings.route),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025))))
    ) {

        HomeHeader()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            itemsIndexed(menuItems) { index, item ->
                AnimatedMenuCard(
                    card = item,
                    delay = index * 100,
                    onClick = { navController.navigate(item.route) }
                )
            }
        }

        BannerAdView(
            adUnitId = "ca-app-pub-3940256099942544/6300978111",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HomeHeader() {

    val infiniteTransition = rememberInfiniteTransition(label = "header")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2000),
            RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
    ) {

        Text(
            text = "✨ AI Kundli Generator",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldenStar.copy(alpha = 0.8f + 0.2f * shimmer),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Discover your cosmic destiny",
            style = MaterialTheme.typography.bodyMedium,
            color = StarlightWhite.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun AnimatedMenuCard(
    card: MenuCard,
    delay: Int,
    onClick: () -> Unit
) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label = "alpha"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = card.gradient + listOf(Color(0x33FFFFFF)),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(GlassBorder, Color.Transparent)),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {

                Text(text = card.icon, fontSize = 40.sp)

                Spacer(Modifier.height(12.dp))

                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = card.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private val Offset.Companion.Infinite
    get() = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
