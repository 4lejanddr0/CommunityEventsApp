package com.tuempresa.communityeventsapp.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tuempresa.communityeventsapp.data.model.Event
import com.tuempresa.communityeventsapp.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventsUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val publicEvents: List<Event> = emptyList(),
    val publicPastEvents: List<Event> = emptyList(),
    val myEvents: List<Event> = emptyList(),
    val myPastEvents: List<Event> = emptyList()
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventsUiState())
    val state: StateFlow<EventsUiState> = _state

    fun load() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)

        try {
            val publicUpcoming = repo.listPublicUpcoming()
            val publicPast = repo.listPublicPast()
            val myEvents = repo.listMine()
            val myPast = repo.listMyPast()

            _state.value = _state.value.copy(
                loading = false,
                publicEvents = publicUpcoming,
                publicPastEvents = publicPast,
                myEvents = myEvents,
                myPastEvents = myPast
            )
        } catch (t: Throwable) {
            _state.value = _state.value.copy(
                loading = false,
                error = t.message ?: "Error al cargar los eventos"
            )
        }
    }

    fun save(
        id: String?,
        title: String,
        description: String,
        location: String,
        start: Timestamp,
        end: Timestamp,
        isPublic: Boolean,
        tags: List<String>,
        onDone: () -> Unit,
        onError: (Throwable) -> Unit
    ) = viewModelScope.launch {
        try {
            val ev = Event(
                id = id,
                title = title,
                description = description,
                location = location,
                startTime = start,
                endTime = end,
                isPublic = isPublic,
                tags = tags,
                lastUpdated = Timestamp.now()
            )

            repo.upsert(ev)
            load()      // refresca listas
            onDone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    fun delete(
        id: String,
        onDone: () -> Unit,
        onError: (Throwable) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.delete(id)
            load()
            onDone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    // ---------------  FUNCIONES DE NOTIFICACIÓN --------------------

    fun checkUpcomingNotification(event: Event): Boolean {
        val now = System.currentTimeMillis()
        val eventTime = event.startTime.toDate().time

        val diff = eventTime - now
        val hours = diff / (1000 * 60 * 60)

        return hours in 1..24   // entre 1 y 24 horas
    }


}
