package com.golazo.medical

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.golazo.medical.data.PreferencesManager
import com.golazo.medical.data.repository.SessionManager
import com.golazo.medical.ui.theme.GolazoTheme
import com.golazo.medical.navigation.GolazoNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentUser by sessionManager.currentUser.collectAsState()
            val isDoctor = currentUser?.role == "doctor"
            
            // Use separate theme settings for Doctor and Player
            val doctorTheme by preferencesManager.doctorThemeFlow.collectAsState()
            val playerTheme by preferencesManager.playerThemeFlow.collectAsState()
            
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = if (isDoctor) {
                when (doctorTheme) {
                    "Dark" -> true
                    "Light" -> false
                    else -> isSystemDark
                }
            } else {
                // Player always uses light theme (or their own setting)
                when (playerTheme) {
                    "Dark" -> true
                    "Light" -> false
                    else -> false // Default to light for player
                }
            }
            
            GolazoTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GolazoNavHost()
                }
            }
        }
    }
}
