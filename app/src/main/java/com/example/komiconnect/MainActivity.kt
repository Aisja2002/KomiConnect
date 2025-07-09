package com.example.komiconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.komiconnect.screens.settings.SettingsViewModel
import com.example.komiconnect.ui.KomiConnectNavGraph
import com.example.komiconnect.ui.theme.KomiConnectTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm = koinViewModel<SettingsViewModel>()
            val settingsState by settingsVm.state.collectAsStateWithLifecycle()
            KomiConnectTheme(settingsState.theme) {
                val navController = rememberNavController()
                KomiConnectNavGraph(navController)
            }
        }
    }
}
