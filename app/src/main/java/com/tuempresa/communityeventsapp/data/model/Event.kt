package com.tuempresa.communityeventsapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Event(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),

    @get:PropertyName("public")
    @set:PropertyName("public")
    var isPublic: Boolean = false,   //

    val tags: List<String> = emptyList(),
    val creatorId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)



