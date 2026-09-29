package com.lyreon.desktop

import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopStreamProxy
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import javafx.application.Platform
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun main() {
    println("=== TESTING JAVAFX MEDIA PLAYER WITH PROXY ===")
    Platform.startup {}

    val testTrack = LyreonTrack(
        videoId = "x3bfa3DZ8JM",
        title = "Secukupnya",
        artist = "Hindia",
        durationSec = 206L
    )

    val resolved = runBlocking {
        YouTubeDesktopRepository.resolveStream(testTrack)
    }
    println("Resolved URL: ${resolved.url.take(80)}... (bitrate: ${resolved.bitrateKbps} kbps)")

    val proxyUrl = DesktopStreamProxy.getStreamUrl(testTrack, resolved.url)
    println("Testing with PROXY URL: $proxyUrl")

    val latch = CountDownLatch(1)

    Platform.runLater {
        try {
            println("Creating Media with proxyUrl...")
            val media = Media(proxyUrl)
            media.setOnError {
                println("PROXY MEDIA ERROR: ${media.error}")
            }

            val player = MediaPlayer(media)
            player.statusProperty().addListener { _, oldStatus, newStatus ->
                println("MediaPlayer status: $oldStatus -> $newStatus")
            }
            player.setOnReady {
                println("ON READY! Duration: ${media.duration}")
                println("Tracks: ${media.tracks}")
                player.play()
            }
            player.setOnPlaying {
                println("ON PLAYING! Current time: ${player.currentTime}")
                latch.countDown()
            }
            player.setOnError {
                println("PLAYER ERROR: ${player.error}")
                player.error?.printStackTrace()
                latch.countDown()
            }
        } catch (e: Throwable) {
            println("EXCEPTION: ${e.message}")
            e.printStackTrace()
            latch.countDown()
        }
    }

    val reached = latch.await(15, TimeUnit.SECONDS)
    println("Latch reached: $reached")
    System.exit(0)
}
