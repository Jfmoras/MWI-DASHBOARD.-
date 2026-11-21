package com.moraswood.mwidashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text("Moras Wood Industries", style = MaterialTheme.typography.headlineMedium)
        Text("Dashboard", style = MaterialTheme.typography.titleMedium)

        Button(onClick = { navController.navigate("quotation") }, modifier = Modifier.fillMaxWidth()) {
            Text("1. Quotation & Estimation")
        }
        Button(onClick = { /* Hopus calculator */ }, modifier = Modifier.fillMaxWidth()) {
            Text("2. Hopus Calculator")
        }
        Button(onClick = { navController.navigate("projects") }, modifier = Modifier.fillMaxWidth()) {
            Text("3. Projects & Payments")
        }
        Button(onClick = { /* Inventory later */ }, modifier = Modifier.fillMaxWidth(), enabled = false) {
            Text("4. Inventory & Expenses (coming)")
        }
        Button(onClick = { navController.navigate("machines") }, modifier = Modifier.fillMaxWidth()) {
            Text("5. Machines")
        }
        Button(onClick = { navController.navigate("settings") }, modifier = Modifier.fillMaxWidth()) {
            Text("6. Settings")
        }
    }
}
