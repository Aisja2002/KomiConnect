package com.example.komiconnect.screens.add

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.repositories.DataRepository
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.PostData
import com.example.komiconnect.network.PostRequest
import com.example.komiconnect.network.UserData
import com.example.komiconnect.screens.login.LoginState
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.launch


data class AddState(val token: String, var conventions: List<ConventionResponse>)

class AddViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(AddState("", emptyList()))
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var addResponse by mutableStateOf<String?>(null)
        private set
    var addError by mutableStateOf<String?>(null)
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

    fun uploadPicture(token: String, id: Int, data: ByteArray) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.uploadPostPicture(id, data)) {
                is Success -> {
                    addResponse = result.value
                    addError = null
                }

                is Failure -> {
                    addResponse = null
                    addError = result.reason
                }
            }
        }
    }


    init {
        viewModelScope.launch {
            repository.token.collect { newToken ->
                state = AddState(newToken, emptyList())
                allConventions(newToken)
            }
        }
    }
}