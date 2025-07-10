package com.example.komiconnect.screens.profile

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
import com.example.komiconnect.network.UserResponse
import com.example.komiconnect.network.UserData
import com.example.komiconnect.network.UserStats
import com.example.komiconnect.ui.Badge
import com.example.komiconnect.ui.getBadges
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import kotlinx.coroutines.launch

data class ProfileState(val token: String,  var imagePosts: Map<Int, Bitmap?>, var profilePicture: Bitmap?, var userStats: UserStats?, var badges: List<Badge>)

class ProfileViewModel(
    private val repository: DataRepository
) : ViewModel() {
    var state by mutableStateOf(ProfileState("", emptyMap(), null, null, emptyList()))
        private set
    var meResponse by mutableStateOf<UserResponse?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var saveResponse by mutableStateOf<String?>(null)
        private set
    var saveError by mutableStateOf<String?>(null)
        private set
    var postResponse by mutableStateOf<Array<PostResponse>?>(null)
        private set
    var postError by mutableStateOf<String?>(null)
        private set
    var imageResponse by mutableStateOf<Bitmap?>(null)

    fun fetchUserProfile(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = if (id == null) {
                api.me()
            } else {
                api.getUserFromId(id)
            }
            when (result) {
                is Success -> {
                    meResponse = result.value
                    error = null

                    var userStatsResult =  api.getUserStats(meResponse?.id!!)
                    when (userStatsResult) {
                        is Success -> {
                            val badges = getBadges(userStatsResult.value)
                            state = state.copy(userStats = userStatsResult.value, badges = badges)
                        }

                        is Failure -> {

                        }

                    }

                    var imageResult = api.getUserPicture(meResponse?.id!!)
                    when (imageResult) {
                        is Success -> {
                            imageResponse = BitmapFactory.decodeByteArray(
                                imageResult.value,
                                0,
                                imageResult.value.size
                            )
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

    fun fetchProfilePost(token: String, id: Int?) {
        viewModelScope.launch {
            val api = ApiService(token)
            val result = api.getPostFromProfile(id)

            when (result) {
                is Success -> {

                    val newMap = state.imagePosts.toMutableMap()
                    postResponse = result.value
                    postError = null

                    if (postResponse != null) {
                        for (post in postResponse) {
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
                    }
                    state = state.copy(imagePosts = newMap)
                }

                is Failure -> {
                    postResponse = null
                    postError = result.reason
                }
            }
        }
    }

    fun saveDataChanges(data: UserData, token: String) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.putUserData(data)) {
                is Success -> {
                    saveResponse = result.value
                    saveError = null
                    fetchUserProfile(state.token, null)
                }

                is Failure -> {
                    saveResponse = null
                    saveError = result.reason
                }

            }
        }
    }



    fun uploadPicture(token: String, id: Int, data: ByteArray) {
        viewModelScope.launch {
            val api = ApiService(token)
            when (val result = api.uploadUserPicture(id, data)) {
                is Success -> {
                    saveResponse = result.value
                    saveError = null
                    fetchUserProfile(state.token, null)
                }

                is Failure -> {
                    saveResponse = null
                    saveError = result.reason
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            viewModelScope.launch {
                repository.token.collect { newToken ->
                    state = ProfileState(newToken, emptyMap(), null, null, emptyList())

                    if (newToken.isEmpty()) {
                        meResponse = null
                        error = null
                    } else {
                        fetchUserProfile(newToken, null)
                    }
                }
            }
        }
    }
}