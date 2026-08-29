package com.portee.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Decodes a downsampled bitmap capped at reqSizePx on its longer side, avoiding a
// full-resolution decode of a camera photo just to show it at a few dozen/hundred dp — or,
// for ScoreProcessor, just to bound how much memory the per-pixel contrast/sharpen/system-
// detection passes need. Camera sensors are usually landscape-native, so a portrait photo is
// stored with its pixels rotated and an EXIF orientation tag saying how to display it upright —
// BitmapFactory ignores that tag, so it must be applied manually or portrait photos come out
// sideways.
fun decodeSampledBitmap(context: Context, uri: Uri, reqSizePx: Int): Bitmap? {
    val resolver = context.contentResolver

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    val boundsStream = resolver.openInputStream(uri) ?: return null
    boundsStream.use { BitmapFactory.decodeStream(it, null, bounds) }

    var sampleSize = 1
    while (bounds.outWidth / sampleSize > reqSizePx || bounds.outHeight / sampleSize > reqSizePx) {
        sampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val decodeStream = resolver.openInputStream(uri) ?: return null
    val bitmap = decodeStream.use { BitmapFactory.decodeStream(it, null, decodeOptions) } ?: return null

    val rotationDegrees = resolver.openInputStream(uri)?.use { ExifInterface(it).rotationDegrees } ?: 0
    return if (rotationDegrees == 0) bitmap else rotateBitmap(bitmap, rotationDegrees)
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Composable
fun rememberDecodedBitmap(uriString: String, reqSizePx: Int): ImageBitmap? {
    val context = LocalContext.current
    val state = produceState<ImageBitmap?>(initialValue = null, uriString, reqSizePx) {
        value = withContext(Dispatchers.IO) {
            decodeSampledBitmap(context, Uri.parse(uriString), reqSizePx)?.asImageBitmap()
        }
    }
    return state.value
}
