package com.tuempresa.communityeventsapp.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.communityeventsapp.data.model.Comment
import com.tuempresa.communityeventsapp.data.model.Event
import com.tuempresa.communityeventsapp.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventDetailUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val event: Event? = null,
    val isOwner: Boolean = false,
    val attendees: Long = 0,
    val attending: Boolean = false,

    // Comentarios y rating
    val comments: List<Comment> = emptyList(),
    val averageRating: Double? = null,   // promedio (1–5) o null si no hay
    val ratingsCount: Int = 0            // cuántas reseñas hay
)

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state

    fun load(eventId: String) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)

        try {
            val event = repo.getById(eventId)
            if (event == null) {
                _state.value = _state.value.copy(
                    loading = false,
                    event = null,
                    error = "Evento no encontrado"
                )
                return@launch
            }

            val uid = repo.currentUserId()
            val isOwner = uid != null && uid == event.creatorId
            val attendees = repo.attendeesCount(eventId)
            val attending = repo.isAttending(eventId)

            val comments = repo.listComments(eventId)
            val avg = comments.map { it.rating }.average()
            val avgOrNull = if (avg.isNaN()) null else avg

            _state.value = _state.value.copy(
                loading = false,
                event = event,
                isOwner = isOwner,
                attendees = attendees,
                attending = attending,
                comments = comments,
                averageRating = avgOrNull,
                ratingsCount = comments.size,
                error = null
            )
        } catch (t: Throwable) {
            _state.value = _state.value.copy(
                loading = false,
                error = t.message ?: "Error al cargar el evento"
            )
        }
    }

    fun toggleAttend(eventId: String) = viewModelScope.launch {
        try {
            val attendingNow = _state.value.attending
            if (attendingNow) {
                repo.unAttend(eventId)
            } else {
                repo.attend(eventId)
            }

            val attendees = repo.attendeesCount(eventId)
            val attending = repo.isAttending(eventId)

            _state.value = _state.value.copy(
                attending = attending,
                attendees = attendees
            )
        } catch (t: Throwable) {
            _state.value = _state.value.copy(error = t.message)
        }
    }

    fun addComment(eventId: String, text: String, rating: Int) = viewModelScope.launch {
        try {
            repo.addComment(eventId, text, rating)

            // recargamos comentarios y promedio
            val comments = repo.listComments(eventId)
            val avg = comments.map { it.rating }.average()
            val avgOrNull = if (avg.isNaN()) null else avg

            _state.value = _state.value.copy(
                comments = comments,
                averageRating = avgOrNull,
                ratingsCount = comments.size
            )
        } catch (t: Throwable) {
            _state.value = _state.value.copy(error = t.message)
        }
    }
}
