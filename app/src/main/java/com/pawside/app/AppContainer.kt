package com.pawside.app

import android.content.Context
import com.pawside.app.data.AssetRepository
import com.pawside.app.data.SettingsRepository

/**
 * Manual dependency container. Kept simple (no DI framework) and exposed via
 * [PawsideApplication.container].
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val assetRepository: AssetRepository = AssetRepository(appContext)
    val settingsRepository: SettingsRepository = SettingsRepository(appContext)
}
