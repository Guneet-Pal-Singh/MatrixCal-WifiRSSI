package com.example.assignment3q2.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment3q2.WifiViewModel

@Composable
fun HomeScreen(
    viewModel: WifiViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val locations = listOf("Location 1", "Location 2", "Location 3")
    var expanded by remember { mutableStateOf(false) }
    val currentLocation by remember { derivedStateOf { viewModel.currentLocation } }
    val isScanning by remember { derivedStateOf { viewModel.isScanning } }
    val scanCount by remember { derivedStateOf { viewModel.scanCount } }

    val rssiMatrix = viewModel.rssiMatrix

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            // Location selection dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(0.7f)
                ) {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(currentLocation)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        locations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    viewModel.selectLocation(location)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Selected: $currentLocation", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.startScan(context) },
                    enabled = !isScanning,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start Scan")
                }
                if (isScanning) {
                    Button(
                        onClick = { viewModel.stopScan(context) },
                        enabled = isScanning,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Stop Scan")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("data") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Logged Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Scans: $scanCount / ${viewModel.readingsPerLocation}")

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // --- Live RSSI Averages Section ---
            Text("RSSI Averages for $currentLocation", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            val stats = viewModel.getWifiStatisticsWithRange()

            AnimatedVisibility(visible = stats.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    if (stats.isEmpty()) {
                        item {
                            Text("No data yet", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        items(stats.entries.toList()) { (ssid, triple) ->
                            val (avgRssi, minRssi, maxRssi) = triple
                            Text(
                                "SSID: $ssid | Avg RSSI: ${"%.2f".format(avgRssi)} dBm | Range: $minRssi to $maxRssi"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // --- RSSI Matrix Section ---
            Text("RSSI Matrix for $currentLocation", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(rssiMatrix[currentLocation]?.take(100) ?: emptyList()) { index, wifiList ->
                    Column(
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Scan $index")
                        wifiList.forEach { wifi ->
                            Text("SSID: ${wifi.ssid} | RSSI: ${wifi.rssi} dBm")
                        }
                    }
                }
            }
        }
    }
}
