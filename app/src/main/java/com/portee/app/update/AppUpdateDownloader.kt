package com.portee.app.update

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.net.URL

fun downloadApk(context: Context, url: String): File {
    val dir = File(context.cacheDir, "updates").apply { mkdirs() }
    val file = File(dir, "portee-update.apk")
    URL(url).openStream().use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file
}

fun apkInstallUri(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
