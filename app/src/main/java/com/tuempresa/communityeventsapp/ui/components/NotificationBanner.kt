package com.tuempresa.communityeventsapp.ui.components

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun NotificationBanner(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
