package com.pawside.app

import android.app.Application

/**
 * Application entry point. Holds process-wide singletons (asset repository, etc.)
 * that the rest of the app reaches through [container].
 */
class PawsideApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
