package com.grocery.manager.data.local

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageUtils {

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return ""

        val fileName = "product_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    fun deleteImageFromInternalStorage(imagePath: String) {
        if (imagePath.isNotEmpty()) {
            File(imagePath).delete()
        }
    }
}