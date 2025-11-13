package com.tuempresa.communityeventsapp.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tuempresa.communityeventsapp.data.model.Event
import com.tuempresa.communityeventsapp.data.model.Comment
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val col get() = db.collection("events")
    private fun uid() = auth.currentUser?.uid
    fun currentUserId() = uid()

    private fun attendeesCol(eventId: String) =
        col.document(eventId).collection("attendees")

    private fun commentsCol(eventId: String) =
        col.document(eventId).collection("comments")

    // --------------------------------------------------------------------
    // LISTAS DE EVENTOS
    // --------------------------------------------------------------------

    suspend fun listPublicUpcoming(limit: Long = 50): List<Event> {
        val now = Timestamp.now()
        val snap = col
            .whereEqualTo("public", true)
            .whereGreaterThan("endTime", now)
            .orderBy("endTime", Query.Direction.ASCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
    }

    suspend fun listMine(limit: Long = 100): List<Event> {
        val user = uid() ?: return emptyList()
        val snap = col
            .whereEqualTo("creatorId", user)
            .orderBy("startTime", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
    }

    // Eventos públicos pasados (para la sección "Eventos públicos pasados")
    suspend fun listPublicPast(limit: Long = 50): List<Event> {
        val now = Timestamp.now()
        val snap = col
            .whereEqualTo("public", true)
            .whereLessThan("endTime", now)
            .orderBy("endTime", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
    }

    // Mis eventos pasados (creados por mí y ya finalizados)
    suspend fun listMyPast(limit: Long = 50): List<Event> {
        val user = uid() ?: return emptyList()
        val now = Timestamp.now()
        val snap = col
            .whereEqualTo("creatorId", user)
            .whereLessThan("endTime", now)
            .orderBy("endTime", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
    }


    suspend fun listPastPublic(limit: Long = 50): List<Event> {
        val now = Timestamp.now()
        val snap = col
            .whereEqualTo("public", true)
            .whereLessThan("endTime", now)
            .orderBy("endTime", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Event::class.java)?.copy(id = it.id) }
    }

    // --------------------------------------------------------------------
    // CRUD BÁSICO DE EVENTOS
    // --------------------------------------------------------------------

    suspend fun getById(id: String): Event? {
        val doc = col.document(id).get().await()
        return doc.toObject(Event::class.java)?.copy(id = doc.id)
    }

    suspend fun upsert(event: Event): String {
        val user = uid() ?: throw IllegalStateException("No autenticado")
        val now = Timestamp.now()
        val data = event.copy(
            creatorId = user,
            updatedAt = now,
            createdAt = event.createdAt.takeIf { event.id != null } ?: now
        )

        // Calculamos siempre un id NO nulo
        val id = if (event.id == null) {
            // Crear nuevo documento
            col.add(data).await().id
        } else {
            // Actualizar existente
            val existingId = event.id ?: throw IllegalStateException("Id nulo en actualización")
            col.document(existingId).set(data).await()
            existingId
        }

        return id
    }


    suspend fun delete(id: String) {
        col.document(id).delete().await()
    }

    // --------------------------------------------------------------------
    // ASISTENTES
    // --------------------------------------------------------------------

    suspend fun attend(eventId: String) {
        val user = uid() ?: return
        attendeesCol(eventId)
            .document(user)
            .set(mapOf("joinedAt" to Timestamp.now()))
            .await()
    }

    suspend fun unAttend(eventId: String) {
        val user = uid() ?: return
        attendeesCol(eventId)
            .document(user)
            .delete()
            .await()
    }

    suspend fun attendeesCount(eventId: String): Long {
        val snap = attendeesCol(eventId).get().await()
        return snap.size().toLong()
    }

    suspend fun isAttending(eventId: String): Boolean {
        val user = uid() ?: return false
        val doc = attendeesCol(eventId).document(user).get().await()
        return doc.exists()
    }

    // --------------------------------------------------------------------
    // COMENTARIOS
    // --------------------------------------------------------------------

    suspend fun listComments(eventId: String): List<Comment> {
        val snap = commentsCol(eventId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snap.documents.mapNotNull {
            it.toObject(Comment::class.java)?.copy(id = it.id)
        }
    }

    suspend fun addComment(eventId: String, text: String, rating: Int) {
        val userId = uid() ?: throw IllegalStateException("No autenticado")
        val userName = auth.currentUser?.displayName
            ?: auth.currentUser?.email
            ?: "Anónimo"

        val comment = Comment(
            eventId = eventId,
            userId = userId,
            userName = userName,
            rating = rating,
            text = text,
            createdAt = Timestamp.now()
        )

        commentsCol(eventId).add(comment).await()
    }
}
