package com.example.assignment3q2.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment3q2.WifiViewModel
import com.example.assignment3q2.db.WifiStat

@Composable
fun DataScreen(viewModel: WifiViewModel, navController: NavController) {
    val locations = listOf("Location 1", "Location 2", "Location 3")
    var expanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(locations[0]) }
    var wifiStats by remember { mutableStateOf<List<WifiStat>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch data when location changes
    LaunchedEffect(selectedLocation) {
        isLoading = true
        wifiStats = viewModel.getStatsForLocation(selectedLocation)
        isLoading = false
    }

    Column(Modifier.padding(16.dp)) {
        Text("Logged RSSI Data", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        // Location dropdown
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedLocation)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location) },
                        onClick = {
                            selectedLocation = location
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text("Data for $selectedLocation", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (wifiStats.isEmpty()) {
            Text("No data found for $selectedLocation")
        } else {
            Column {
                wifiStats.forEach { stat ->
                    Text("SSID: ${stat.ssid} | Avg RSSI: ${"%.2f".format(stat.avgRssi)} dBm | RSSI Range:${stat.minRssi}dBm - ${stat.maxRssi}dBm")
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}
