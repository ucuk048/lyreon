/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.internal.StabilityInferred
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.lyreon.desktop.yt;

import androidx.compose.runtime.internal.StabilityInferred;
import com.lyreon.desktop.model.LyreonTrack;
import com.lyreon.desktop.yt.BrowseItem;
import com.lyreon.desktop.yt.BrowseItemKind;
import com.lyreon.desktop.yt.BrowseParser;
import com.lyreon.desktop.yt.MusicSearchSummary;
import com.lyreon.desktop.yt.YtAlbum;
import com.lyreon.desktop.yt.YtArtist;
import com.lyreon.desktop.yt.YtSearchPlaylist;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ$\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\rH\u0002J$\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\rH\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tH\u0002J$\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\rH\u0002J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\b\u001a\u00020\tH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00ca\u0001\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006\u0015"}, d2={"Lcom/lyreon/desktop/yt/MusicSearchParser;", "", "<init>", "()V", "MAX_PER_KIND", "", "parse", "Lcom/lyreon/desktop/yt/MusicSearchSummary;", "root", "Lorg/json/JSONObject;", "forEachRenderer", "", "visit", "Lkotlin/Function1;", "forEachTopCard", "topCardItem", "Lcom/lyreon/desktop/yt/BrowseItem;", "card", "forEachCard", "searchResults", "Lorg/json/JSONArray;", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
@SourceDebugExtension(value={"SMAP\nSearchModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SearchModel.kt\ncom/lyreon/desktop/yt/MusicSearchParser\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,273:1\n1#2:274\n*E\n"})
public final class MusicSearchParser {
    @NotNull
    public static final MusicSearchParser INSTANCE = new MusicSearchParser();
    private static final int MAX_PER_KIND = 24;
    @JvmField
    public static final int $stable;

    private MusicSearchParser() {
    }

    @NotNull
    public final MusicSearchSummary parse(@NotNull JSONObject root) {
        Intrinsics.checkNotNullParameter((Object)root, (String)"root");
        ArrayList songs = new ArrayList();
        ArrayList artists = new ArrayList();
        ArrayList albums = new ArrayList();
        ArrayList playlists = new ArrayList();
        HashSet seenArtists = new HashSet();
        HashSet seenAlbums = new HashSet();
        HashSet seenSongs = new HashSet();
        HashSet seenPlaylists = new HashSet();
        this.forEachRenderer(root, (Function1<? super JSONObject, Unit>)((Function1)arg_0 -> MusicSearchParser.parse$lambda$0(artists, albums, songs, playlists, seenArtists, seenAlbums, seenPlaylists, seenSongs, arg_0)));
        this.forEachCard(root, (Function1<? super JSONObject, Unit>)((Function1)arg_0 -> MusicSearchParser.parse$lambda$1(seenArtists, artists, seenAlbums, albums, seenPlaylists, playlists, seenSongs, songs, arg_0)));
        this.forEachTopCard(root, (Function1<? super JSONObject, Unit>)((Function1)arg_0 -> MusicSearchParser.parse$lambda$2(seenArtists, artists, seenAlbums, albums, seenPlaylists, playlists, seenSongs, songs, arg_0)));
        return new MusicSearchSummary(songs, CollectionsKt.take((Iterable)artists, (int)24), CollectionsKt.take((Iterable)albums, (int)24), CollectionsKt.take((Iterable)playlists, (int)24));
    }

    private final void forEachRenderer(JSONObject root, Function1<? super JSONObject, Unit> visit) {
        JSONArray jSONArray = this.searchResults(root);
        if (jSONArray == null) {
            return;
        }
        JSONArray contents = jSONArray;
        int n = contents.length();
        for (int s = 0; s < n; ++s) {
            JSONObject node;
            if (contents.optJSONObject(s) == null) continue;
            Object object = node.optJSONObject("musicShelfRenderer");
            if (object == null || (object = object.optJSONArray("contents")) == null) {
                JSONObject jSONObject = node.optJSONObject("itemSectionRenderer");
                object = jSONObject != null ? jSONObject.optJSONArray("contents") : null;
                if (object == null) continue;
            }
            JSONObject rows = object;
            int n2 = rows.length();
            for (int i = 0; i < n2; ++i) {
                JSONObject jSONObject;
                JSONObject jSONObject2 = rows.optJSONObject(i);
                if (jSONObject2 == null || (jSONObject = jSONObject2.optJSONObject("musicResponsiveListItemRenderer")) == null) continue;
                JSONObject renderer = jSONObject;
                visit.invoke((Object)renderer);
            }
        }
    }

