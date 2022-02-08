package ru.netology.nmedia.repository

interface SignRepository {
    suspend fun updateUser(login: String, password: String)
    suspend fun registerUser(login: String, password: String, name: String)
}