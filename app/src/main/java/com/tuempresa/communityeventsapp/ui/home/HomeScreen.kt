package com.tuempresa.communityeventsapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuempresa.communityeventsapp.ui.auth.AuthViewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos comunitarios") },
                actions = { TextButton(onClick = { vm.signOut(); onLogout() }) { Text("Salir") } }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { /* crear evento */ }) { Text("+") } }
    ) { inner ->
        Column(Modifier.padding(inner).padding(16.dp)) {
            Text("Aquí listaremos los eventos")

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Licencia: Creative Commons BY-NC-SA 4.0\nCommunityEventsApp – 2025",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )

        }
    }
}

