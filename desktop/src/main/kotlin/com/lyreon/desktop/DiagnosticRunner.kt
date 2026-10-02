package com.lyreon.desktop



import com.lyreon.desktop.model.LyreonTrack

import com.lyreon.desktop.model.SearchFilter

import com.lyreon.desktop.player.DesktopStreamProxy

import com.lyreon.desktop.yt.*

import kotlinx.coroutines.runBlocking

import okhttp3.MediaType.Companion.toMediaType

import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONObject





fun main() {

    println("=== STARTING LYREON DESKTOP DIAGNOSTIC ===")

    

    // Test if FALLBACK_KEY is valid

    val fallbackKey = "AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30"

    val testUrl = "https://www.youtube.com/youtubei/v1/search?key=$fallbackKey&prettyPrint=false"

    val testPayload = org.json.JSONObject()

        .put("context", org.json.JSONObject().put("client", org.json.JSONObject()

            .put("clientName", "WEB")

            .put("clientVersion", "2.20260818.01.00")

            .put("hl", "en")

            .put("gl", "US")

        ))

        .put("query", "Hindia")

    val testReq = okhttp3.Request.Builder()

        .url(testUrl)

        .header("User-Agent", PlayerClientLadder.WEB_UA_FIREFOX)

        .header("X-Youtube-Client-Name", "1")

        .header("X-Youtube-Client-Version", "2.20260818.01.00")

        .header("Origin", "https://www.youtube.com")

        .header("Referer", "https://www.youtube.com/")

        .post(testPayload.toString().toRequestBody("application/json".toMediaType()))





        .build()



    val fallbackResp = YouTubeDesktopRepository.httpClient.newCall(testReq).execute()

    println("FALLBACK_KEY HTTP Code: ${fallbackResp.code}")

    val fallbackBody = fallbackResp.body?.string().orEmpty()

    println("FALLBACK_KEY Body: ${fallbackBody.take(300)}")

    fallbackResp.close()





    // 2. Test Search Raw

    println("\n[2] Testing Raw InnerTube Search...")

    val testQueries = listOf("Hindia", "Bernadya")

    for (q in testQueries) {

        println("--- Query: '$q' ---")

        // Raw WEB client

        try {

            val reqWeb = InnertubeRequest.search(

                client = InnertubeRequest.Client.WEB,

                version = InnertubeConfig.webClientVersion(),

                query = q,

                params = null, // test no params first

                visitorData = InnertubeConfig.visitor()

            )

            YouTubeDesktopRepository.httpClient.newCall(reqWeb).execute().use { resp ->

                println("WEB (no params) HTTP status: ${resp.code}")

                val body = resp.body?.string().orEmpty()

                println("WEB body length: ${body.length}")

                if (!resp.isSuccessful) {

                    println("WEB error body: ${body.take(300)}")

                } else {

                    val root = JSONObject(body)

                    val contents = root.optJSONObject("contents")

                    println("WEB contents null?: ${contents == null}")

                }

            }

        } catch (e: Exception) {

        }

    }



    // 2. Test Search with SONGS filter and ALL filter

    println("\n[2] Testing InnerTube Search with filters...")



    for (f in listOf(SearchFilter.ALL, SearchFilter.SONGS)) {

        println("Testing search('Hindia', filter=$f)...")

        runBlocking {

            try {

                val results = YouTubeDesktopRepository.search("Hindia", f)

                println("search('Hindia', $f) -> ${results.size} tracks")

                results.take(2).forEach {

                    println("  Track: [${it.videoId}] ${it.title} by ${it.artist}")

                }

            } catch (e: Exception) {

                println("search error with $f: ${e.message}")

            }

        }

    }



    // 3. Test Stream Resolution & JavaFX MediaPlayer

    println("\n[3] Testing Stream Resolution & JavaFX MediaPlayer...")

    val testTrack = LyreonTrack(

        videoId = "x3bfa3DZ8JM",

        title = "Secukupnya",

        artist = "Hindia",

        durationSec = 206L

    )

    

    // Start JavaFX Platform

    try {

        javafx.application.Platform.startup {}

    } catch (_: Exception) {}



    runBlocking {

        try {

            val resolved = YouTubeDesktopRepository.resolveStream(testTrack)

            println("Resolved: key=${resolved.clientKey}, bitrate=${resolved.bitrateKbps}kbps, suffix=${resolved.suffix}")

            val proxyUrl = DesktopStreamProxy.getStreamUrl(testTrack, resolved.url)

            println("Proxy URL: $proxyUrl")



            // Test JavaFX Media with proxyUrl and media listener

            var mediaState = "WAITING"

            var mediaError: String? = null

            javafx.application.Platform.runLater {

                try {

                    println("Constructing javafx.scene.media.Media('$proxyUrl')...")

                    val media = javafx.scene.media.Media(proxyUrl)

                    media.setOnError {

                        println("MEDIA ERROR: ${media.error}")

                    }

                    val player = javafx.scene.media.MediaPlayer(media)

                    player.setOnReady {

                        println("JavaFX MediaPlayer is READY! Duration: ${media.duration}")

                        mediaState = "READY"

                        player.play()

                    }

                    player.setOnPlaying {

                        println("JavaFX MediaPlayer is PLAYING!")

                        mediaState = "PLAYING"

                    }

                    player.setOnError {

                        val err = player.error

                        println("JavaFX Proxy MediaPlayer ON_ERROR: $err, cause=${err?.cause}")

                        err?.printStackTrace()

                        err?.cause?.printStackTrace()

                        mediaError = err?.message

                        mediaState = "ERROR"

                    }

                } catch (e: Throwable) {

                    println("Exception creating Media/MediaPlayer: ${e.javaClass.name}: ${e.message}")

                    e.printStackTrace()

                    mediaError = e.message

                    mediaState = "EXCEPTION"

                }

            }



            // Also test downloading full file to temp and testing JavaFX Media with full local file

            val tempFile = java.io.File(System.getProperty("java.io.tmpdir"), "test_audio_full.m4a")

            println("\nDownloading full file to local: ${tempFile.absolutePath}...")

            val streamReq = Request.Builder().url(resolved.url).build()

            YouTubeDesktopRepository.httpClient.newCall(streamReq).execute().use { resp ->

                tempFile.writeBytes(resp.body!!.bytes())

            }

            println("Full temp file size: ${tempFile.length()} bytes")



            println("Testing JavaFX Media with FULL LOCAL FILE: ${tempFile.toURI()}...")

            var localState = "WAITING"

            javafx.application.Platform.runLater {

                try {

                    val locMedia = javafx.scene.media.Media(tempFile.toURI().toString())

                    locMedia.setOnError { println("LOC FULL MEDIA ERROR: ${locMedia.error}") }

                    val locPlayer = javafx.scene.media.MediaPlayer(locMedia)

                    locPlayer.setOnReady {

                        println("Local FULL MediaPlayer READY! Dur: ${locMedia.duration}")

                        localState = "READY"

                        locPlayer.play()

                    }

                    locPlayer.setOnPlaying {

                        println("Local FULL MediaPlayer PLAYING!")

                    }

                    locPlayer.setOnError {

                        val err = locPlayer.error

                        println("Local FULL MediaPlayer ON_ERROR: $err, cause=${err?.cause}")

                        localState = "ERROR: ${err?.message}"

                    }

                } catch (e: Throwable) {

                    println("Local FULL MediaPlayer EXCEPTION: ${e.message}")

                    localState = "EXCEPTION: ${e.message}"

                }

            }



            // Wait up to 15 seconds to observe JavaFX playback

            for (i in 1..30) {

                Thread.sleep(500)

                if (mediaState in listOf("READY", "PLAYING") && localState in listOf("READY", "PLAYING")) {

                    break

                }

            }

            println("Final JavaFX MediaState: $mediaState, Error: $mediaError")

            println("Final Local MediaState: $localState")





            // Also test direct stream URL without proxy to see if Windows Media Foundation plays it directly

            println("\nTesting JavaFX Media with DIRECT stream URL...")

            var directState = "WAITING"

            javafx.application.Platform.runLater {

                try {

                    val directMedia = javafx.scene.media.Media(resolved.url)

                    val directPlayer = javafx.scene.media.MediaPlayer(directMedia)

                    directPlayer.setOnReady {

                        println("Direct Media is READY! Duration: ${directMedia.duration}")

                        directState = "READY"

                    }

                    directPlayer.setOnError {

                        val err = directPlayer.error

                        println("Direct Media ON_ERROR: $err, cause=${err?.cause}")

                        err?.printStackTrace()

                        err?.cause?.printStackTrace()

                        directState = "ERROR: ${err?.message}"

                    }

                } catch (e: Throwable) {

                    println("Direct Media EXCEPTION: ${e.message}")

                    e.printStackTrace()

                    e.cause?.printStackTrace()

                    directState = "EXCEPTION: ${e.message}"

                }



            }

            for (i in 1..10) {

                Thread.sleep(500)

                if (directState != "WAITING") break

            }

            println("Final Direct MediaState: $directState")



        } catch (e: Exception) {

            println("Stream test error: ${e.message}")

            e.printStackTrace()

        }

    }



    println("=== DIAGNOSTIC COMPLETE ===")

    System.exit(0)

}

