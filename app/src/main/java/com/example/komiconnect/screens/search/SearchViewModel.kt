package com.example.komiconnect.screens.search

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class SearchState(val token: String, var users: List<UserResponse>, var conventions: List<ConventionResponse>, var imageConventions: Map<Int, Bitmap?>, var imageUsers: Map<Int, Bitmap?>)

class SearchViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(SearchState("", emptyList(), emptyList(), emptyMap(), emptyMap()))
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun allUsers(token: String) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.getAllUsers()) {
                is Success -> {
                    val usersList = result.value.toList()
                    val newMap = state.imageUsers.toMutableMap()
                    error = null
                    val loadedImages = mutableMapOf<Int, Bitmap?>()
                    coroutineScope {
                        val deferredList = usersList.map { user ->
                            async {
                                if (!newMap.containsKey(user.id)) {
                                    val imageResult = api.getUserPicture(user.id)
                                    when (imageResult) {
                                        is Success -> BitmapFactory.decodeByteArray(imageResult.value, 0, imageResult.value.size)
                                        is Failure -> null
                                    }
                                } else {
                                    newMap[user.id]
                                }?.also {
                                    loadedImages[user.id] = it
                                }
                            }
                        }
                        deferredList.awaitAll()
                    }

                    newMap.putAll(loadedImages)
                    state = state.copy(users = usersList, imageUsers = newMap)
                }

                is Failure -> {
                    state = state.copy(users = emptyList())
                    error = result.reason
                }
            }
        }
    }
    fun allConventions(token: String) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.getAllConventions()) {
                is Success -> {
                    val conventionsList = result.value.toList()
                    val newMap = state.imageConventions.toMutableMap()
                    error = null

                    val loadedImages = mutableMapOf<Int, Bitmap?>()

                    coroutineScope {
                        val deferredList = conventionsList.map { convention ->
                            async {
                                if (!newMap.containsKey(convention.id)) {
                                    val imageResult = api.getConventionPicture(convention.id)
                                    when (imageResult) {
                                        is Success -> BitmapFactory.decodeByteArray(imageResult.value, 0, imageResult.value.size)
                                        is Failure -> null
                                    }
                                } else {
                                    newMap[convention.id]
                                }?.also {
                                    loadedImages[convention.id] = it
                                }
                            }
                        }
                        deferredList.awaitAll()
                    }

                    newMap.putAll(loadedImages)

                    state = state.copy(conventions = conventionsList, imageConventions = newMap)
                }
                is Failure -> {
                    state = state.copy(conventions = emptyList())
                    error = result.reason
                }
            }
        }
    }



    init {
        viewModelScope.launch {
            repository.token.collect { newToken ->
                state = SearchState(newToken, emptyList(), emptyList(), emptyMap(), emptyMap())
                allUsers(newToken)
                allConventions(newToken)
            }
        }
    }
}