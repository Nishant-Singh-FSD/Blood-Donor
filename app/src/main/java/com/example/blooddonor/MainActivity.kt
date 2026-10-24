package com.example.blooddonor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.blooddonor.presentation.navigation.BloodDonorNavGraph
import com.example.blooddonor.ui.theme.BloodDonorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BloodDonorTheme {
                val navController = rememberNavController()
                BloodDonorNavGraph(navController = navController)
            }
        }
    }
}
