package com.tuempresa.communityeventsapp.data.model

import com.google.firebase.Timestamp

data class Comment(
    val id: String? = null,
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,           // 1..5
    val text: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
