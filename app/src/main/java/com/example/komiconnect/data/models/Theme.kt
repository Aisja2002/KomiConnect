package com.example.komiconnect.data.models

enum class Theme { Chiaro, Scuro, Sistema }

fun themeFromString(value: String): Theme {
    return if (value == "Chiaro") {
        Theme.Chiaro
    } else if (value == "Scuro") {
        Theme.Scuro
    } else {
        Theme.Sistema
    }
}