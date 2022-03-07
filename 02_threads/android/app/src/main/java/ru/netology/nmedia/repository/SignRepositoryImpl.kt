package ru.netology.nmedia.repository

import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception

@Singleton
class SignRepositoryImpl @Inject constructor(
    private val auth: AppAuth,
    private val apiService: ApiService
    ) : SignRepository {

    override suspend fun updateUser(login: String, password: String) {
        try {
            val response = apiService.updateUser(login, password)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            auth.setAuth(body.id, body.token ?: "errorToken")

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, password: String, name: String) {
        try {
            val response = apiService.registerUser(login, password, name)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            auth.setAuth(body.id, body.token ?: "errorToken")

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}