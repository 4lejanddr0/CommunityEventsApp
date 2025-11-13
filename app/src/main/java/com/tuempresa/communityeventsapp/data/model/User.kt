package com.tuempresa.communityeventsapp.data.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
