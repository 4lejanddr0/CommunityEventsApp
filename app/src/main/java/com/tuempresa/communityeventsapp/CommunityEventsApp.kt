package com.tuempresa.communityeventsapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import dagger.hilt.android.HiltAndroidApp
import kotlin.concurrent.thread
import java.net.HttpURLConnection
import java.net.URL

@HiltAndroidApp
class CommunityEventsApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Asegura init de Firebase (por si auto-init no ocurrió)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        val db = Firebase.firestore

        // 🔧 Desactiva caché temporalmente para evitar estado "offline" guardado
        db.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false
        }

        // 🔌 Fuerza Firestore a red online
        db.enableNetwork()
            .addOnSuccessListener { println("Firestore online ✅ (app)") }
            .addOnFailureListener { it.printStackTrace() }

        // (Opcional de diagnóstico de red — puedes quitarlo luego)
        thread {
            try {
                fun ping(url: String): Int {
                    val c = (URL(url).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 8000
                        readTimeout = 8000
                    }
                    val code = c.responseCode
                    c.disconnect()
                    return code
                }
                println("HTTP google.com -> " + ping("https://www.google.com"))
                println("HTTP firestore.googleapis.com -> " + ping("https://firestore.googleapis.com"))
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
