package com.example.komiconnect

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.dsl.module
import android.content.Context
import com.example.komiconnect.convention.ConventionViewModel
import com.example.komiconnect.data.repositories.DataRepository
import com.example.komiconnect.post.PostViewModel
import com.example.komiconnect.screens.add.AddViewModel
import com.example.komiconnect.screens.favorites.FavoritesViewModel
import com.example.komiconnect.screens.home.HomeViewModel
import com.example.komiconnect.screens.login.LoginViewModel
import com.example.komiconnect.screens.map.MapViewModel
import com.example.komiconnect.screens.profile.ProfileViewModel
import com.example.komiconnect.screens.search.SearchViewModel
import com.example.komiconnect.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")

val appModule = module {
    single { get<Context>().dataStore }

    single { DataRepository(get()) }

    viewModel { LoginViewModel(get()) }

    viewModel { HomeViewModel(get()) }

    viewModel { SettingsViewModel(get()) }

    viewModel { ProfileViewModel(get()) }

    viewModel { ConventionViewModel(get()) }

    viewModel { SearchViewModel(get()) }

    viewModel { AddViewModel(get()) }

    viewModel { PostViewModel(get()) }

    viewModel { FavoritesViewModel(get()) }

    viewModel { MapViewModel(get()) }
}
