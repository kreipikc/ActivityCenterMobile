package com.kreipikc.activitycenter.domain.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.core.content.ContextCompat
import com.kreipikc.activitycenter.R

object IconLoader {
    private val iconCache = LruCache<String, Drawable>(50)

    fun loadAppIcon(context: Context, packageName: String): Drawable {
        iconCache[packageName]?.let { return it }

        val icon = loadIconFromSystem(context, packageName)

        iconCache.put(packageName, icon)

        return icon
    }

    private fun loadIconFromSystem(context: Context, packageName: String): Drawable {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: Exception) {
            loadDefaultIcon(context)
        }
    }

    private fun loadDefaultIcon(context: Context): Drawable {
        return ContextCompat.getDrawable(context, R.mipmap.ic_launcher) ?: throw IllegalStateException("No default icon available")
    }

    fun clearCache() {
        iconCache.evictAll()
    }
}