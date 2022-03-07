package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.repository.SignRepository
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(private val repository: SignRepository) : ViewModel() {

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