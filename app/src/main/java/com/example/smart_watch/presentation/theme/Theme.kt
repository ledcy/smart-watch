package com.example.smart_watch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun Smart_watchTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}