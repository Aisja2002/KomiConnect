package com.example.komiconnect.ui

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import com.auth0.android.jwt.JWT
import com.example.komiconnect.R
import com.example.komiconnect.network.UserStats
import kotlinx.serialization.Serializable
import java.io.FileNotFoundException

fun TokenToID(token: String): Int? {
    var jwt: JWT = JWT(token)
    var claim: Int? = jwt.getClaim("id").asInt()
    return claim
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    val contentResolver: ContentResolver = context.contentResolver

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            Bitmap.createBitmap(BitmapFactory.decodeStream(stream))
        }
        bitmap
    }
}

fun uriToBitmap2(imageUri: Uri, contentResolver: ContentResolver): Bitmap {
    val bitmap = when {
        false -> {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }
        else -> {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }
    }
    return bitmap
}


fun saveImageToStorage(
    imageUri: Uri,
    contentResolver: ContentResolver,
    name: String = "IMG_${SystemClock.uptimeMillis()}"
): Uri {
    val bitmap = uriToBitmap2(imageUri, contentResolver)

    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    values.put(MediaStore.Images.Media.DISPLAY_NAME, name)

    val savedImageUri =
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    val outputStream = savedImageUri?.let { contentResolver.openOutputStream(it) }
        ?: throw FileNotFoundException()

    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.close()

    return savedImageUri
}

data class Badge(val title: String, val description: String, val image: Int)

fun getBadges(userStats: UserStats): List<Badge> {
    val badges = mutableListOf<Badge>()
    // total posts
    if (userStats.total_posts >= 100) {
        badges.add(Badge("Postatore seriale", "Ha postato 100 post", R.drawable.trophy))
    }
    if (userStats.total_posts >= 10) {
        badges.add(Badge("Postatore intermedio", "Ha postato 10 post", R.drawable.medal))
    }
    if (userStats.total_posts >= 5) {
        badges.add(Badge("Postatore principiante", "Ha postato 5 post", R.drawable.medal))
    }

    // tagged_conventions

    if (userStats.tagged_conventions >= 3) {
        badges.add(Badge("Coniglietto giramondo", "Ha visitato 3 fiere", R.drawable.trophy))
    } else if (userStats.total_posts >= 2) {
        badges.add(Badge("Fuori dalla tana", "Ha visitato 2 fiere", R.drawable.medal))
    }

    // events_posts

    if (userStats.events_posts >= 3) {
        badges.add(Badge("Lightstick alla mano", "Ha postato 3 eventi", R.drawable.trophy))
    }
    if (userStats.events_posts >= 2) {
        badges.add(Badge("Groupie", "Ha postato 2 eventi", R.drawable.medal))
    }
    if (userStats.events_posts >= 1) {
        badges.add(Badge("Quello è un microfono?", "Ha postato 1 evento", R.drawable.medal))
    }

    // most_likes

    if (userStats.most_likes >= 100) {
        badges.add(Badge("Idolo", "Ha ottenuto 100 like su un singolo post", R.drawable.trophy))
    }
    if (userStats.most_likes >= 10) {
        badges.add(Badge("Micro-influencer", "Ha ottenuto 10 like su un singolo post", R.drawable.medal))
    }
    if (userStats.most_likes >= 3) {
        badges.add(Badge("Bello di zia", "Ha ottenuto 3 like su un singolo post", R.drawable.medal))
    }

    return badges
}