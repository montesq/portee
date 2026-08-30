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
private const val INK_THRESHOLD = 0.006f
private const val MIN_GAP_HEIGHT_FRACTION = 0.02f
private const val MIN_SYSTEM_HEIGHT_FRACTION = 0.04f
private const val CONTENT_PADDING_FRACTION = 0.01f
private const val SYSTEMS_PER_GROUP = 2

// Takes one scanned score page and turns it into one or more display images, enhanced for
// legibility. No OMR/note recognition involved — just ink-density profiles (row-wise and
// column-wise) to find the blank margins around the content and between systems, the same
// idea a document layout analyzer uses to find text lines. A single system alone is a very
// wide, short strip that leaves most of a tall phone screen empty when fit to it, so a few
// consecutive systems are grouped into one image instead of cropping every line separately.
// Falls back to the whole page if nothing is detected, so a page is never lost to a heuristic
// misfire.
fun processScorePage(context: Context, sourceUri: Uri): List<Uri> {
    val decoded = decodeSampledBitmap(context, sourceUri, MAX_PROCESS_DIMENSION) ?: return listOf(sourceUri)
    val contrasted = enhanceContrast(decoded)
    decoded.recycle()
    val enhanced = sharpen(contrasted, SHARPEN_AMOUNT)
    if (enhanced !== contrasted) contrasted.recycle()

    val (left, right) = detectContentColumns(enhanced)
    val groups = detectSystemGroups(enhanced)

    val dir = File(context.cacheDir, "score_systems").apply { mkdirs() }
    val uris = groups.mapIndexed { index, (top, bottom) ->
        val crop = Bitmap.createBitmap(enhanced, left, top, right - left, bottom - top)
        val file = File(dir, "system_${System.currentTimeMillis()}_$index.jpg")
        FileOutputStream(file).use { out -> crop.compress(Bitmap.CompressFormat.JPEG, 90, out) }
        crop.recycle()
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
    enhanced.recycle()
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

private fun luminance(pixel: Int): Float {
    val r = (pixel shr 16) and 0xFF
    val g = (pixel shr 8) and 0xFF
    val b = pixel and 0xFF
    return r * 0.299f + g * 0.587f + b * 0.114f
}

// Finds the left/right bounds of the actual printed content, trimming the blank page margins
// that a document scan still has on the sides (top/bottom margins are handled per-system below).
private fun detectContentColumns(bitmap: Bitmap): Pair<Int, Int> {
    val w = bitmap.width
    val h = bitmap.height
    val col = IntArray(h)
    val colInk = FloatArray(w)
    for (x in 0 until w) {
        bitmap.getPixels(col, 0, 1, x, 0, 1, h)
        var dark = 0
        for (y in 0 until h) {
            if (luminance(col[y]) < 180f) dark++
        }
        colInk[x] = dark.toFloat() / h
    }

    var left = 0
    while (left < w - 1 && colInk[left] <= INK_THRESHOLD) left++
    var right = w - 1
    while (right > left && colInk[right] <= INK_THRESHOLD) right--

    if (right <= left) return 0 to w

    val padding = (w * CONTENT_PADDING_FRACTION).toInt().coerceAtLeast(2)
    return (left - padding).coerceAtLeast(0) to (right + padding).coerceAtMost(w - 1)
}

// Finds individual system row-ranges via a row-wise ink profile, then groups SYSTEMS_PER_GROUP
// consecutive systems into one display image — a single system alone is too short relative to
// the page width to fill a tall phone screen once scaled to fit.
private fun detectSystemGroups(bitmap: Bitmap): List<Pair<Int, Int>> {
    val w = bitmap.width
    val h = bitmap.height
    val row = IntArray(w)
    val rowInk = FloatArray(h)
    for (y in 0 until h) {
        bitmap.getPixels(row, 0, w, 0, y, w, 1)
        var dark = 0
        for (x in 0 until w) {
            if (luminance(row[x]) < 180f) dark++
        }
        rowInk[y] = dark.toFloat() / w
    }

    val minGapRows = (h * MIN_GAP_HEIGHT_FRACTION).toInt().coerceAtLeast(3)
    val systems = mutableListOf<Pair<Int, Int>>()
    var contentStart = -1
    var lastContentRow = -1
    var gapRun = 0

    for (y in 0 until h) {
        if (rowInk[y] > INK_THRESHOLD) {
            if (contentStart == -1) contentStart = y
            lastContentRow = y
            gapRun = 0
        } else if (contentStart != -1) {
            gapRun++
            if (gapRun >= minGapRows) {
                systems += contentStart to lastContentRow
                contentStart = -1
            }
        }
    }
    if (contentStart != -1) systems += contentStart to lastContentRow
    if (systems.isEmpty()) return emptyList()

    val padding = (h * CONTENT_PADDING_FRACTION).toInt().coerceAtLeast(2)
    val minHeight = (h * MIN_SYSTEM_HEIGHT_FRACTION).toInt()

    return systems
        .chunked(SYSTEMS_PER_GROUP)
        .map { group -> group.first().first to group.last().second }
        .map { (start, end) -> (start - padding).coerceAtLeast(0) to (end + padding).coerceAtMost(h - 1) }
        .filter { (start, end) -> end - start >= minHeight }
}
