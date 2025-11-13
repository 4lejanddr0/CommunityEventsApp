package com.tuempresa.communityeventsapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.communityeventsapp.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    val currentUser get() = auth.currentUser

    suspend fun signInWithEmail(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
        ensureUserDoc()
    }

    suspend fun signUpWithEmail(email: String, pass: String, displayName: String) {
        auth.createUserWithEmailAndPassword(email, pass).await()
        auth.currentUser?.updateProfile(
            com.google.firebase.auth.userProfileChangeRequest { this.displayName = displayName }
        )?.await()
        ensureUserDoc()
    }

    suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
        ensureUserDoc()
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun ensureUserDoc() {
        val u = auth.currentUser ?: return
        val doc = db.collection("users").document(u.uid)
        val snap = doc.get().await()
        if (!snap.exists()) {
            val user = User(
                uid = u.uid,
                displayName = u.displayName ?: "",
                email = u.email ?: "",
                photoUrl = u.photoUrl?.toString()
            )
            doc.set(user).await()
        }
    }
}
