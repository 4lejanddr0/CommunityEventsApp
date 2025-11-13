package com.tuempresa.communityeventsapp.ui.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    viewModel: EventsViewModel,
    eventId: String?,
    onDone: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val ctx = LocalContext.current
    var loading by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(TextFieldValue()) }
    var desc by remember { mutableStateOf(TextFieldValue()) }
    var location by remember { mutableStateOf(TextFieldValue()) }
    var isPublic by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf(TextFieldValue()) }

    // --- Fecha/hora usando Calendar ---
    val startCal = remember { Calendar.getInstance() }
    val endCal = remember { Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) } }

    // Precarga si es edición
    LaunchedEffect(eventId, state.myEvents, state.publicEvents) {
        if (!eventId.isNullOrBlank()) {
            val all = state.myEvents + state.publicEvents
            val found = all.firstOrNull { it.id == eventId }
            if (found != null) {
                title = TextFieldValue(found.title)
                desc = TextFieldValue(found.description)
                location = TextFieldValue(found.location)
                isPublic = found.isPublic                 // <- usa el valor real
                tags = TextFieldValue(found.tags.joinToString(", "))

                startCal.time = found.startTime.toDate()
                endCal.time = found.endTime.toDate()
            }
        }
    }

    fun toTimestamp(cal: Calendar) = Timestamp(Date(cal.timeInMillis))

    val fmtDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val fmtTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val fmtFull = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // Validación simple de rango
    val dateError: String? =
        if (endCal.timeInMillis <= startCal.timeInMillis)
            "La fecha/hora de fin debe ser posterior al inicio."
        else null

    // Diálogos nativos
    fun showDatePicker(isStart: Boolean) {
        val cal = if (isStart) startCal else endCal
        DatePickerDialog(
            ctx,
            { _, y, m, d ->
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
                if (endCal.timeInMillis <= startCal.timeInMillis) {
                    endCal.timeInMillis = startCal.timeInMillis + 60 * 60 * 1000
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker(isStart: Boolean) {
        val cal = if (isStart) startCal else endCal
        TimePickerDialog(
            ctx,
            { _, h, min ->
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, min)
                if (endCal.timeInMillis <= startCal.timeInMillis) {
                    endCal.timeInMillis = startCal.timeInMillis + 60 * 60 * 1000
                }
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (eventId == null) "Nuevo evento" else "Editar evento")
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lugar") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Inicio ---
            Text("Inicio", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showDatePicker(true) }) {
                    Text(fmtDate.format(startCal.time))
                }
                OutlinedButton(onClick = { showTimePicker(true) }) {
                    Text(fmtTime.format(startCal.time))
                }
            }

            // --- Fin ---
            Text("Fin", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showDatePicker(false) }) {
                    Text(fmtDate.format(endCal.time))
                }
                OutlinedButton(onClick = { showTimePicker(false) }) {
                    Text(fmtTime.format(endCal.time))
                }
            }

            if (dateError != null) {
                Text(
                    dateError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Visibilidad
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = isPublic,
                    onClick = { isPublic = true },
                    label = { Text("Público") }
                )
                FilterChip(
                    selected = !isPublic,
                    onClick = { isPublic = false },
                    label = { Text("Privado") }
                )
            }

            // Etiquetas (tags)
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Etiquetas (separadas por coma)") },
                placeholder = { Text("Ej.: Familiar, Música, Centro Histórico") },
                supportingText = { Text("También puedes usar punto y coma (;)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !loading && dateError == null,
                    onClick = {
                        loading = true
                        // acepta coma o punto y coma
                        val tagList = tags.text
                            .split(",", ";")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        viewModel.save(
                            id = eventId,
                            title = title.text,
                            description = desc.text,
                            location = location.text,
                            start = toTimestamp(startCal),
                            end = toTimestamp(endCal),
                            isPublic = isPublic,
                            tags = tagList,
                            onDone = { loading = false; onDone() },
                            onError = { loading = false }
                        )
                    }
                ) {
                    Text(if (eventId == null) "Crear" else "Guardar")
                }

                if (eventId != null) {
                    val scope = rememberCoroutineScope()
                    OutlinedButton(
                        enabled = !loading,
                        onClick = {
                            scope.launch {
                                viewModel.delete(
                                    eventId,
                                    onDone = onDone,
                                    onError = {}
                                )
                            }
                        }
                    ) {
                        Text("Eliminar")
                    }
                }
            }

            // Vista previa
            Text(
                "Resumen: ${fmtFull.format(startCal.time)} → ${fmtFull.format(endCal.time)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
