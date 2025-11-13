package com.tuempresa.communityeventsapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.communityeventsapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun isLoggedIn(): Boolean = repo.currentUser != null

    fun signInEmail(email: String, pass: String) {
        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            try {
                repo.signInWithEmail(email, pass)
                _state.value = AuthUiState(success = true)
            } catch (e: Exception) {
                _state.value = AuthUiState(error = e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun signUpEmail(name: String, email: String, pass: String) {
        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            try {
                repo.signUpWithEmail(email, pass, name)
                _state.value = AuthUiState(success = true)
            } catch (e: Exception) {
                _state.value = AuthUiState(error = e.message ?: "Error al registrarse")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _state.value = AuthUiState(loading = true)
        viewModelScope.launch {
            try {
                repo.signInWithGoogle(idToken)
                _state.value = AuthUiState(success = true)
            } catch (e: Exception) {
                _state.value = AuthUiState(error = e.message ?: "Error con Google")
            }
        }
    }

    fun signOut() = repo.signOut()

    fun clearError() { _state.value = _state.value.copy(error = null) }
}
