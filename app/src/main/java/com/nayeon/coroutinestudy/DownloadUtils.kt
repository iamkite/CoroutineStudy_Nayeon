package com.nayeon.coroutinestudy

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import okhttp3.ResponseBody
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.FileOutputStream

object DownloadUtils {
    @JvmStatic
    val downloadPath
        get() = if (isScopedStorage) {
            Environment.DIRECTORY_DOWNLOADS + File.separator + "coroutinestudy"
        } else {
            File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "coroutinestudy").absolutePath
        }

    @JvmStatic
    val isScopedStorage
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Environment.isExternalStorageLegacy()

    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    @JvmStatic
    fun downloadStartForScopedStorage(context: Context?, contentValues: ContentValues, fileName: String): Uri? {
        context ?: return null

        val collection = if (isScopedStorage) {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }
        val resolver = context.contentResolver
        contentValues.apply {
            put(MediaStore.Downloads.RELATIVE_PATH, downloadPath)
            put(MediaStore.Downloads.MIME_TYPE, "image/jpeg")
            put(MediaStore.Downloads.IS_PENDING, 1)
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        }
        return resolver.insert(collection, contentValues)
    }

    @WorkerThread
    @JvmStatic
    fun download(context: Context?, name: String, responseBody: ResponseBody) {
        context ?: return
        val resolver = context.contentResolver
        if (isScopedStorage) {
            val contentValues = ContentValues()
            val uri = downloadStartForScopedStorage(context, contentValues, name) ?: return
            responseBody.byteStream().use { inputStream ->
                try {
                    resolver.openFileDescriptor(uri, "w", null)
                } catch (e: Exception) {
                    return@use
                }.use { descriptor ->
                    inputStream.source().use { from ->
                        FileOutputStream(descriptor?.fileDescriptor).sink().buffer().use { to ->
                            to.writeAll(from)
                        }
                    }
                }
            }
            downloadCompleteForScopedStorage(context, uri, contentValues)
        } else {
            val downloadFile = File(downloadPath, name + ".jpeg")
            if (!downloadFile.parentFile.exists()) {
                downloadFile.parentFile.mkdirs()
            }
            responseBody.byteStream().use { inputStream ->
                try {
                    inputStream.source().use { from ->
                        downloadFile.sink().buffer().writeAll(from)
                    }
                } catch (e: Exception) {
                    return@use
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    @JvmStatic
    fun downloadCompleteForScopedStorage(context: Context, uri: Uri, contentValues: ContentValues) {
        val resolver = context.contentResolver
        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }

}
