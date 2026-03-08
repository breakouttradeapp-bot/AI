package com.aikundli.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aikundli.ui.theme.*

@Composable
fun MatchResultScreen(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { navController.popBackStack() }) {

                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = GoldenStar
                )
            }

            Text(
                "Compatibility Result",
                style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                "Match result will appear here",
                color = StarlightWhite
            )
        }

    }

}
