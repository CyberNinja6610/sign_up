package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.SignInModelState
import ru.netology.nmedia.model.Token
import ru.netology.nmedia.repository.UserRepository

@ExperimentalCoroutinesApi
class SignInViewModel (application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository()
    private val _login: MutableStateFlow<String> = MutableStateFlow("")
    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    private val _state: MutableStateFlow<SignInModelState> = MutableStateFlow(SignInModelState())
    private val _token: MutableStateFlow<Token?> = MutableStateFlow(null)

    val login = _login.asLiveData()
    val password = _password.asLiveData()
    val state = _state.asLiveData()
    val token = _token.asLiveData()


    fun editLogin(value: String) {
        _login.value = value
    }

    fun editPassword(value: String) {
        _password.value = value
    }

    fun validatePassword() {
        _state.value = SignInModelState(
            passwordError = if (password.value?.isEmpty() != false) {
                getApplication<Application>().getString(R.string.enter_password)
            } else {
                null
            }, loginError = _state.value.loginError
        )
    }

    fun validateLogin() {
        _state.value = SignInModelState(
            loginError = if (login.value?.isEmpty() != false) {
                getApplication<Application>().getString(R.string.enter_login)
            } else {
                null
            }, passwordError = _state.value.passwordError
        )
    }


    fun submit() = viewModelScope.launch {
        try {
            validateLogin()
            validatePassword()
            if (_login.value.isBlank() || _login.value.isBlank()) {
                return@launch
            }
            _token.value = repository.updateUser(_login.value, _password.value)
            _state.value = SignInModelState()
        } catch (e: AppError) {
            val error = getApplication<Application>().getString(R.string.unknown_error);
            _state.value = SignInModelState(
                loginError = error,
                passwordError = error
            )
            println(state.value)
        } catch (e: Exception) {
            val error = getApplication<Application>().getString(R.string.unknown_error);
            _state.value = SignInModelState(
                loginError = error,
                passwordError = error
            )
        }
    }

    fun reset () {
        _login.value = ""
        _password.value = ""
        _state.value = SignInModelState()
        _token.value = null
    }


}