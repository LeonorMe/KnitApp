package com.malha.app.core.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageHelper {
    private const val TAG = "ImageStorageHelper"

    /**
     * Copies an image from any accessible Uri (e.g. Photo Picker content:// Uri)
     * into the app's private internal storage directory.
     * Returns a persistent file:// Uri string that can be safely saved to SQLite/DataStore
     * and loaded by Coil at any time across app restarts.
     */
    fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String? {
        return runCatching {
            val imagesDir = File(context.filesDir, "images").apply {
                if (!exists()) mkdirs()
            }
            val mimeType = context.contentResolver.getType(sourceUri)
            val extension = when {
                mimeType?.contains("png", ignoreCase = true) == true -> "png"
                mimeType?.contains("webp", ignoreCase = true) == true -> "webp"
                else -> "jpg"
            }
            val fileName = "img_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.$extension"
            val targetFile = File(imagesDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            } ?: run {
                Log.w(TAG, "Could not open input stream for $sourceUri")
                return null
            }

            Uri.fromFile(targetFile).toString()
        }.onFailure { e ->
            Log.e(TAG, "Failed to copy image to internal storage from $sourceUri", e)
        }.getOrNull()
    }
}
