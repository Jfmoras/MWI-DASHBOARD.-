package com.moraswood.mwidashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun QuotationEstimationScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Quotation & Estimation", style = MaterialTheme.typography.headlineMedium)
        Text("1.1 CFT Calculator")
        Text("1.2 Estimation")
        Text("1.3 Quotation")
        Text("1.4 Saved Notes")
        Text("1.5 Saved Quotations")
        Text("1.6 Saved Calculations")
        Spacer(Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }) { Text("Back") }
    }
}
