package com.example.komiconnect.screens.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.repositories.DataRepository
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.PostResponse
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.launch
import kotlin.collections.set


data class HomeState(val token: String, val posts: List<PostResponse>,  var imagePosts: Map<Int, Bitmap?>)

class HomeViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(HomeState("", emptyList(), emptyMap()))
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun allPosts(token: String) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.getAllPosts()) {
                is Success -> {
                    val newMap = state.imagePosts.toMutableMap()
                    state = state.copy(posts = result.value.toList())
                    error = null

                    for (post in state.posts) {
                        val imageResult = api.getPostPicture(post.id)
                        when (imageResult) {
                            is Success -> {
                                val bitmap = BitmapFactory.decodeByteArray(imageResult.value, 0, imageResult.value.size)
                                newMap[post.id] = bitmap
                            }
                            is Failure -> {
                                newMap[post.id] = null
                            }
                        }
                    }
                    state = state.copy(imagePosts = newMap)
                }

                is Failure -> {
                    state = state.copy(posts = emptyList())
                    error = result.reason
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.token.collect { newToken ->
                state = HomeState(newToken, emptyList(), emptyMap())
                allPosts(newToken)
            }
        }
    }
}