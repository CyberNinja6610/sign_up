package ru.netology.nmedia.model

data class SignUpModelState(
    val loginError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val nameError: String? = null
)
