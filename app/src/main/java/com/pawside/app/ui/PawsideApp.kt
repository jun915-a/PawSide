package com.pawside.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pawside.app.PawsideApplication
import com.pawside.app.ui.about.LicensesScreen
import com.pawside.app.ui.assets.AssetImportScreen
import com.pawside.app.ui.assets.AssetImportViewModel
import com.pawside.app.ui.home.HomeScreen
import com.pawside.app.ui.home.HomeViewModel
import com.pawside.app.ui.settings.SettingsScreen
import com.pawside.app.ui.settings.SettingsViewModel

/** Top-level navigation destinations. */
object Routes {
    const val HOME = "home"
    const val ASSET_IMPORT = "asset_import"
    const val SETTINGS = "settings"
    const val LICENSES = "licenses"
}

@Composable
fun PawsideApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(factory = PawsideViewModelFactory)
            HomeScreen(
                viewModel = vm,
                onOpenAssetImport = { navController.navigate(Routes.ASSET_IMPORT) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.ASSET_IMPORT) {
            val vm: AssetImportViewModel = viewModel(factory = PawsideViewModelFactory)
            AssetImportScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.SETTINGS) {
            val vm: SettingsViewModel = viewModel(factory = PawsideViewModelFactory)
            SettingsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onOpenLicenses = { navController.navigate(Routes.LICENSES) },
            )
        }
        composable(Routes.LICENSES) {
            LicensesScreen(onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Builds view models with the app-wide [com.pawside.app.data.AssetRepository]
 * pulled from [PawsideApplication.container].
 */
val PawsideViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
    initializer { HomeViewModel(app().container.assetRepository) }
    initializer { AssetImportViewModel(app().container.assetRepository) }
    initializer {
        SettingsViewModel(settingsRepository = app().container.settingsRepository)
    }
}

private fun CreationExtras.app(): PawsideApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PawsideApplication
