package com.portee.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

private const val MAX_PROCESS_DIMENSION = 1600
private const val CONTRAST_FACTOR = 1.35f
private const val SHARPEN_AMOUNT = 0.25f
private const val ROW_INK_THRESHOLD = 0.006f
private const val MIN_GAP_HEIGHT_FRACTION = 0.02f
private const val MIN_SYSTEM_HEIGHT_FRACTION = 0.04f
private const val SYSTEM_PADDING_FRACTION = 0.01f

// Takes one scanned score page and turns it into one or more "system" images (one per line
// of staves), enhanced for legibility. No OMR/note recognition involved — just a horizontal
// ink-density profile to find the blank margins between systems, the same idea a document
// layout analyzer uses to find text lines. Falls back to the whole page if nothing is detected,
// so a piece never loses a page over a heuristic misfire.
fun processScorePage(context: Context, sourceUri: Uri): List<Uri> {
    val decoded = decodeSampledBitmap(context, sourceUri, MAX_PROCESS_DIMENSION) ?: return listOf(sourceUri)
    val enhanced = sharpen(enhanceContrast(decoded), SHARPEN_AMOUNT)
    val bands = detectSystemBands(enhanced)

    val dir = File(context.cacheDir, "score_systems").apply { mkdirs() }
    val uris = bands.mapIndexed { index, (startY, endY) ->
        val crop = Bitmap.createBitmap(enhanced, 0, startY, enhanced.width, endY - startY)
        val file = File(dir, "system_${System.currentTimeMillis()}_$index.jpg")
        FileOutputStream(file).use { out -> crop.compress(Bitmap.CompressFormat.JPEG, 90, out) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    return uris.ifEmpty { listOf(sourceUri) }
}

private fun enhanceContrast(bitmap: Bitmap, factor: Float = CONTRAST_FACTOR): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val translate = (1 - factor) * 128f
    val matrix = ColorMatrix(
        floatArrayOf(
            factor, 0f, 0f, 0f, translate,
            0f, factor, 0f, 0f, translate,
            0f, 0f, factor, 0f, translate,
            0f, 0f, 0f, 1f, 0f,
        ),
    )
    val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(matrix) }
    Canvas(output).drawBitmap(bitmap, 0f, 0f, paint)
    return output
}

private fun sharpen(bitmap: Bitmap, amount: Float): Bitmap {
    if (amount <= 0f) return bitmap
    val w = bitmap.width
    val h = bitmap.height
    val src = IntArray(w * h)
    bitmap.getPixels(src, 0, w, 0, 0, w, h)
    val dst = IntArray(w * h)
    val center = 1f + 4f * amount

    for (y in 0 until h) {
        for (x in 0 until w) {
            val idx = y * w + x
            dst[idx] = if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                src[idx]
            } else {
                sharpenPixel(src[idx], src[idx - w], src[idx + w], src[idx - 1], src[idx + 1], center, amount)
            }
        }
    }
    return Bitmap.createBitmap(dst, w, h, Bitmap.Config.ARGB_8888)
}

private fun sharpenPixel(c: Int, up: Int, down: Int, left: Int, right: Int, center: Float, edge: Float): Int {
    fun channel(shift: Int): Int {
        val cc = (c shr shift) and 0xFF
        val uu = (up shr shift) and 0xFF
        val dd = (down shr shift) and 0xFF
        val ll = (left shr shift) and 0xFF
        val rr = (right shr shift) and 0xFF
        val v = cc * center - edge * (uu + dd + ll + rr)
        return v.toInt().coerceIn(0, 255)
    }
    val r = channel(16)
    val g = channel(8)
    val b = channel(0)
    return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
}

private fun detectSystemBands(bitmap: Bitmap): List<Pair<Int, Int>> {
    val w = bitmap.width
    val h = bitmap.height
    val rowInk = FloatArray(h)
    val row = IntArray(w)
    for (y in 0 until h) {
        bitmap.getPixels(row, 0, w, 0, y, w, 1)
        var dark = 0
        for (x in 0 until w) {
            val p = row[x]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            val luminance = r * 0.299f + g * 0.587f + b * 0.114f
            if (luminance < 180f) dark++
        }
        rowInk[y] = dark.toFloat() / w
    }

    val minGapRows = (h * MIN_GAP_HEIGHT_FRACTION).toInt().coerceAtLeast(3)
    val bands = mutableListOf<Pair<Int, Int>>()
    var contentStart = -1
    var lastContentRow = -1
    var gapRun = 0

    for (y in 0 until h) {
        if (rowInk[y] > ROW_INK_THRESHOLD) {
            if (contentStart == -1) contentStart = y
            lastContentRow = y
            gapRun = 0
        } else if (contentStart != -1) {
            gapRun++
            if (gapRun >= minGapRows) {
                bands += contentStart to lastContentRow
                contentStart = -1
            }
        }
    }
    if (contentStart != -1) bands += contentStart to lastContentRow

    val padding = (h * SYSTEM_PADDING_FRACTION).toInt().coerceAtLeast(2)
    val minHeight = (h * MIN_SYSTEM_HEIGHT_FRACTION).toInt()

    return bands
        .map { (start, end) -> (start - padding).coerceAtLeast(0) to (end + padding).coerceAtMost(h - 1) }
        .filter { (start, end) -> end - start >= minHeight }
}
