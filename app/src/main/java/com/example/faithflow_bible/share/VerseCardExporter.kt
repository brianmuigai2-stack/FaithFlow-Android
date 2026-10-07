package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Handles PNG export, FileProvider sharing, and MediaStore saving.
 */
object VerseCardExporter {

    private const val TAG = "VerseCardExporter"
    private const val AUTHORITY_SUFFIX = ".fileprovider"
    private const val SHARE_DIR = "verse_cards"
    private const val PICTURE_DIR = "FaithFlow"

    /**
     * Saves the bitmap as PNG to cache, returns a FileProvider content URI.
     */
    fun exportForShare(context: Context, bitmap: Bitmap): Uri? {
        return runCatching {
            val dir = File(context.cacheDir, SHARE_DIR).apply { mkdirs() }
            val file = File(dir, "card_${UUID.randomUUID()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}$AUTHORITY_SUFFIX",
                file
            )
        }.onFailure { e ->
            Log.e(TAG, "exportForShare failed", e)
        }.getOrNull()
    }

    /**
     * Saves the bitmap to Pictures/FaithFlow using MediaStore (Android 10+)
     * or fallback to legacy storage path.
     */
    fun saveToPictures(context: Context, bitmap: Bitmap): Boolean {
        return runCatching {
            val filename = "FaithFlow_${System.currentTimeMillis()}.png"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val values = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$PICTURE_DIR")
                }
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)!!.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    return true
                }
                false
            } else {
                val dir = File(
                    android.os.Environment.getDataDirectory(),
                    "Pictures/$PICTURE_DIR"
                ).apply { mkdirs() }
                val file = File(dir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                true
            }
        }.onFailure { e ->
            Log.e(TAG, "saveToPictures failed", e)
        }.getOrNull() ?: false
    }
}
