package com.tuempresa.communityeventsapp.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    vm: EventDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val state by vm.state.collectAsState()

    // cargar datos del evento
    LaunchedEffect(eventId) { vm.load(eventId) }

    // estado local para comentario nuevo
    var newComment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    // 👇 estado de scroll
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del evento") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                },
                actions = {
                    // Solo muestra Editar si eres propietario y hay id
                    if (state.isOwner && state.event?.id != null) {
                        TextButton(onClick = { onEdit(state.event!!.id!!) }) { Text("Editar") }
                    }
                }
            )
        }
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> {
                    val friendly = if (state.error?.contains("PERMISSION_DENIED") == true) {
                        "Este evento ya no está disponible o no tienes permiso para verlo."
                    } else {
                        state.error ?: "Ocurrió un error al cargar el evento."
                    }

                    Text(
                        friendly,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.event == null -> Text(
                    "Evento no encontrado",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {
                    state.event?.let { e ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)   // 👈 AHORA SÍ HAY SCROLL
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(e.title, style = MaterialTheme.typography.headlineSmall)
                            Text(e.location, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${fmt(e.startTime)} — ${fmt(e.endTime)}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            // Mensaje si el evento ya pasó
                            val isPast = remember(e.endTime) {
                                e.endTime.toDate().time < System.currentTimeMillis()
                            }
                            if (isPast) {
                                Text(
                                    "Este evento ya finalizó.",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            AssistChip(
                                onClick = {},
                                label = { Text(if (e.isPublic) "Público" else "Privado") }
                            )

                            if (e.description.isNotBlank()) {
                                Text(e.description, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (e.tags.isNotEmpty()) {
                                Text(
                                    "Tags: " + e.tags.joinToString(" · "),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Asistentes: ${state.attendees}",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Button(
                                onClick = { vm.toggleAttend(e.id!!) },
                                modifier = Modifier.align(Alignment.Start)
                            ) {
                                Text(if (state.attending) "Ya no asistiré" else "Asistiré")
                            }

                            Divider(Modifier.padding(vertical = 12.dp))

                            // -------- Comentarios y calificaciones --------
                            Text(
                                "Comentarios y calificaciones",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Resumen de rating, si hay reseñas
                            if (state.ratingsCount > 0 && state.averageRating != null) {
                                Text(
                                    text = "Calificación promedio: ${
                                        String.format("%.1f", state.averageRating)
                                    } ★ (${state.ratingsCount} reseñas)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(8.dp))
                            }

                            // 1) Lista de comentarios
                            if (state.comments.isEmpty()) {
                                Text("Aún no hay comentarios.")
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.comments.forEach { c ->
                                        ElevatedCard(Modifier.fillMaxWidth()) {
                                            Column(Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "${c.userName} • ${c.rating} ★",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    c.text,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // 2) Formulario para comentar (si aplica)
                            val canComment = isPast && state.attending

                            if (canComment) {
                                Text(
                                    "Tu reseña",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    for (value in 1..5) {
                                        FilterChip(
                                            selected = rating == value,
                                            onClick = { rating = value },
                                            label = { Text("$value ★") }
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = newComment,
                                    onValueChange = { newComment = it },
                                    label = { Text("Escribe un comentario sobre tu experiencia") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    enabled = newComment.isNotBlank(),
                                    onClick = {
                                        vm.addComment(e.id!!, newComment.trim(), rating)
                                        newComment = ""
                                    }
                                ) {
                                    Text("Enviar comentario")
                                }
                            } else {
                                Text(
                                    "Comentar.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun fmt(ts: Timestamp): String {
    val d = ts.toDate()
    return android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", d).toString()
}
