package com.portee.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

// Decodes a downsampled bitmap for thumbnail display, avoiding a full-resolution
// decode of a camera photo just to show it at a few dozen dp.
fun decodeSampledBitmap(context: Context, uri: Uri, reqSizePx: Int): Bitmap? {
    val resolver = context.contentResolver
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) } ?: return null

    var sampleSize = 1
    val halfHeight = bounds.outHeight / 2
    val halfWidth = bounds.outWidth / 2
    while (halfHeight / sampleSize >= reqSizePx && halfWidth / sampleSize >= reqSizePx) {
        sampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, decodeOptions) }
}
