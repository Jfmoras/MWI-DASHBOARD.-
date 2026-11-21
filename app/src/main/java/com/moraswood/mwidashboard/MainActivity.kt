package com.moraswood.mwidashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moraswood.mwidashboard.ui.theme.MWIDashboardTheme
import com.moraswood.mwidashboard.ui.screens.HomeScreen
import com.moraswood.mwidashboard.ui.screens.QuotationEstimationScreen
import com.moraswood.mwidashboard.ui.screens.ProjectsScreen
import com.moraswood.mwidashboard.ui.screens.MachinesScreen
import com.moraswood.mwidashboard.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MWIDashboardTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen(navController) }
                        composable("quotation") { QuotationEstimationScreen(navController) }
                        composable("projects") { ProjectsScreen(navController) }
                        composable("machines") { MachinesScreen(navController) }
                        composable("settings") { SettingsScreen(navController) }
                    }
                }
            }
        }
    }
}
