package com.golazo.medical.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import java.util.Locale

@Composable
fun rememberSpeechRecognizer(
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
): SpeechRecognizerState {
    var isListening by remember { mutableStateOf(false) }
    
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                onResult(matches[0])
            }
        } else {
            onError("Speech recognition cancelled")
        }
    }
    
    return SpeechRecognizerState(
        isListening = isListening,
        startListening = {
            isListening = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            speechLauncher.launch(intent)
        },
        stopListening = {
            isListening = false
        }
    )
}

data class SpeechRecognizerState(
    val isListening: Boolean,
    val startListening: () -> Unit,
    val stopListening: () -> Unit
)
