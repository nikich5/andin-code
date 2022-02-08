package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import kotlin.Exception

class SignRepositoryImpl : SignRepository {

    override suspend fun updateUser(login: String, password: String) {
        try {
            val response = PostsApi.retrofitService.updateUser(login, password)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            AppAuth.getInstance().setAuth(body.id, body.token ?: "errorToken")

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, password: String, name: String) {
        try {
            val response = PostsApi.retrofitService.registerUser(login, password, name)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            AppAuth.getInstance().setAuth(body.id, body.token ?: "errorToken")

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}