package com.pawside.app.ui.assets

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawside.app.data.AssetRepository
import com.pawside.app.data.DogAsset
import com.pawside.app.data.DogState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AssetImportViewModel(
    private val repository: AssetRepository,
) : ViewModel() {

    val assets: StateFlow<Map<DogState, DogAsset>> =
        repository.assets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap(),
        )

    fun onMediaPicked(state: DogState, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch { repository.import(state, uri) }
    }

    fun onClear(state: DogState) {
        viewModelScope.launch { repository.clear(state) }
    }
}
