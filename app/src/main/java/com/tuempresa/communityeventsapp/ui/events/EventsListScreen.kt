package com.tuempresa.communityeventsapp.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.tuempresa.communityeventsapp.data.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsListScreen(
    viewModel: EventsViewModel,
    onOpen: (String) -> Unit,   // <- abrir detalle
    onCreate: () -> Unit,       // <- crear nuevo
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eventos comunitarios") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) { Text("+") }
        }
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    "Error: ${state.error}",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Próximos públicos
                        item { SectionHeader("Próximos públicos") }
                        if (state.publicEvents.isEmpty()) {
                            item { Text("No hay eventos públicos próximos.") }
                        } else {
                            items(state.publicEvents) { e ->
                                EventCard(e) { e.id?.let(onOpen) }
                            }
                        }

                        item { Spacer(Modifier.height(24.dp)) }

                        // Mis eventos
                        item { SectionHeader("Mis eventos") }
                        if (state.myEvents.isEmpty()) {
                            item { Text("Aún no has creado eventos.") }
                        } else {
                            items(state.myEvents) { e ->
                                EventCard(e) { e.id?.let(onOpen) }
                            }
                        }

                        item { Spacer(Modifier.height(24.dp)) }

                        // Eventos públicos pasados
                        item { SectionHeader("Eventos públicos pasados") }
                        if (state.publicPastEvents.isEmpty()) {
                            item { Text("No hay registro de eventos públicos pasados.") }
                        } else {
                            items(state.publicPastEvents) { e ->
                                EventCard(e) { e.id?.let(onOpen) }
                            }
                        }

                        item { Spacer(Modifier.height(24.dp)) }

                        // Mis eventos pasados
                        item { SectionHeader("Mis eventos pasados") }
                        if (state.myPastEvents.isEmpty()) {
                            item { Text("Aún no tienes eventos pasados registrados.") }
                        } else {
                            items(state.myPastEvents) { e ->
                                EventCard(e) { e.id?.let(onOpen) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleLarge)
            Text(event.location, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${fmt(event.startTime)} - ${fmt(event.endTime)}",
                style = MaterialTheme.typography.bodySmall
            )
            if (event.tags.isNotEmpty()) {
                Text(
                    event.tags.joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun fmt(ts: Timestamp): String {
    val d = ts.toDate()
    return android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", d).toString()
}
