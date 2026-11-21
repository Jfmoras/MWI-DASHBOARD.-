package com.moraswood.mwidashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("AI API configuration, length converter, and other settings.")
        Spacer(Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}
