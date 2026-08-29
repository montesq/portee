package com.portee.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Decodes a downsampled bitmap for thumbnail/preview display, avoiding a full-resolution
// decode of a camera photo just to show it at a few dozen or a few hundred dp.
fun decodeSampledBitmap(context: Context, uri: Uri, reqSizePx: Int): Bitmap? {
    val resolver = context.contentResolver

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    val boundsStream = resolver.openInputStream(uri) ?: return null
    boundsStream.use { BitmapFactory.decodeStream(it, null, bounds) }

    var sampleSize = 1
    val halfHeight = bounds.outHeight / 2
    val halfWidth = bounds.outWidth / 2
    while (halfHeight / sampleSize >= reqSizePx && halfWidth / sampleSize >= reqSizePx) {
        sampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val decodeStream = resolver.openInputStream(uri) ?: return null
    return decodeStream.use { BitmapFactory.decodeStream(it, null, decodeOptions) }
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
