package com.lyreon.desktop

import com.lyreon.desktop.spotify.SpotifyScraper
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpotifyScraperTest {

    @Test
    fun testFetchLargePlaylist() = runBlocking {
        val playlistId = "5TxV7h85CKFdRjG1aaNMcK"
        var progressReported = 0
        val playlist = SpotifyScraper.fetchPlaylist(playlistId) { loaded, total ->
            progressReported = loaded
            println("Progress: $loaded / $total")
        }

        println("Loaded playlist '${playlist.name}' by ${playlist.author} with ${playlist.tracks.size} tracks")
        assertEquals("Sukai", playlist.name)
        assertTrue(playlist.tracks.size > 100, "Playlist harus memiliki lebih dari 100 trek, didapat: ${playlist.tracks.size}")
        assertTrue(playlist.tracks.size >= 700, "Playlist 5TxV7h85CKFdRjG1aaNMcK seharusnya memiliki 700+ trek, didapat: ${playlist.tracks.size}")
        assertTrue(playlist.coverUrl.isNotBlank(), "Cover URL tidak boleh kosong")
        assertTrue(playlist.tracks.first().title.isNotBlank(), "Judul lagu pertama tidak boleh kosong")
        assertTrue(progressReported > 100, "Progress callback harus terpanggil melebihi 100 trek")
    }
}
