package com.pawside.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** App-wide light/dark appearance, user-selectable in settings. */
enum class ThemeMode(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    ;

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.firstOrNull { it.key == key } ?: SYSTEM
    }
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "pawside_settings")

/** Persists the app theme. */
class SettingsRepository(private val context: Context) {

    val themeMode: Flow<ThemeMode> =
        context.settingsDataStore.data.map { ThemeMode.fromKey(it[THEME_MODE]) }

    suspend fun setThemeMode(mode: ThemeMode) = context.settingsDataStore.edit {
        it[THEME_MODE] = mode.key
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
