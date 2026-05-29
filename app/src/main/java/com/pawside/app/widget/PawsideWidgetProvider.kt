package com.pawside.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.RemoteViews
import com.pawside.app.MainActivity
import com.pawside.app.R
import com.pawside.app.data.AssetRepository
import com.pawside.app.data.DogState
import com.pawside.app.data.StatusEvaluator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Home-screen widget that shows the pet reacting to live device status
 * (battery / charging / memory). Updates are driven by:
 *  - the periodic `updatePeriodMillis` from the widget info XML,
 *  - power connected/disconnected system broadcasts (instant charging changes),
 *  - the widget being resized.
 *
 * Tapping the widget opens the app.
 */
class PawsideWidgetProvider : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pendingResult = goAsync()
        scope.launch {
            try {
                val appContext = context.applicationContext
                val state = StatusEvaluator.evaluate(context)
                val imagePath = AssetRepository(appContext).assets.first()[state]?.imagePath
                appWidgetIds.forEach { id ->
                    appWidgetManager.updateAppWidget(id, buildViews(context, state, imagePath))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        // Redraw when the user resizes the widget so the photo refits the new size.
        onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            -> requestUpdateAll(context)
        }
    }

    private fun requestUpdateAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, PawsideWidgetProvider::class.java))
        if (ids.isNotEmpty()) onUpdate(context, manager, ids)
    }

    private fun buildViews(context: Context, state: DogState, imagePath: String?): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_pawside)
        views.setTextViewText(R.id.widget_label, state.widgetLabel)

        val bitmap = imagePath?.let { decodeSampled(it, MAX_IMAGE_PX) }
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.widget_image, bitmap)
            views.setViewVisibility(R.id.widget_placeholder, android.view.View.GONE)
            views.setViewVisibility(R.id.widget_image, android.view.View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_image, android.view.View.GONE)
            views.setViewVisibility(R.id.widget_placeholder, android.view.View.VISIBLE)
        }

        views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent(context))
        return views
    }

    /** Decodes [path] downsampled so the bitmap stays within the RemoteViews size budget. */
    private fun decodeSampled(path: String, reqSize: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        val longestEdge = maxOf(bounds.outWidth, bounds.outHeight)
        while (longestEdge / sample > reqSize) sample *= 2

        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        return BitmapFactory.decodeFile(path, options)
    }

    private companion object {
        const val MAX_IMAGE_PX = 512

        /** Shared tap action: opens the Pawside app. */
        fun openAppPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
