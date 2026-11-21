package com.moraswood.mwidashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Machine(val name: String, val status: String = "Running", val issuedDate: String = "")

@Composable
fun MachinesScreen(navController: NavController) {
    var machines by remember { mutableStateOf(listOf(
        Machine("CNC Router", "Running", "2025-01-15"),
        Machine("Panel Saw", "Out of operation", "2024-11-02")
    )) }
    var newName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Machines", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Machine name") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (newName.isNotBlank()) {
                    machines = machines + Machine(newName)
                    newName = ""
                }
            }) { Text("Add") }
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(machines) { m ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(m.name, style = MaterialTheme.typography.titleMedium)
                        Text("Status: ${m.status}")
                        Text("Issued: ${m.issuedDate.ifBlank { "-" }}")
                    }
                }
            }
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}
