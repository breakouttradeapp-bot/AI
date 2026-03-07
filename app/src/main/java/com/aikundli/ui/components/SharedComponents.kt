package com.aikundli.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aikundli.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        border   = BorderStroke(1.dp, GlassBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0x22FFFFFF),
                            Color(0x11FFFFFF)
                        )
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color> = listOf(MysticViolet, CelestialBlue),
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(54.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        enabled  = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradient), RoundedCornerShape(16.dp)),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(text, color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.titleLarge,
        color    = GoldenStar,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
