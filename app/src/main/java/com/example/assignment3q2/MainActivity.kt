package com.example.assignment3q2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment3q2.db.WifiDatabase
import com.example.assignment3q2.screen.DataScreen
import com.example.assignment3q2.screen.HomeScreen
import com.example.assignment3q2.ui.theme.Assignment3Q2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment3Q2Theme {
                val app = application
                val wifiStatDao = WifiDatabase.getInstance(app).wifiStatDao()
                val factory = WifiViewModelFactory(app, wifiStatDao)
                val viewModel: WifiViewModel = viewModel(factory = factory)
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(viewModel, navController) }
                    composable("data") { DataScreen(viewModel, navController) }
                }
            }
        }
    }
}
