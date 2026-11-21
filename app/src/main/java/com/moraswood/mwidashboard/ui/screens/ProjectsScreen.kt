package com.moraswood.mwidashboard.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import com.moraswood.mwidashboard.data.AppDatabase
import com.moraswood.mwidashboard.data.Project
import kotlinx.coroutines.launch

@Composable
fun ProjectsScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { Room.databaseBuilder(context, AppDatabase::class.java, "mwi.db").build() }
    val projects by db.projectDao().getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var newName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Projects & Payments", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("New project name") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (newName.isNotBlank()) {
                    scope.launch {
                        db.projectDao().insert(Project(name = newName))
                        newName = ""
                    }
                }
            }) { Text("Add") }
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(projects) { project ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(project.name, style = MaterialTheme.typography.titleMedium)
                        Text("Quoted: ₹${project.quotedPrice}")
                        Text(project.description.ifBlank { "No description" }, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}
