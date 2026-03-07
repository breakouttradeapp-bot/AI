package com.aikundli.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aikundli.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

data class Star(
    val x: Float, val y: Float,
    val radius: Float, val alpha: Float
)

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    // ── Animation states ──────────────────────────────────────────────────
    var starsAlpha    by remember { mutableStateOf(0f) }
    var zodiacAlpha   by remember { mutableStateOf(0f) }
    var logoAlpha     by remember { mutableStateOf(0f) }
    var titleAlpha    by remember { mutableStateOf(0f) }
    var logoScale     by remember { mutableStateOf(0.3f) }

    val zodiacRotation by rememberInfiniteTransition(label = "zodiac").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val glowPulse by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )

    // Random stars (stable across recompositions)
    val stars = remember {
        List(120) {
            Star(
                x      = Random.nextFloat(),
                y      = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 0.5f,
                alpha  = Random.nextFloat() * 0.8f + 0.2f
            )
        }
    }

    // Twinkling per star
    val twinkle by rememberInfiniteTransition(label = "twinkle").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "twinkle"
    )

    // ── Sequenced animation launch ────────────────────────────────────────
    LaunchedEffect(Unit) {
        // Stars fade in
        animate(0f, 1f, animationSpec = tween(800)) { v, _ -> starsAlpha = v }
        // Zodiac wheel appears
        animate(0f, 1f, animationSpec = tween(600)) { v, _ -> zodiacAlpha = v }
        // Logo pops in
        animate(0f, 1f, animationSpec = tween(700, easing = OvershootInterpolator().toEasing())) { v, _ ->
            logoAlpha = v; logoScale = 0.3f + v * 0.7f
        }
        delay(200)
        // Title fades in
        animate(0f, 1f, animationSpec = tween(600)) { v, _ -> titleAlpha = v }
        delay(1000)
        onSplashComplete()
    }

    // ── UI ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DarkSpace, DeepIndigo, Color(0xFF120025)))
            ),
        contentAlignment = Alignment.Center
    ) {
        // Stars canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(starsAlpha)
        ) {
            drawStars(stars, twinkle)
        }

        // Content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Zodiac wheel
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .alpha(zodiacAlpha)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawZodiacWheel(zodiacRotation, glowPulse)
                }

                // Logo inside wheel
                Text(
                    text     = "☽✦☾",
                    fontSize = 48.sp,
                    modifier = Modifier
                        .alpha(logoAlpha * glowPulse)
                        .scale(logoScale)
                )
            }

            Spacer(Modifier.height(28.dp))

            // App title
            Text(
                text       = "AI Kundli Generator",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = GoldenStar,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.alpha(titleAlpha)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Your Cosmic Blueprint",
                fontSize = 14.sp,
                color    = StarlightWhite.copy(alpha = 0.7f),
                modifier = Modifier.alpha(titleAlpha * 0.8f)
            )
        }
    }
}

// ── Canvas helpers ────────────────────────────────────────────────────────

fun DrawScope.drawStars(stars: List<Star>, twinkle: Float) {
    stars.forEach { star ->
        val a = (star.alpha * (0.6f + 0.4f * twinkle)).coerceIn(0f, 1f)
        drawCircle(
            color  = Color.White.copy(alpha = a),
            radius = star.radius,
            center = Offset(star.x * size.width, star.y * size.height)
        )
    }
}

fun DrawScope.drawZodiacWheel(rotation: Float, glow: Float) {
    val cx     = size.width / 2
    val cy     = size.height / 2
    val radius = size.minDimension / 2 - 10f
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))

    // Outer glow ring
    drawCircle(
        brush  = Brush.radialGradient(
            colors = listOf(MysticViolet.copy(alpha = 0.4f * glow), Color.Transparent),
            center = Offset(cx, cy), radius = radius + 20
        ),
        radius = radius + 20,
        center = Offset(cx, cy)
    )

    // Dashed circle
    drawCircle(
        color  = MysticViolet.copy(alpha = 0.6f),
        radius = radius,
        center = Offset(cx, cy),
        style  = stroke
    )

    // 12 zodiac tick marks
    val zodiacSymbols = listOf("♈","♉","♊","♋","♌","♍","♎","♏","♐","♑","♒","♓")
    for (i in 0..11) {
        val angle  = Math.toRadians((rotation + i * 30.0))
        val startX = cx + (radius - 20) * cos(angle).toFloat()
        val startY = cy + (radius - 20) * sin(angle).toFloat()
        val endX   = cx + radius * cos(angle).toFloat()
        val endY   = cy + radius * sin(angle).toFloat()
        drawLine(
            color       = GoldenStar.copy(alpha = 0.8f),
            start       = Offset(startX, startY),
            end         = Offset(endX, endY),
            strokeWidth = 2f
        )
    }

    // Inner circle
    drawCircle(
        color  = CosmicPurple.copy(alpha = 0.3f * glow),
        radius = radius * 0.6f,
        center = Offset(cx, cy)
    )
}

private fun android.view.animation.Interpolator.toEasing() = Easing { x ->
    getInterpolation(x)
}

private fun OvershootInterpolator() = android.view.animation.OvershootInterpolator(2f)
