package com.localpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.localpulse.data.repository.AuthRepository
import com.localpulse.ui.navigation.AppNavigation
import com.localpulse.ui.theme.LocalPulseTheme
import com.localpulse.util.Constants

/**
 * Main entry point of the LocalPulse application
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        val authRepository = AuthRepository()
        val startDestination = if (authRepository.isUserLoggedIn()) {
            Constants.ROUTE_HOME
        } else {
            Constants.ROUTE_LOGIN
        }

        setContent {
            LocalPulseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}

