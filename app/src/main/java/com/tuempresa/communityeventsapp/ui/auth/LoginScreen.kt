package com.tuempresa.communityeventsapp.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.tuempresa.communityeventsapp.R

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current

    var isRegister by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    // Google launcher
    val googleLauncher = rememberLauncherForActivityResult(StartActivityForResult()) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) vm.signInWithGoogle(idToken)
            } catch (e: Exception) {
                // error ya se maneja pasando por el state si quieres
            }
        }
    }

    // Navegar si autenticó
    LaunchedEffect(state.success) {
        if (state.success) onLoggedIn()
    }

    // UI
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(24.dp)) {
            Text(if (isRegister) "Crear cuenta" else "Iniciar sesión",
                style = MaterialTheme.typography.headlineSmall)

            if (isRegister) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Nombre") }, singleLine = true)
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Correo") }, singleLine = true)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isRegister) vm.signUpEmail(name, email, pass)
                    else vm.signInEmail(email, pass)
                },
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isRegister) "Registrarme" else "Entrar") }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    // Config GoogleSignIn
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(ctx.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(ctx, gso)
                    googleLauncher.launch(client.signInIntent)
                },
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Continuar con Google") }

            TextButton(onClick = { isRegister = !isRegister }) {
                Text(if (isRegister) "¿Ya tienes cuenta? Inicia sesión"
                else "¿No tienes cuenta? Regístrate")
            }

            if (state.loading) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
