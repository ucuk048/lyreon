package com.lyreon.desktop.core

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LyreonLog {
    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.ROOT)
    private val logFile by lazy {
        try {
            val userHome = System.getProperty("user.home") ?: "."
            val dir = File(userHome, ".lyreon").apply { mkdirs() }
            File(dir, "desktop.log")
        } catch (_: Exception) {
            null
        }
    }

    fun d(tag: String, msg: String) {
        log("DEBUG", tag, msg)
    }

    fun i(tag: String, msg: String) {
        log("INFO", tag, msg)
    }

    fun w(tag: String, msg: String, tr: Throwable? = null) {
        log("WARN", tag, msg, tr)
    }

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        log("ERROR", tag, msg, tr)
    }

    private fun log(level: String, tag: String, msg: String, tr: Throwable? = null) {
        val time = synchronized(timeFormat) { timeFormat.format(Date()) }
        val line = "[$time] [$level/$tag] $msg"
        println(line)
        tr?.printStackTrace(System.out)

        logFile?.let { file ->
            try {
                PrintWriter(FileWriter(file, true)).use { out ->
                    out.println(line)
                    tr?.printStackTrace(out)
                }
            } catch (_: Exception) {}
        }
    }
}

