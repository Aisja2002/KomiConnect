package com.example.komiconnect.screens.convention

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.repositories.DataRepository
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.ConventionResponse
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class ConventionState(val token: String, var profilePicture: Bitmap?)


class ConventionViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(ConventionState("", null))
        private set
    var meResponse by mutableStateOf<ConventionResponse?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var imageResponse by mutableStateOf<Bitmap?>(null)



    fun fetchConventionProfile(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            var result: Result<ConventionResponse, String>? = null
            if (id != null) {
                result = api.getConventionFromId(id)
                when (result) {
                    is Success -> {
                        meResponse = result.value
                        error = null
                            var imageResult = api.getConventionPicture(meResponse?.id ?: 0)
                            when (imageResult) {
                                is Success -> {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        BitmapFactory.decodeByteArray(imageResult.value, 0, imageResult.value.size)
                                    }
                                    imageResponse = bitmap
                                    state = state.copy(profilePicture = imageResponse)
                                }

                                is Failure -> {
                                    imageResponse = null
                                    state = state.copy(profilePicture = imageResponse)
                                }
                            }
                    }

                    is Failure -> {
                        meResponse = null
                        error = result.reason
                    }

                }
            }
        }
    }


    init {
        viewModelScope.launch {
            repository.token.collect { newToken ->
                state = ConventionState(repository.token.first(), null)

                if (newToken.isEmpty()) {
                    meResponse = null
                    error = null
                    state = state.copy(repository.token.first(), null)
                } else {
                    fetchConventionProfile(state.token, null)
                }
            }
        }
    }
}
