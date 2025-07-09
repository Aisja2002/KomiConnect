package com.example.komiconnect.screens.settings


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.komiconnect.data.models.Theme
import com.example.komiconnect.data.models.themeFromString
import com.example.komiconnect.data.repositories.DataRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*


data class SettingsState(var token: String, var theme: Theme)

class SettingsViewModel(
    private val repository: DataRepository
) : ViewModel() {
    private val _state = MutableStateFlow<SettingsState>(SettingsState("", Theme.Sistema))
    val state: StateFlow<SettingsState> get() = _state.asStateFlow()

    fun resetToken () {
        viewModelScope.launch {
            _state.update {
                SettingsState("", it.theme)
            }
            repository.resetToken()
        }
    }

    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            _state.update {
                SettingsState(it.token, theme)
            }
            repository.setTheme(theme.toString())
        }
    }

    init {
        viewModelScope.launch {
            _state.update {
                SettingsState(repository.token.first(), themeFromString(repository.theme.first()))
            }
        }
    }
}