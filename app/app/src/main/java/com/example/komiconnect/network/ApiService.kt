package com.example.komiconnect.network

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import io.ktor.client.plugins.auth.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.auth.providers.*
import dev.forkhandles.result4k.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

@Serializable
data class UserData(var location: String? = null, var bio: String? = null)

@Serializable
data class UserResponse(val id: Int, val username: String, val admin: Int, var data: UserData)

@Serializable
data class ConventionCoordinates( val latitude: Double? = null, val longitude: Double ?= null)

@Serializable
data class ConventionData(val name: String? = null, val start : String? = null, val end: String ?= null, val location: String ?= null, val website: String ?= null, val coordinates: ConventionCoordinates? = null)

@Serializable
data class ConventionResponse (val id: Int, val data: ConventionData)

@Serializable
data class PutUserRequest(val data: UserData)

@Serializable
data class PostData(var title: String? = null, var description: String? = null)

@Serializable
data class PostRequest(val convention: Int, val label: String, val data: PostData)

@Serializable
data class PostResponse(val id: Int, val user: Int, val convention: Int, val label: String, val data: PostData)

@Serializable
data class PostCreationResponse(val id: Int)

@Serializable
data class UserStats(val total_posts: Int, val tagged_conventions: Int, val events_posts: Int, val most_likes: Int)


class ApiService(val token: String, val client: HttpClient = HttpClient(CIO){
    install(ContentNegotiation) {
        json()
    }
    install(Auth) {
        bearer {
            loadTokens {
                BearerTokens(token, null)
            }
        }
    }
}){
    suspend fun me(): Result<UserResponse, String> {
        try {
            val response = client.get("$BASE_URL/me") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body<UserResponse>())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun putUserData(userData: UserData): Result<String, String> {
        try {
            val response = client.put("$BASE_URL/user") {
                contentType(ContentType.Application.Json)
                setBody(PutUserRequest(userData))
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.bodyAsText())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getAllUsers() : Result<Array<UserResponse>, String> {
        try {
            val response = client.get("$BASE_URL/user") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }


    suspend fun getUserFromId(id : Int): Result<UserResponse, String> {
        try {
            val response = client.get("$BASE_URL/user/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body<UserResponse>())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getAllConventions() : Result<Array<ConventionResponse>, String> {
        try {
            val response = client.get("$BASE_URL/convention") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getConventionFromId(id : Int): Result<ConventionResponse, String> {
        try {
            val response = client.get("$BASE_URL/convention/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body<ConventionResponse>())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }


    suspend fun addPost(post: PostRequest): Result<PostCreationResponse, String> {
        try {
            val response = client.post("$BASE_URL/post") {
                contentType(ContentType.Application.Json)
                setBody(post)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }


suspend fun deletePost(id: Int?): Result<String, String> {
    try {
        val response = client.delete("$BASE_URL/post/$id") {
            contentType(ContentType.Application.Json)
        }
        val status = response.status.isSuccess()
        return if (status) {
            Success(response.bodyAsText())
        } else {
            Failure(response.bodyAsText())
        }
    } catch(e: Exception) {
        return Failure(e.message ?: "Unknown error")
    }
}
    suspend fun getAllPosts() : Result<Array<PostResponse>, String> {
        try {
            val response = client.get("$BASE_URL/post") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getPostFromProfile(id: Int?): Result<Array<PostResponse>, String> {
        try {
            val response = client.get("$BASE_URL/post-user/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }


    suspend fun getPostFromId(id : Int?): Result<PostResponse, String> {
        try {
            val response = client.get("$BASE_URL/post/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body<PostResponse>())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun addFavorite(id: Int?): Result<String, String> {
        try {
            val response = client.post("$BASE_URL/favorite/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.bodyAsText())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteFavorite(id: Int?): Result<String, String> {
        try {
            val response = client.delete("$BASE_URL/favorite/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.bodyAsText())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getAllFavorites(): Result<Array<Int>, String> {
        try {
            val response = client.get("$BASE_URL/favorite/user") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                val r: List<Int> = response.body()
                Success(r.toTypedArray())
            } else {
                val r = response.bodyAsText()
                Failure(r)
            }
        } catch(e: Exception) {
            val r = e.message ?: "Unknown error"
            return Failure(r)
        }
    }


    suspend fun addLike(id: Int?): Result<String, String> {
        try {
            val response = client.post("$BASE_URL/like/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.bodyAsText())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteLike(id: Int?): Result<String, String> {
        try {
            val response = client.delete("$BASE_URL/like/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.bodyAsText())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getAllLikes(id: Int?): Result<Array<Int>, String> {
        try {
            val response = client.get("$BASE_URL/like/post/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                val r: List<Int> = response.body()
                Success(r.toTypedArray())
            } else {
                val r = response.bodyAsText()
                Failure(r)
            }
        } catch(e: Exception) {
            val r = e.message ?: "Unknown error"
            return Failure(r)
        }
    }

    suspend fun getFavoritesFromUser(): Result<Array<PostResponse>, String> {
        try {
            val response = client.get("$BASE_URL/favorite/user/post") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                val r: List<PostResponse> = response.body()
                Success(r.toTypedArray())
            } else {
                val r = response.bodyAsText()
                Failure(r)
            }
        } catch(e: Exception) {
            val r = e.message ?: "Unknown error"
            return Failure(r)
        }
    }

    suspend fun getConventionPicture(id: Int): Result<ByteArray, String> {
        try {
            val response = client.get("$BASE_URL/picture/convention/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getUserPicture(id: Int): Result<ByteArray, String> {
        try {
            val response = client.get("$BASE_URL/picture/user/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getPostPicture(id: Int): Result<ByteArray, String> {
        try {
            val response = client.get("$BASE_URL/picture/post/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun uploadUserPicture(id: Int, data: ByteArray): Result<String, String> {
        try {
            val response = client.post("$BASE_URL/picture/user/$id") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("\"file\"", data, Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"picture.jpg\"")
                                append(HttpHeaders.ContentType, "image/jpeg")
                            })
                        }
                    )
                )
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun uploadPostPicture(id: Int, data: ByteArray): Result<String, String> {
        try {
            val response = client.post("$BASE_URL/picture/post/$id") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("\"file\"", data, Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"picture.jpg\"")
                                append(HttpHeaders.ContentType, "image/jpeg")
                            })
                        }
                    )
                )
            }
            val status = response.status.isSuccess()
            return if (status) {
                Success(response.body())
            } else {
                Failure(response.bodyAsText())
            }
        } catch(e: Exception) {
            return Failure(e.message ?: "Unknown error")
        }
    }

    suspend fun getUserStats(id: Int): Result<UserStats, String> {
        try {
            val response = client.get("$BASE_URL/stats/user/$id") {
                contentType(ContentType.Application.Json)
            }
            val status = response.status.isSuccess()
            return if (status) {
                val r: UserStats = response.body()
                Success(r)
            } else {
                val r = response.bodyAsText()
                Failure(r)
            }
        } catch(e: Exception) {
            val r = e.message ?: "Unknown error"
            return Failure(r)
        }
    }
}