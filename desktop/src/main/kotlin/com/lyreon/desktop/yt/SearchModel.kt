package com.lyreon.desktop.yt

import com.lyreon.desktop.model.LyreonTrack
import org.json.JSONArray
import org.json.JSONObject

data class YtArtist(
    val name: String,
    val browseId: String,
    val thumbUrl: String = "",
    val subtitle: String = "",
    val radioPlaylistId: String = ""
)

data class YtAlbum(
    val title: String,
    val browseId: String,
    val artist: String = "",
    val thumbUrl: String = "",
    val subtitle: String = ""
)

data class YtSearchPlaylist(
    val title: String,
    val playlistId: String,
    val author: String = "",
    val thumbUrl: String = "",
    val subtitle: String = ""
)

data class MusicSearchSummary(
    val songs: List<LyreonTrack> = emptyList(),
    val artists: List<YtArtist> = emptyList(),
    val albums: List<YtAlbum> = emptyList(),
    val playlists: List<YtSearchPlaylist> = emptyList()
) {
    val isEmpty: Boolean get() = songs.isEmpty() && artists.isEmpty() && albums.isEmpty() && playlists.isEmpty()
}

internal object MusicSearchParser {
    private const val MAX_PER_KIND = 24

    fun parse(root: JSONObject): MusicSearchSummary {
        val songs = ArrayList<LyreonTrack>()
        val artists = ArrayList<YtArtist>()
        val albums = ArrayList<YtAlbum>()
        val playlists = ArrayList<YtSearchPlaylist>()

        val seenArtists = HashSet<String>()
        val seenAlbums = HashSet<String>()
        val seenSongs = HashSet<String>()
        val seenPlaylists = HashSet<String>()

        forEachRenderer(root) { renderer ->
            val item = BrowseParser.parseRow(renderer) ?: return@forEachRenderer
            if (artists.size >= MAX_PER_KIND && albums.size >= MAX_PER_KIND && songs.size >= 48 && playlists.size >= MAX_PER_KIND) {
                return@forEachRenderer
            }
            addItem(seenArtists, artists, seenAlbums, albums, seenPlaylists, playlists, seenSongs, songs, item)
        }

        forEachCard(root) { card ->
            val item = BrowseParser.parseCard(card) ?: return@forEachCard
            addItem(seenArtists, artists, seenAlbums, albums, seenPlaylists, playlists, seenSongs, songs, item)
        }

        forEachTopCard(root) { card ->
            addItem(seenArtists, artists, seenAlbums, albums, seenPlaylists, playlists, seenSongs, songs, topCardItem(card))
        }

        return MusicSearchSummary(
            songs = songs,
            artists = artists.take(MAX_PER_KIND),
            albums = albums.take(MAX_PER_KIND),
            playlists = playlists.take(MAX_PER_KIND)
        )
    }

    private fun forEachRenderer(root: JSONObject, visit: (JSONObject) -> Unit) {
        val contents = searchResults(root) ?: return
        for (s in 0 until contents.length()) {
            val node = contents.optJSONObject(s) ?: continue
            val rows = node.optJSONObject("musicShelfRenderer")?.optJSONArray("contents")
                ?: node.optJSONObject("itemSectionRenderer")?.optJSONArray("contents")
                ?: continue
            for (i in 0 until rows.length()) {
                val renderer = rows.optJSONObject(i)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                visit(renderer)
            }
        }
    }

    private fun forEachTopCard(root: JSONObject, visit: (JSONObject) -> Unit) {
        val contents = searchResults(root) ?: return
        for (s in 0 until contents.length()) {
            val card = contents.optJSONObject(s)?.optJSONObject("musicCardShelfRenderer") ?: continue
            visit(card)
        }
    }

    private fun forEachCard(root: JSONObject, visit: (JSONObject) -> Unit) {
        val contents = searchResults(root) ?: return
        for (s in 0 until contents.length()) {
            val node = contents.optJSONObject(s) ?: continue
            val shelf = node.optJSONObject("musicCarouselShelfRenderer")
                ?: node.optJSONObject("musicImmersiveCarouselShelfRenderer")
            val cards = shelf?.optJSONArray("contents")
                ?: node.optJSONObject("musicShelfRenderer")?.optJSONArray("contents")
                ?: continue
            for (i in 0 until cards.length()) {
                val card = cards.optJSONObject(i)?.optJSONObject("musicTwoRowItemRenderer") ?: continue
                visit(card)
            }
        }
    }

