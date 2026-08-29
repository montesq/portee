package com.portee.app.update

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionCode: Int,
    val tagName: String,
    val downloadUrl: String,
)

// Checks GitHub Releases for a newer build than the one currently installed. The repo is
// public specifically so this works without embedding any credentials in the APK.
object UpdateChecker {
    private const val RELEASES_URL = "https://api.github.com/repos/montesq/portee/releases/latest"

    fun fetchLatest(): UpdateInfo? {
        val connection = URL(RELEASES_URL).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val tag = json.getString("tag_name")
            val versionCode = tag.removePrefix("v").toIntOrNull() ?: return null

            val assets = json.getJSONArray("assets")
            var apkUrl: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.getString("name").endsWith(".apk")) {
                    apkUrl = asset.getString("browser_download_url")
                    break
                }
            }

            apkUrl?.let { UpdateInfo(versionCode, tag, it) }
        } catch (e: Exception) {
            // Best-effort background check — any network/parsing failure just means no update banner.
            null
        } finally {
            connection.disconnect()
        }
    }
}