    private final void forEachTopCard(JSONObject root, Function1<? super JSONObject, Unit> visit) {
        JSONArray jSONArray = this.searchResults(root);
        if (jSONArray == null) {
            return;
        }
        JSONArray contents = jSONArray;
        int n = contents.length();
        for (int s = 0; s < n; ++s) {
            JSONObject jSONObject = contents.optJSONObject(s);
            if (jSONObject == null || (jSONObject = jSONObject.optJSONObject("musicCardShelfRenderer")) == null) continue;
            JSONObject card = jSONObject;
            visit.invoke((Object)card);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final BrowseItem topCardItem(JSONObject card) {
        String string;
        String string2;
        String string3;
        String string4;
        JSONArray jSONArray;
        JSONObject jSONObject;
        String watchVideoId;
        JSONObject jSONObject2;
        JSONObject jSONObject3;
        JSONObject jSONObject4;
        JSONObject jSONObject5;
        String string5;
        String pageType;
        JSONObject jSONObject6;
        JSONObject jSONObject7;
        JSONObject jSONObject8 = card.optJSONObject("onTap");
        Object onTap = jSONObject8 != null ? jSONObject8.optJSONObject("browseEndpoint") : null;
        String string6 = onTap != null && (jSONObject7 = onTap.optJSONObject("browseEndpointContextSupportedConfigs")) != null && (jSONObject6 = jSONObject7.optJSONObject("browseEndpointContextMusicConfig")) != null ? jSONObject6.optString("pageType") : null;
        if (string6 == null) {
            string6 = pageType = "";
        }
        if ((string5 = (jSONObject6 = card.optJSONObject("onTap")) != null && (jSONObject5 = jSONObject6.optJSONObject("watchPlaylistEndpoint")) != null ? jSONObject5.optString("playlistId") : null) == null) {
            string5 = "";
        }
        String playlistId = string5;
        JSONObject jSONObject9 = card.optJSONObject("title");
        Object titleRuns = jSONObject9 != null ? jSONObject9.optJSONArray("runs") : null;
        String string7 = titleRuns != null && (jSONObject4 = titleRuns.optJSONObject(0)) != null && (jSONObject3 = jSONObject4.optJSONObject("navigationEndpoint")) != null && (jSONObject2 = jSONObject3.optJSONObject("watchEndpoint")) != null ? jSONObject2.optString("videoId") : null;
        if (string7 == null) {
            string7 = watchVideoId = "";
        }
        if ((jSONObject3 = card.optJSONObject("thumbnail")) != null && (jSONObject2 = jSONObject3.optJSONObject("musicThumbnailRenderer")) != null && (jSONObject = jSONObject2.optJSONObject("thumbnail")) != null && (jSONArray = jSONObject.optJSONArray("thumbnails")) != null) {
            JSONArray arr = jSONArray;
            boolean bl = false;
            if (arr.length() > 0) {
                JSONObject jSONObject10 = arr.optJSONObject(arr.length() - 1);
                string4 = jSONObject10 != null ? jSONObject10.optString("url") : null;
                if (string4 == null) {
                    string4 = "";
                }
            } else {
                string4 = "";
            }
        } else {
            string4 = string3 = null;
        }
        if (string4 == null) {
            string3 = "";
        }
        String thumb = string3;
        BrowseItemKind browseItemKind = Intrinsics.areEqual((Object)pageType, (Object)"MUSIC_PAGE_TYPE_ARTIST") || Intrinsics.areEqual((Object)pageType, (Object)"MUSIC_PAGE_TYPE_USER_CHANNEL") ? BrowseItemKind.ARTIST : (Intrinsics.areEqual((Object)pageType, (Object)"MUSIC_PAGE_TYPE_ALBUM") ? BrowseItemKind.ALBUM : (Intrinsics.areEqual((Object)pageType, (Object)"MUSIC_PAGE_TYPE_PLAYLIST") || !StringsKt.isBlank((CharSequence)playlistId) ? BrowseItemKind.PLAYLIST : (!StringsKt.isBlank((CharSequence)watchVideoId) ? BrowseItemKind.TRACK : BrowseItemKind.OTHER)));
        JSONArray jSONArray2 = titleRuns;
        String string8 = CollectionsKt.joinToString$default((Iterable)((Iterable)RangesKt.until((int)0, (int)(jSONArray2 != null ? jSONArray2.length() : 0))), (CharSequence)"", null, null, (int)0, null, arg_0 -> MusicSearchParser.topCardItem$lambda$1((JSONArray)titleRuns, arg_0), (int)30, null);
        JSONObject jSONObject11 = card.optJSONObject("subtitle");
        if (jSONObject11 != null && (jSONObject11 = jSONObject11.optJSONArray("runs")) != null) {
            void runs;
            jSONArray = jSONObject11;
            String string9 = string8;
            BrowseItemKind browseItemKind2 = browseItemKind;
            boolean bl = false;
            String string10 = CollectionsKt.joinToString$default((Iterable)((Iterable)RangesKt.until((int)0, (int)runs.length())), (CharSequence)"", null, null, (int)0, null, arg_0 -> MusicSearchParser.topCardItem$lambda$2$0((JSONArray)runs, arg_0), (int)30, null);
            browseItemKind = browseItemKind2;
            string8 = string9;
            string2 = string10;
        } else {
            string2 = string = null;
        }
        if (string2 == null) {
            string = "";
        }
        JSONObject jSONObject12 = onTap;
        String string11 = jSONObject12 != null ? jSONObject12.optString("browseId") : null;
        if (string11 == null) {
            string11 = "";
        }
        DefaultConstructorMarker defaultConstructorMarker = null;
        int n = 384;
        int n2 = 0;
        String string12 = null;
        String string13 = string11;
        String string14 = playlistId;
        String string15 = watchVideoId;
        String string16 = thumb;
        String string17 = string;
        String string18 = string8;
        BrowseItemKind browseItemKind3 = browseItemKind;
        return new BrowseItem(browseItemKind3, string18, string17, string16, string15, string14, string13, string12, n2, n, defaultConstructorMarker);
    }

    private final void forEachCard(JSONObject root, Function1<? super JSONObject, Unit> visit) {
        JSONArray jSONArray = this.searchResults(root);
        if (jSONArray == null) {
            return;
        }
        JSONArray contents = jSONArray;
        int n = contents.length();
        for (int s = 0; s < n; ++s) {
            JSONObject cards;
            JSONObject shelf;
            Object object;
            JSONObject node;
            if (contents.optJSONObject(s) == null) continue;
            JSONObject jSONObject = node.optJSONObject("musicCarouselShelfRenderer");
            if (jSONObject == null) {
                jSONObject = node.optJSONObject("musicImmersiveCarouselShelfRenderer");
            }
            if ((object = (shelf = jSONObject)) == null || (object = object.optJSONArray("contents")) == null) {
                JSONObject jSONObject2 = node.optJSONObject("musicShelfRenderer");
                object = jSONObject2 != null ? jSONObject2.optJSONArray("contents") : null;
            }
            if ((cards = object) == null) continue;
            int n2 = cards.length();
            for (int i = 0; i < n2; ++i) {
                JSONObject jSONObject3 = cards.optJSONObject(i);
                if (jSONObject3 == null || (jSONObject3 = jSONObject3.optJSONObject("musicTwoRowItemRenderer")) == null) continue;
                JSONObject card = jSONObject3;
                visit.invoke((Object)card);
            }
        }
    }

    private final JSONArray searchResults(JSONObject root) {
        JSONObject jSONObject;
        JSONArray tabs;
        JSONObject tabbed;
        JSONObject jSONObject2 = root.optJSONObject("contents");
        JSONObject jSONObject3 = tabbed = jSONObject2 != null ? jSONObject2.optJSONObject("tabbedSearchResultsRenderer") : null;
        Object object = tabs = jSONObject3 != null ? jSONObject3.optJSONArray("tabs") : null;
        if (tabs != null) {
            int n = tabs.length();
            for (int t = 0; t < n; ++t) {
                JSONObject jSONObject4;
                JSONObject jSONObject5;
                JSONObject jSONObject6;
                JSONArray list;
                JSONObject jSONObject7 = tabs.optJSONObject(t);
                JSONArray jSONArray = list = jSONObject7 != null && (jSONObject6 = jSONObject7.optJSONObject("tabRenderer")) != null && (jSONObject5 = jSONObject6.optJSONObject("content")) != null && (jSONObject4 = jSONObject5.optJSONObject("sectionListRenderer")) != null ? jSONObject4.optJSONArray("contents") : null;
                if (list == null || list.length() <= 0) continue;
                return list;
            }
            return null;
        }
        JSONObject jSONObject8 = root.optJSONObject("contents");
        return jSONObject8 != null && (jSONObject = jSONObject8.optJSONObject("sectionListRenderer")) != null ? jSONObject.optJSONArray("contents") : null;
    }

    private static final void parse$add(HashSet<String> seenArtists, ArrayList<YtArtist> artists, HashSet<String> seenAlbums, ArrayList<YtAlbum> albums, HashSet<String> seenPlaylists, ArrayList<YtSearchPlaylist> playlists, HashSet<String> seenSongs, ArrayList<LyreonTrack> songs, BrowseItem item) {
        switch (WhenMappings.$EnumSwitchMapping$0[item.getKind().ordinal()]) {
            case 1: {
                if (!(!StringsKt.isBlank((CharSequence)item.getBrowseId())) || !seenArtists.add(item.getBrowseId())) break;
                ((Collection)artists).add(new YtArtist(item.getTitle(), item.getBrowseId(), item.getThumbUrl(), item.getSubtitle(), item.getPlaylistId()));
                break;
            }
            case 2: {
                if (!(!StringsKt.isBlank((CharSequence)item.getBrowseId())) || !seenAlbums.add(item.getBrowseId())) break;
                ((Collection)albums).add(new YtAlbum(item.getTitle(), item.getBrowseId(), StringsKt.substringBefore$default((String)item.getSubtitle(), (String)" \u2022 ", null, (int)2, null), item.getThumbUrl(), item.getSubtitle()));
                break;
            }
            case 3: {
                if (!(!StringsKt.isBlank((CharSequence)item.getPlaylistId())) || !seenPlaylists.add(item.getPlaylistId())) break;
                ((Collection)playlists).add(new YtSearchPlaylist(item.getTitle(), item.getPlaylistId(), StringsKt.substringBefore$default((String)item.getSubtitle(), (String)" \u2022 ", null, (int)2, null), item.getThumbUrl(), item.getSubtitle()));
                break;
            }
            case 4: 
            case 5: {
                if (StringsKt.isBlank((CharSequence)item.getVideoId()) || !seenSongs.add(item.getVideoId())) {
                    return;
                }
                ((Collection)songs).add(new LyreonTrack(item.getVideoId(), item.getTitle(), StringsKt.substringBefore$default((String)item.getSubtitle(), (String)" \u2022 ", null, (int)2, null), StringsKt.substringAfter((String)item.getSubtitle(), (String)" \u2022 ", (String)""), item.getDurationSec(), item.getThumbUrl(), false, null, 192, null));
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
    }

    private static final Unit parse$lambda$0(ArrayList $artists, ArrayList $albums, ArrayList $songs, ArrayList $playlists, HashSet $seenArtists, HashSet $seenAlbums, HashSet $seenPlaylists, HashSet $seenSongs, JSONObject renderer) {
        Intrinsics.checkNotNullParameter((Object)renderer, (String)"renderer");
        BrowseItem browseItem = BrowseParser.INSTANCE.parseRow(renderer);
        if (browseItem == null) {
            return Unit.INSTANCE;
        }
        BrowseItem item = browseItem;
        if ($artists.size() >= 24 && $albums.size() >= 24 && $songs.size() >= 48 && $playlists.size() >= 24) {
            return Unit.INSTANCE;
        }
        MusicSearchParser.parse$add($seenArtists, $artists, $seenAlbums, $albums, $seenPlaylists, $playlists, $seenSongs, $songs, item);
        return Unit.INSTANCE;
    }

    private static final Unit parse$lambda$1(HashSet $seenArtists, ArrayList $artists, HashSet $seenAlbums, ArrayList $albums, HashSet $seenPlaylists, ArrayList $playlists, HashSet $seenSongs, ArrayList $songs, JSONObject card) {
        Intrinsics.checkNotNullParameter((Object)card, (String)"card");
        BrowseItem browseItem = BrowseParser.INSTANCE.parseCard(card);
        if (browseItem == null) {
            return Unit.INSTANCE;
        }
        BrowseItem item = browseItem;
        MusicSearchParser.parse$add($seenArtists, $artists, $seenAlbums, $albums, $seenPlaylists, $playlists, $seenSongs, $songs, item);
        return Unit.INSTANCE;
    }

    private static final Unit parse$lambda$2(HashSet $seenArtists, ArrayList $artists, HashSet $seenAlbums, ArrayList $albums, HashSet $seenPlaylists, ArrayList $playlists, HashSet $seenSongs, ArrayList $songs, JSONObject card) {
        Intrinsics.checkNotNullParameter((Object)card, (String)"card");
        MusicSearchParser.parse$add($seenArtists, $artists, $seenAlbums, $albums, $seenPlaylists, $playlists, $seenSongs, $songs, INSTANCE.topCardItem(card));
        return Unit.INSTANCE;
    }

    private static final CharSequence topCardItem$lambda$1(JSONArray $titleRuns, int it) {
        String string;
        JSONArray jSONArray = $titleRuns;
        if ((jSONArray != null && (jSONArray = jSONArray.optJSONObject(it)) != null ? jSONArray.optString("text") : (string = null)) == null) {
            string = "";
        }
        return string;
    }

    private static final CharSequence topCardItem$lambda$2$0(JSONArray $runs, int it) {
        JSONObject jSONObject = $runs.optJSONObject(it);
        String string = jSONObject != null ? jSONObject.optString("text") : null;
        if (string == null) {
            string = "";
        }
        return string;
    }

    @Metadata(mv={2, 4, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[BrowseItemKind.values().length];
            try {
                nArray[BrowseItemKind.ARTIST.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[BrowseItemKind.ALBUM.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[BrowseItemKind.PLAYLIST.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[BrowseItemKind.TRACK.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[BrowseItemKind.OTHER.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}
