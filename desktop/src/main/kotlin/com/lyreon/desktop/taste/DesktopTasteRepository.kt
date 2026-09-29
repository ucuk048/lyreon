package com.lyreon.desktop.taste

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.model.LyreonTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Locale

@Serializable
data class DesktopTasteProfile(
    val artists: Map<String, Float> = emptyMap(),
    val tokens: Map<String, Float> = emptyMap(),
    val genres: Map<String, Float> = emptyMap(),
    val hashtags: Map<String, Float> = emptyMap(),
    val playCounts: Map<String, Int> = emptyMap(),
    val updatedAtMs: Long = 0L,
) {
    val totalPlays: Int get() = playCounts.values.sum()
    val isRich: Boolean get() = totalPlays >= 3 || artists.isNotEmpty()

    fun topArtists(n: Int = 5): List<Pair<String, Float>> =
        artists.entries.sortedByDescending { it.value }.take(n).map { it.key to it.value }

    fun topGenres(n: Int = 4): List<Pair<String, Float>> =
        genres.entries.sortedByDescending { it.value }.take(n).map { it.key to it.value }

    fun topTokens(n: Int = 6): List<String> =
        tokens.entries.sortedByDescending { it.value }.take(n).map { it.key }

    fun personalityChips(n: Int = 6): List<String> {
        val out = LinkedHashSet<String>()
        hashtags.entries.sortedByDescending { it.value }.take(n).forEach { out += "#${it.key}" }
        tokens.entries.sortedByDescending { it.value }.take(n).forEach { out += it.key }
        return out.take(n)
    }
}

object DesktopTasteRepository {

    private const val TAG = "DesktopTasteRepository"
    private val scope = CoroutineScope(Dispatchers.IO)

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val appDir: File by lazy {
        val userHome = System.getProperty("user.home") ?: "."
        File(userHome, ".lyreon").apply { mkdirs() }
    }

    private val tasteFile: File by lazy {
        File(appDir, "taste.json")
    }

    private val _profile = MutableStateFlow(DesktopTasteProfile())
    val profile: StateFlow<DesktopTasteProfile> = _profile.asStateFlow()

    init {
        loadTaste()
    }

    private fun loadTaste() {
        try {
            if (tasteFile.exists() && tasteFile.length() > 0) {
                val raw = tasteFile.readText(Charsets.UTF_8)
                _profile.value = json.decodeFromString<DesktopTasteProfile>(raw)
            }
        } catch (e: Exception) {
            LyreonLog.e(TAG, "Gagal memuat profil selera: ${e.message}")
        }
    }

    private fun persist() {
        scope.launch {
            try {
                val text = json.encodeToString(_profile.value)
                tasteFile.writeText(text, Charsets.UTF_8)
            } catch (e: Exception) {
                LyreonLog.e(TAG, "Gagal menyimpan selera: ${e.message}")
            }
        }
    }

    fun recordPlay(track: LyreonTrack, completionRatio: Float = 1.0f) {
        val w = 0.25f + 2.75f * completionRatio.coerceIn(0f, 1f)
        val p = _profile.value
        val artist = MusicTextAnalyzer.cleanArtist(track.artist).lowercase(Locale.ROOT)
        val mods = MusicTextAnalyzer.modifiersOf(track.title)
        val gens = MusicTextAnalyzer.genresOf("${track.title} ${track.artist}")
        val plays = p.playCounts[track.videoId] ?: 0

        _profile.value = p.copy(
            artists = bump(p.artists, artist, w),
            tokens = bumpAll(p.tokens, mods, w * 0.9f),
            genres = bumpAll(p.genres, gens, w * 0.7f),
            playCounts = p.playCounts + (track.videoId to plays + 1),
            updatedAtMs = System.currentTimeMillis()
        )
        persist()
    }

    fun recordLike(track: LyreonTrack) {
        val p = _profile.value
        val artist = MusicTextAnalyzer.cleanArtist(track.artist).lowercase(Locale.ROOT)
        val mods = MusicTextAnalyzer.modifiersOf(track.title)
        val gens = MusicTextAnalyzer.genresOf("${track.title} ${track.artist}")

        _profile.value = p.copy(
            artists = bump(p.artists, artist, 4.0f),
            tokens = bumpAll(p.tokens, mods, 3.0f),
            genres = bumpAll(p.genres, gens, 2.5f),
            updatedAtMs = System.currentTimeMillis()
        )
        persist()
    }

    private fun bump(map: Map<String, Float>, key: String, weight: Float): Map<String, Float> {
        if (key.isBlank()) return map
        return map + (key to ((map[key] ?: 0f) + weight))
    }

    private fun bumpAll(map: Map<String, Float>, keys: List<String>, weight: Float): Map<String, Float> {
        var m = map
        keys.forEach { m = bump(m, it, weight) }
        return m
    }
}
