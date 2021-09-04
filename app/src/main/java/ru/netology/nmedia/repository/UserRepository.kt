package ru.netology.nmedia.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.*
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.Token
import java.io.File
import java.io.IOException

class UserRepository() {

    suspend fun updateUser(login: String, password: String): Token {
        try {
            val response = UserApi.service.updateUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun registerUser(login: String, password: String, name: String): Token {
        try {
            val response = UserApi.service.registerUser(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        media: File
    ): Token {
        try {
            val mediaPart = MultipartBody.Part.createFormData(
                "file", media.name, media.asRequestBody(),
            )

            val response = UserApi.service.registerWithPhoto(
                media = mediaPart,
                login = login.toRequestBody("text/plain".toMediaType()),
                pass = password.toRequestBody("text/plain".toMediaType()),
                name = name.toRequestBody("text/plain".toMediaType()),
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}
