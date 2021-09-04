package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.model.*
import ru.netology.nmedia.repository.UserRepository

@ExperimentalCoroutinesApi
class SignUpViewModel (application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository()
    private val _login: MutableStateFlow<String> = MutableStateFlow("")
    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    private val _confirmPassword: MutableStateFlow<String> = MutableStateFlow("")
    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    private val _state: MutableStateFlow<SignUpModelState> = MutableStateFlow(SignUpModelState())
    private val _token: MutableStateFlow<Token?> = MutableStateFlow(null)
    private val _photo: MutableStateFlow<PhotoModel?> = MutableStateFlow(null)


    val login = _login.asLiveData()
    val password = _password.asLiveData()
    val state = _state.asLiveData()
    val confirmPassword = _confirmPassword.asLiveData()
    val name = _name.asLiveData()
    val token = _token.asLiveData()
    val photo = _photo.asLiveData()


    fun editLogin(value: String) {
        _login.value = value
    }

    fun editPassword(value: String) {
        _password.value = value
    }

    fun editConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun editName(value: String) {
        _name.value = value
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun validatePassword() {
        _state.value = _state.value.copy(
            passwordError = if (password.value?.isEmpty() != false) {
                getApplication<Application>().getString(R.string.enter_password)
            } else {
                null
            }
        )
    }

    fun validateConfirmPassword() {
        _state.value = _state.value.copy(
            confirmPasswordError = if (confirmPassword.value?.isEmpty() != false) {
                getApplication<Application>().getString(R.string.enter_confirm_password)
            } else {
                null
            }
        )
    }

    private fun validatePasswordEquals() {
        val error = if (confirmPassword.value != password.value) {
            getApplication<Application>().getString(R.string.passwords_not_equals)
        } else {
            null
        };
        _state.value = _state.value.copy(
            confirmPasswordError = error,
            passwordError = error
        )
    }

    fun validateLogin() {
        _state.value = _state.value.copy(
            loginError = if (login.value?.isEmpty() != false) {
                getApplication<Application>().getString(R.string.enter_login)
            } else {
                null
            }
        )
    }

    fun validateName() {
        _state.value = _state.value.copy(
            nameError = if (login.value?.isEmpty() != false) {
                getApplication<Application>().getString(R.string.enter_name)
            } else {
                null
            }
        )
    }


    fun submit() = viewModelScope.launch {
        try {
            validateLogin()
            validatePassword()
            validateConfirmPassword()
            validateName()
            validatePasswordEquals()
            if (_login.value.isBlank()
                || _login.value.isBlank()
                || _name.value.isBlank()
                || _confirmPassword.value.isBlank()
                || _password.value != _confirmPassword.value
            ) {
                return@launch
            }
            if (_photo.value != null) {
                _token.value = _photo.value?.uri?.let {
                    repository.registerWithPhoto(
                        _login.value,
                        _password.value,
                        _name.value,
                        it.toFile()
                    )
                }
            } else {
                _token.value = repository.registerUser(_login.value, _password.value, _name.value)
            }

            _state.value = SignUpModelState()
        } catch (e: AppError) {
            val error = getApplication<Application>().getString(R.string.unknown_error);
            _state.value = SignUpModelState(
                loginError = error,
                passwordError = error,
                confirmPasswordError = error,
                nameError = error,
            )
            println(state.value)
        } catch (e: Exception) {
            val error = getApplication<Application>().getString(R.string.unknown_error);
            _state.value = SignUpModelState(
                loginError = error,
                passwordError = error,
                confirmPasswordError = error,
                nameError = error,
            )
        }
    }

    fun reset() {
        _login.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _photo.value = null
        _name.value = ""

        _state.value = SignUpModelState()
        _token.value = null
    }


}