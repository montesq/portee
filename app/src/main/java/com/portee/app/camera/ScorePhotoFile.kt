package com.portee.app.camera

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Creates a private, app-cache file for a camera capture and returns its content:// Uri
// via FileProvider, so ACTION-less TakePicture can write the full-resolution photo into it.
fun createScorePhotoUri(context: Context): Uri {
    val dir = File(context.cacheDir, "score_photos").apply { mkdirs() }
    val file = File(dir, "score_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
