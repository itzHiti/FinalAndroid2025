package kz.itzhiti.donernaabaya.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.itzhiti.donernaabaya.config.ApiConfig
import kz.itzhiti.donernaabaya.data.repositories.AuthException
import kz.itzhiti.donernaabaya.data.repositories.AuthRepository

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    private val _isLoggedIn = MutableLiveData<Boolean>(repo.isLoggedIn())
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val username: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Пожалуйста заполните все поля")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                repo.login(username, password, ApiConfig.KEYCLOAK_CLIENT_SECRET)
                _loginState.value = LoginState.Success(username)
                _isLoggedIn.value = true
            } catch (e: AuthException) {
                _loginState.value = LoginState.Error(e.message ?: "Ошибка входа")
            } catch (t: Throwable) {
                _loginState.value = LoginState.Error("Ошибка соединения: ${t.message}")
            }
        }
    }

    fun logout() {
        repo.logout()
        _isLoggedIn.value = false
        _loginState.value = LoginState.Idle
    }

    fun getUsername(): String? = repo.getUsername()
}
