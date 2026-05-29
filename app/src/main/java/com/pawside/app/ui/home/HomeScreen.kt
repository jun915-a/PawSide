package com.pawside.app.ui.home

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pawside.app.widget.PawsideWidgetProvider
import java.io.File

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenAssetImport: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Pawside",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "あなたのペットが、スマホの中からそばで見守ります。",
            style = MaterialTheme.typography.bodyMedium,
        )

        WelcomeHero(heroImagePath = state.heroImagePath)

        // This app revolves around its home-screen widget, so make adding it the
        // most prominent action.
        WidgetCard(onAddWidget = { requestPinWidget(context) })

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("素材の設定状況", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${state.configuredCount} / ${state.assets.size} ステートに素材を登録済み",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Button(onClick = onOpenAssetImport, modifier = Modifier.fillMaxWidth()) {
            Text("ペットの写真を登録する")
        }
        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
            Text("設定")
        }
    }
}

@Composable
private fun WidgetCard(onAddWidget: () -> Unit) {
    val context = LocalContext.current
    val supported = remember {
        (context.getSystemService(AppWidgetManager::class.java))?.isRequestPinAppWidgetSupported == true
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("ホーム画面ウィジェット", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Pawsideはウィジェットが主役です。ホーム画面に置くと、スマホの状態をペットが教えてくれます。",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(12.dp))
            if (supported) {
                Button(onClick = onAddWidget, modifier = Modifier.fillMaxWidth()) {
                    Text("ホーム画面にウィジェットを追加")
                }
            } else {
                Text(
                    text = "お使いのホームアプリでは自動追加に対応していません。ホーム画面を長押し→「ウィジェット」からPawsideを追加してください。",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun WelcomeHero(heroImagePath: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.4f)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        if (heroImagePath != null) {
            AsyncImage(
                model = File(heroImagePath),
                contentDescription = "ペットの写真",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = "ペットの写真を登録すると\nここに表示されます",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

/** Asks the launcher to pin the Pawside widget to the home screen (API 26+). */
private fun requestPinWidget(context: Context) {
    val manager = context.getSystemService(AppWidgetManager::class.java) ?: return
    if (!manager.isRequestPinAppWidgetSupported) return
    manager.requestPinAppWidget(
        ComponentName(context, PawsideWidgetProvider::class.java),
        null,
        null,
    )
}
