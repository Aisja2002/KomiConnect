package com.example.komiconnect.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.repositories.DataRepository
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.UserResponse
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.launch


data class MapState(val token: String, var conventions: List<ConventionResponse>)

class MapViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(MapState("", emptyList()))
        private set
    var error by mutableStateOf<String?>(null)
        private set


    fun allConventions(token: String) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.getAllConventions()) {
                is Success -> {
                    state.conventions = result.value.toList()
                    error = null
                }
                is Failure -> {
                    state.conventions = emptyList()
                    error = result.reason
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.token.collect { newToken ->
                state = MapState(newToken, emptyList())
                allConventions(newToken)
            }
        }
    }
}