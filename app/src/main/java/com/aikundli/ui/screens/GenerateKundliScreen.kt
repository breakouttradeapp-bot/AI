package com.aikundli.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aikundli.model.KundliRequest
import com.aikundli.ui.components.GlassCard
import com.aikundli.ui.navigation.Screen
import com.aikundli.ui.theme.*
import com.aikundli.viewmodel.KundliViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateKundliScreen(
    navController: NavController,
    viewModel: KundliViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.kundliState.collectAsState()

    var name      by remember { mutableStateOf("") }
    var gender    by remember { mutableStateOf("Male") }
    var dob       by remember { mutableStateOf("") }
    var tob       by remember { mutableStateOf("") }
    var place     by remember { mutableStateOf("") }
    var latitude  by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            dob = "%04d-%02d-%02d".format(y, m + 1, d)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _, h, min -> tob = "%02d:%02d".format(h, min) },
        12, 0, true
    )

    LaunchedEffect(state.kundliResult) {
        if (state.kundliResult != null) {
            navController.navigate(Screen.KundliResult.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkSpace, DeepIndigo)))
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldenStar)
            }
            Text(
                "Generate Kundli",
                style = MaterialTheme.typography.headlineMedium,
                color = GoldenStar,
                fontWeight = FontWeight.Bold
            )
        }

        GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text("🔮 Birth Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldenStar)

                KundliTextField(value = name, onValueChange = { name = it },
                    label = "Full Name", icon = "👤")

                Column {
                    Text("Gender",
                        color = StarlightWhite.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MysticViolet,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { datePicker.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Icon(Icons.Default.CalendarMonth, null, tint = GoldenStar)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (dob.isEmpty()) "Date of Birth" else dob,
                        color = if (dob.isEmpty()) StarlightWhite.copy(0.5f) else StarlightWhite
                    )
                }

                OutlinedButton(
                    onClick = { timePicker.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Icon(Icons.Default.AccessTime, null, tint = GoldenStar)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (tob.isEmpty()) "Time of Birth" else tob,
                        color = if (tob.isEmpty()) StarlightWhite.copy(0.5f) else StarlightWhite
                    )
                }

                KundliTextField(value = place, onValueChange = { place = it },
                    label = "Place of Birth", icon = "📍")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    KundliTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = "Latitude",
                        icon = "🌐",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )

                    KundliTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = "Longitude",
                        icon = "🌐",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                }

                state.error?.let {
                    Text(it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {

                        if (name.isBlank() || dob.isBlank() || tob.isBlank()
                            || latitude.isBlank() || longitude.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Please fill all required fields",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        viewModel.generateKundli(
                            KundliRequest(
                                name = name,
                                gender = gender,
                                dateOfBirth = dob,
                                timeOfBirth = tob,
                                latitude = latitude.toDoubleOrNull() ?: 0.0,
                                longitude = longitude.toDoubleOrNull() ?: 0.0,
                                timezone = "Asia/Kolkata"
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MysticViolet),
                    enabled = !state.isLoading
                ) {

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("✨ Generate Kundli", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun KundliTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = StarlightWhite.copy(alpha = 0.7f)) },
        leadingIcon = { Text(icon) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MysticViolet,
            unfocusedBorderColor = GlassBorder,
            focusedTextColor = StarlightWhite,
            unfocusedTextColor = StarlightWhite
        )
    )
}
