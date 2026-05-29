package com.pawside.app.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A single open-source component bundled in the app. */
private data class LicenseEntry(
    val name: String,
    val copyright: String,
    val license: String,
)

private val Libraries = listOf(
    LicenseEntry("Android Jetpack / AndroidX", "Copyright The Android Open Source Project", "Apache License 2.0"),
    LicenseEntry("Jetpack Compose", "Copyright The Android Open Source Project", "Apache License 2.0"),
    LicenseEntry("Kotlin", "Copyright JetBrains s.r.o. and contributors", "Apache License 2.0"),
    LicenseEntry("kotlinx.coroutines", "Copyright JetBrains s.r.o. and contributors", "Apache License 2.0"),
    LicenseEntry("AndroidX DataStore", "Copyright The Android Open Source Project", "Apache License 2.0"),
    LicenseEntry("Coil", "Copyright Coil Contributors", "Apache License 2.0"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ライセンス情報") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Pawsideは以下のオープンソースソフトウェアを利用しています。",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            items(Libraries) { entry ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(entry.name, style = MaterialTheme.typography.titleMedium)
                        Text(entry.copyright, style = MaterialTheme.typography.bodySmall)
                        Text(entry.license, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Apache License 2.0", style = MaterialTheme.typography.titleMedium)
                        Text(
                            APACHE_2_0,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

private const val APACHE_2_0 = """Licensed under the Apache License, Version 2.0 (the "License"); you may not use these files except in compliance with the License. You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."""