    private fun searchResults(root: JSONObject): JSONArray? {
        val tabbed = root.optJSONObject("contents")?.optJSONObject("tabbedSearchResultsRenderer")
        val tabs = tabbed?.optJSONArray("tabs")
        if (tabs != null) {
            for (t in 0 until tabs.length()) {
                val list = tabs.optJSONObject(t)
                    ?.optJSONObject("tabRenderer")
                    ?.optJSONObject("content")
                    ?.optJSONObject("sectionListRenderer")
                    ?.optJSONArray("contents")
                if (list != null && list.length() > 0) return list
            }
            return null
        }
        return root.optJSONObject("contents")?.optJSONObject("sectionListRenderer")?.optJSONArray("contents")
    }

    private fun topCardItem(card: JSONObject): BrowseItem {
        val onTap = card.optJSONObject("onTap")?.optJSONObject("browseEndpoint")
        val pageType = onTap?.optJSONObject("browseEndpointContextSupportedConfigs")
            ?.optJSONObject("browseEndpointContextMusicConfig")?.optString("pageType").orEmpty()
        val playlistId = card.optJSONObject("onTap")?.optJSONObject("watchPlaylistEndpoint")?.optString("playlistId").orEmpty()

        val titleRuns = card.optJSONObject("title")?.optJSONArray("runs")
        val watchVideoId = titleRuns?.optJSONObject(0)?.optJSONObject("navigationEndpoint")
            ?.optJSONObject("watchEndpoint")?.optString("videoId").orEmpty()

        val thumb = card.optJSONObject("thumbnail")?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")?.optJSONArray("thumbnails")?.let { arr ->
                if (arr.length() > 0) arr.optJSONObject(arr.length() - 1)?.optString("url").orEmpty() else ""
            }.orEmpty()

        val kind = when {
            pageType == "MUSIC_PAGE_TYPE_ARTIST" || pageType == "MUSIC_PAGE_TYPE_USER_CHANNEL" -> BrowseItemKind.ARTIST
            pageType == "MUSIC_PAGE_TYPE_ALBUM" -> BrowseItemKind.ALBUM
            pageType == "MUSIC_PAGE_TYPE_PLAYLIST" || playlistId.isNotBlank() -> BrowseItemKind.PLAYLIST
            watchVideoId.isNotBlank() -> BrowseItemKind.TRACK
            else -> BrowseItemKind.OTHER
        }

        val title = (0 until (titleRuns?.length() ?: 0)).joinToString("") {
            titleRuns?.optJSONObject(it)?.optString("text").orEmpty()
        }

        val subRuns = card.optJSONObject("subtitle")?.optJSONArray("runs")
        val subtitle = if (subRuns != null) {
            (0 until subRuns.length()).joinToString("") {
                subRuns.optJSONObject(it)?.optString("text").orEmpty()
            }
        } else ""

        val browseId = onTap?.optString("browseId").orEmpty()

        return BrowseItem(
            kind = kind,
            title = title,
            subtitle = subtitle,
            thumbUrl = thumb,
            videoId = watchVideoId,
            playlistId = playlistId,
            browseId = browseId
        )
    }

    private fun addItem(
        seenArtists: HashSet<String>,
        artists: ArrayList<YtArtist>,
        seenAlbums: HashSet<String>,
        albums: ArrayList<YtAlbum>,
        seenPlaylists: HashSet<String>,
        playlists: ArrayList<YtSearchPlaylist>,
        seenSongs: HashSet<String>,
        songs: ArrayList<LyreonTrack>,
        item: BrowseItem
    ) {
        when (item.kind) {
            BrowseItemKind.ARTIST -> {
                if (item.browseId.isNotBlank() && seenArtists.add(item.browseId)) {
                    artists.add(YtArtist(item.title, item.browseId, item.thumbUrl, item.subtitle, item.playlistId))
                }
            }
            BrowseItemKind.ALBUM -> {
                if (item.browseId.isNotBlank() && seenAlbums.add(item.browseId)) {
                    albums.add(YtAlbum(item.title, item.browseId, item.subtitle.substringBefore(" • "), item.thumbUrl, item.subtitle))
                }
            }
            BrowseItemKind.PLAYLIST -> {
                if (item.playlistId.isNotBlank() && seenPlaylists.add(item.playlistId)) {
                    playlists.add(YtSearchPlaylist(item.title, item.playlistId, item.subtitle.substringBefore(" • "), item.thumbUrl, item.subtitle))
                }
            }
            BrowseItemKind.TRACK, BrowseItemKind.OTHER -> {
                if (item.videoId.isNotBlank() && seenSongs.add(item.videoId)) {
                    songs.add(
                        LyreonTrack(
                            videoId = item.videoId,
                            title = item.title,
                            artist = item.subtitle.substringBefore(" • "),
                            album = item.subtitle.substringAfter(" • ", ""),
                            durationSec = item.durationSec.toLong(),
                            thumbnailUrl = item.thumbUrl
                        )
                    )
                }
            }
        }
    }
}
