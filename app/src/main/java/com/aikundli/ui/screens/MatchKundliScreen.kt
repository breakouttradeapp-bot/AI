package com.aikundli.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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

    val context = LocalContext.current
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

            Text(
                "Match Kundli",
                style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )
        }

        PersonCard(
            title = "👤 Person 1 (Groom / Partner A)",
            name = name1,
            onNameChange = { value -> name1 = value },
            dob  = dob1,
            onDobChange  = { value -> dob1 = value },
            tob  = tob1,
            onTobChange  = { value -> tob1 = value },
            lat  = lat1,
            onLatChange  = { value -> lat1 = value },
            lng  = lng1,
            onLngChange  = { value -> lng1 = value }
        )

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("💕", style = MaterialTheme.typography.displayLarge)
        }

        PersonCard(
            title = "👤 Person 2 (Bride / Partner B)",
            name = name2,
            onNameChange = { value -> name2 = value },
            dob  = dob2,
            onDobChange  = { value -> dob2 = value },
            tob  = tob2,
            onTobChange  = { value -> tob2 = value },
            lat  = lat2,
            onLatChange  = { value -> lat2 = value },
            lng  = lng2,
            onLngChange  = { value -> lng2 = value }
        )

        state.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {

                if (
                    name1.isBlank() || dob1.isBlank() || tob1.isBlank() ||
                    name2.isBlank() || dob2.isBlank() || tob2.isBlank() ||
                    lat1.isBlank() || lng1.isBlank() ||
                    lat2.isBlank() || lng2.isBlank()
                ) {

                    Toast.makeText(
                        context,
                        "Please fill all birth details for both persons",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                viewModel.checkCompatibility(
                    CompatibilityRequest(
                        person1 = KundliRequest(
                            name1,
                            "Male",
                            dob1,
                            tob1,
                            lat1.toDoubleOrNull() ?: 0.0,
                            lng1.toDoubleOrNull() ?: 0.0,
                            "Asia/Kolkata"
                        ),
                        person2 = KundliRequest(
                            name2,
                            "Female",
                            dob2,
                            tob2,
                            lat2.toDoubleOrNull() ?: 0.0,
                            lng2.toDoubleOrNull() ?: 0.0,
                            "Asia/Kolkata"
                        )
                    )
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 16.dp),

            shape  = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NebulaPink),
            enabled = !state.isLoading
        ) {

            if (state.isLoading)
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else
                Text("💕 Check Compatibility", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
    }
}

/* -------------------------------------------------------------------------- */
/*  Added missing PersonCard composable                                       */
/* -------------------------------------------------------------------------- */

@Composable
fun PersonCard(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    tob: String,
    onTobChange: (String) -> Unit,
    lat: String,
    onLatChange: (String) -> Unit,
    lng: String,
    onLngChange: (String) -> Unit
) {

    GlassCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(title, style = MaterialTheme.typography.titleLarge, color = GoldenStar)

            KundliTextField(name, onNameChange, "Full Name", "👤")

            KundliTextField(dob, onDobChange, "Date of Birth (YYYY-MM-DD)", "📅")

            KundliTextField(tob, onTobChange, "Time of Birth (HH:MM)", "⏰")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                KundliTextField(
                    lat,
                    onLatChange,
                    "Latitude",
                    "🌐",
                    Modifier.weight(1f)
                )

                KundliTextField(
                    lng,
                    onLngChange,
                    "Longitude",
                    "🌐",
                    Modifier.weight(1f)
                )
            }
        }
    }
}
