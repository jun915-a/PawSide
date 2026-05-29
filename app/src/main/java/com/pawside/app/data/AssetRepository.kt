package com.pawside.app.data

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

/** Resolved photo assigned to a single [DogState]. The path points into internal storage. */
data class DogAsset(
    val state: DogState,
    val imagePath: String? = null,
) {
    val hasImage: Boolean get() = imagePath != null
    val isEmpty: Boolean get() = imagePath == null
}

private val Context.assetDataStore: DataStore<Preferences> by preferencesDataStore(name = "pawside_assets")

/**
 * Owns the user-imported pet photos. Files picked from the gallery are copied
 * into the app's internal storage (so they survive even if the source is
 * deleted) and their paths are persisted in DataStore, keyed by [DogState].
 *
 * Each import uses a fresh, timestamped filename and deletes the previous one,
 * so re-picking a photo changes the stored path — which makes the assets flow
 * re-emit and image loaders reload instead of serving a stale cached thumbnail.
 */
class AssetRepository(private val context: Context) {

    private val assetsDir: File by lazy {
        File(context.filesDir, "dog_assets").apply { mkdirs() }
    }

    /** Emits the current photo assignment for every [DogState]. */
    val assets: Flow<Map<DogState, DogAsset>> =
        context.assetDataStore.data.map { prefs ->
            DogState.entries.associateWith { state ->
                DogAsset(
                    state = state,
                    imagePath = prefs[imageKey(state)]?.takeIf { File(it).exists() },
                )
            }
        }

    /** Copies [source] into internal storage and assigns it to [state]. */
    suspend fun import(state: DogState, source: Uri) = withContext(Dispatchers.IO) {
        val extension = mimeExtension(source)
        val target = File(assetsDir, "${state.key}_${System.currentTimeMillis()}.$extension")

        context.contentResolver.openInputStream(source)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: error("素材を読み込めませんでした: $source")

        context.assetDataStore.edit { prefs ->
            // Remove the previously stored file before re-pointing.
            prefs[imageKey(state)]?.let { old ->
                if (old != target.absolutePath) File(old).delete()
            }
            prefs[imageKey(state)] = target.absolutePath
        }
    }

    /** Clears the photo assigned to [state] and deletes the backing file. */
    suspend fun clear(state: DogState) = withContext(Dispatchers.IO) {
        context.assetDataStore.edit { prefs ->
            prefs[imageKey(state)]?.let { File(it).delete() }
            prefs.remove(imageKey(state))
        }
    }

    private fun mimeExtension(uri: Uri): String =
        when (context.contentResolver.getType(uri)) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }

    private companion object {
        fun imageKey(state: DogState) = stringPreferencesKey("${state.key}_image")
    }
}
