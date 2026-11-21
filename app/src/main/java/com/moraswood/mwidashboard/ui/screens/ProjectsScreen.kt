package com.moraswood.mwidashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProjectsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Projects & Payments", style = MaterialTheme.typography.headlineMedium)
        Text("Project list, details, quotations, payments, documents, comments will go here.")
        Spacer(Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}
