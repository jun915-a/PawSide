package com.pawside.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawside.app.data.AssetRepository
import com.pawside.app.data.DogAsset
import com.pawside.app.data.DogState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val assets: Map<DogState, DogAsset> = emptyMap(),
) {
    val configuredCount: Int get() = assets.values.count { !it.isEmpty }

    /** A representative photo for the home hero: the first state that has one. */
    val heroImagePath: String? get() = assets.values.firstNotNullOfOrNull { it.imagePath }
}

class HomeViewModel(repository: AssetRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        repository.assets
            .map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState(),
            )
}
