package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.repository.SignRepository
import ru.netology.nmedia.repository.SignRepositoryImpl

class SignViewModel : ViewModel() {
    private val repository: SignRepository = SignRepositoryImpl()

    fun updateUser(login: String, password: String) {
        viewModelScope.launch {
            repository.updateUser(login, password)
        }
    }

    fun registerUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            repository.registerUser(login, password, name)
        }
    }
}