package com.example.komiconnect.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.repositories.DataRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class LoginState(val token: String)

class LoginViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state: LoginState? by mutableStateOf(null)
        private set

    fun setToken(token: String) {
        state = LoginState(token)
        viewModelScope.launch {
            repository.setToken(token)
        }
    }

    init {
        viewModelScope.launch {
            state = LoginState(repository.token.first())
        }
    }
}