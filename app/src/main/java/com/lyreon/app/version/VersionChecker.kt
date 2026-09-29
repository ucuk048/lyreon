/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.version

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.lyreon.app.yt.LyreonHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.concurrent.TimeUnit

sealed class VersionStatus {
    data object Checking : VersionStatus()
    data object Valid : VersionStatus()
    data class Outdated(
        val currentVersion: String,
        val requiredVersion: String,
    ) : VersionStatus()
}

object VersionChecker {
    private const val TAG = "LyreonVersionChecker"
    const val VERSION_URL = "https://raw.githubusercontent.com/ucuk048/lyreon/refs/heads/main/version.txt"
    const val UPDATE_URL = "https://github.com/ucuk048/lyreon"

    fun getCurrentVersion(context: Context): String {
        return runCatching {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            pInfo.versionName ?: "0.0.0"
        }.getOrDefault("0.0.0")
    }

    suspend fun checkVersion(context: Context): VersionStatus = withContext(Dispatchers.IO) {
        val currentVersion = getCurrentVersion(context).trim()
        val client = LyreonHttp.extractClient.newBuilder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(VERSION_URL)
            .header("Cache-Control", "no-cache")
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gagal mengambil version.txt: HTTP ${response.code}")
                return@withContext VersionStatus.Valid
            }
            val remoteText = response.body.string().trim()
            if (remoteText.isBlank()) {
                return@withContext VersionStatus.Valid
            }

            Log.i(TAG, "Pemeriksaan versi: lokal='$currentVersion', remote='$remoteText'")

            // Jika versi lokal tidak sama dengan yang tertera di remote version.txt
            if (!currentVersion.equals(remoteText, ignoreCase = true)) {
                return@withContext VersionStatus.Outdated(
                    currentVersion = currentVersion,
                    requiredVersion = remoteText,
                )
            }
            return@withContext VersionStatus.Valid
        } catch (e: Exception) {
            Log.w(TAG, "Pemeriksaan versi gagal: ${e.message}")
            return@withContext VersionStatus.Valid
        }
    }
}
