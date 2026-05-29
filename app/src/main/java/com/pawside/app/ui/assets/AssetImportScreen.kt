package com.pawside.app.ui.assets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pawside.app.data.DogAsset
import com.pawside.app.data.DogState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetImportScreen(
    viewModel: AssetImportViewModel,
    onBack: () -> Unit,
) {
    val assets by viewModel.assets.collectAsStateWithLifecycle()

    // The photo picker reports its result asynchronously, so remember which
    // state slot is currently being filled across the launch/callback hop.
    var pendingState by remember { mutableStateOf<DogState?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        pendingState?.let { viewModel.onMediaPicked(it, uri) }
        pendingState = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ペットの素材を登録") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(DogState.entries.toList(), key = { it.key }) { state ->
                StateCard(
                    state = state,
                    asset = assets[state] ?: DogAsset(state),
                    onPick = {
                        pendingState = state
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                    onClear = { viewModel.onClear(state) },
                )
            }
        }
    }
}

@Composable
private fun StateCard(
    state: DogState,
    asset: DogAsset,
    onPick: () -> Unit,
    onClear: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(state.displayLabel, style = MaterialTheme.typography.titleLarge)
            Text(state.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                val path = asset.imagePath
                if (path != null) {
                    AsyncImage(
                        // Key on the file path; since each import gets a unique
                        // path the cache never serves a stale thumbnail.
                        model = ImageRequest.Builder(context)
                            .data(File(path))
                            .build(),
                        contentDescription = "${state.displayLabel} の写真",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        Icons.Filled.Image,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onPick, modifier = Modifier.fillMaxWidth()) {
                Text(if (asset.hasImage) "写真を変更" else "写真を選ぶ")
            }
            if (asset.hasImage) {
                TextButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) {
                    Text("削除")
                }
            }
        }
    }
}
