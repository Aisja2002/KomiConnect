package com.example.komiconnect.post

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.repositories.DataRepository
import com.example.komiconnect.network.ApiService
import com.example.komiconnect.network.ConventionResponse
import com.example.komiconnect.network.PostResponse
import com.example.komiconnect.network.UserResponse
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

data class PostState(val token: String, val favorites: List<Int>, val likes: List<Int>)


class PostViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(PostState("", emptyList(), emptyList()))
        private set
    var postResponse by mutableStateOf<PostResponse?>(null)
        private set
    var postError by mutableStateOf<String?>(null)
        private set
    var userResponse by mutableStateOf<UserResponse?>(null)
        private set
    var userError by mutableStateOf<String?>(null)
        private set
    var conventionResponse by mutableStateOf<ConventionResponse?>(null)
        private set
    var conventionError by mutableStateOf<String?>(null)
        private set
    var favoriteResponse by mutableStateOf<String?>(null)
        private set
    var favoriteError by mutableStateOf<String?>(null)
        private set
    var unfavoriteResponse by mutableStateOf<String?>(null)
        private set
    var unfavoriteError by mutableStateOf<String?>(null)
        private set
    var likeResponse by mutableStateOf<String?>(null)
        private set
    var likeError by mutableStateOf<String?>(null)
        private set
    var unlikeResponse by mutableStateOf<String?>(null)
        private set
    var unlikeError by mutableStateOf<String?>(null)
        private set
    var fetchLikeResponse by mutableStateOf<String?>(null)
        private set
    var fetchLikeError by mutableStateOf<String?>(null)
        private set
    var imageResponse by mutableStateOf<Bitmap?>(null)
    var userImageResponse by mutableStateOf<Bitmap?>(null)
    var deleteResponse by mutableStateOf<String?>(null)
        private set
    var deleteError by mutableStateOf<String?>(null)
        private set


    fun fetchPostProfile(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.getPostFromId(id)
            when (result) {
                is Success -> {
                    postResponse = result.value
                    postError = null
                }

                is Failure -> {
                    postResponse = null
                    postError = result.reason
                }
            }
            if (id!= null) {
                var imageResult = api.getPostPicture(id)
                when (imageResult) {
                    is Success -> {
                        imageResponse = BitmapFactory.decodeByteArray(
                            imageResult.value,
                            0,
                            imageResult.value.size
                        )
                    }

                    is Failure -> {
                        imageResponse = null
                    }
                }
            }
        }
    }

    fun fetchUserProfile(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.getUserFromId(id ?: 0)
            when (result) {
                is Success -> {
                    userResponse = result.value
                    userError = null

                    if (id!= null) {
                        var imageResult = api.getUserPicture(id)
                        when (imageResult) {
                            is Success -> {
                                userImageResponse = BitmapFactory.decodeByteArray(
                                    imageResult.value,
                                    0,
                                    imageResult.value.size
                                )
                            }

                            is Failure -> {
                                userImageResponse = null
                            }
                        }
                    }


                }

                is Failure -> {
                    userResponse = null
                    userError = result.reason
                }
            }
        }
    }

    fun fetchConvention(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.getConventionFromId(id ?: 0)
            when (result) {
                is Success -> {
                    conventionResponse = result.value
                    conventionError = null
                }

                is Failure -> {
                    conventionResponse = null
                    conventionError = result.reason
                }
            }
        }
    }

    fun addFavorite(token: String, id: Int?, completableDeferred: CompletableDeferred<Unit>?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.addFavorite(id)
            when (result) {
                is Success -> {
                    favoriteResponse = result.value
                    favoriteError = null
                }

                is Failure -> {
                    favoriteResponse = null
                    favoriteError = result.reason
                }
            }
            completableDeferred?.complete(Unit)
        }
    }


    fun deleteFavorite(token: String, id: Int?, completableDeferred: CompletableDeferred<Unit>?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.deleteFavorite(id)
            when (result) {
                is Success -> {
                    unfavoriteResponse = result.value
                    unfavoriteError = null
                }

                is Failure -> {
                    unfavoriteResponse = null
                    unfavoriteError = result.reason
                }
            }
            completableDeferred?.complete(Unit)
        }
    }

    fun fetchFavorites(token: String, completableDeferred: CompletableDeferred<Unit>?) {
        viewModelScope.launch {
            completableDeferred?.await()
            val api = ApiService(token)
            when (val result = api.getAllFavorites()) {
                is Success -> {
                    state = state.copy(favorites = result.value.toList())
                    unfavoriteError = null
                }

                is Failure -> {
                    state = state.copy(favorites = emptyList())
                    unfavoriteError = result.reason
                }
            }
        }
    }


    fun addLike(token: String, id: Int?, completableDeferred: CompletableDeferred<Unit>?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.addLike(id)
            when (result) {
                is Success -> {
                    likeResponse = result.value
                    likeError = null
                }

                is Failure -> {
                    likeResponse = null
                    likeError = result.reason
                }
            }
            completableDeferred?.complete(Unit)
        }
    }


    fun deleteLike(token: String, id: Int?, completableDeferred: CompletableDeferred<Unit>?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.deleteLike(id)
            when (result) {
                is Success -> {
                    unlikeResponse = result.value
                    unlikeError = null
                }

                is Failure -> {
                    unlikeResponse = null
                    unlikeError = result.reason
                }
            }
            completableDeferred?.complete(Unit)
        }
    }


    fun fetchLikes(token: String, id: Int?, completableDeferred: CompletableDeferred<Unit>?) {
        viewModelScope.launch {
            completableDeferred?.await()
            val api = ApiService(token)
            when (val result = api.getAllLikes(id)) {
                is Success -> {
                    state = state.copy(likes = result.value.toList())
                    fetchLikeError = null
                }

                is Failure -> {
                    state = state.copy(likes = emptyList())
                    fetchLikeError = result.reason
                }
            }
        }
    }

    fun deletePost(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.deletePost(id)
            when (result) {
                is Success -> {
                    unlikeResponse = result.value
                    unlikeError = null
                }

                is Failure -> {
                    unlikeResponse = null
                    unlikeError = result.reason
                }
            }
        }
    }


    init {
            viewModelScope.launch {
                repository.token.collect { newToken ->
                    state = PostState(newToken, emptyList(),  emptyList())
                    if (newToken.isEmpty()) {
                        postResponse = null
                        postError = null
                        userResponse = null
                        userError = null
                        conventionResponse = null
                        conventionError = null
                        favoriteResponse = null
                        favoriteError = null
                        unfavoriteResponse = null
                        unfavoriteError = null
                        likeResponse = null
                        likeError = null
                        unlikeResponse = null
                        unlikeError = null
                        fetchLikeResponse = null
                        fetchLikeError = null
                        imageResponse = null
                    } else {
                        fetchPostProfile(newToken, null)
                        fetchUserProfile(newToken, null)
                        fetchConvention(newToken, null)
                        fetchFavorites(newToken, null)
                        fetchLikes(newToken, null, null)
                    }
                }
            }
        }
}