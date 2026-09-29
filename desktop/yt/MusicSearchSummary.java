/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.internal.StabilityInferred
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.lyreon.desktop.yt;

import androidx.compose.runtime.internal.StabilityInferred;
import com.lyreon.desktop.model.LyreonTrack;
import com.lyreon.desktop.yt.YtAlbum;
import com.lyreon.desktop.yt.YtArtist;
import com.lyreon.desktop.yt.YtSearchPlaylist;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u0003H\u00c6\u0003J\u000f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\n0\u0003H\u00c6\u0003JI\u0010\u0019\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003H\u00c6\u0001J\u0014\u0010\u001a\u001a\u00020\u00132\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010\u001c\u001a\u00020\u001dH\u00d6\u0081\u0004J\n\u0010\u001e\u001a\u00020\u001fH\u00d6\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0011\u0010\u0012\u001a\u00020\u00138F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0014\u00ca\u0001\f\b!\u0012\b\b\"\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006 "}, d2={"Lcom/lyreon/desktop/yt/MusicSearchSummary;", "", "songs", "", "Lcom/lyreon/desktop/model/LyreonTrack;", "artists", "Lcom/lyreon/desktop/yt/YtArtist;", "albums", "Lcom/lyreon/desktop/yt/YtAlbum;", "playlists", "Lcom/lyreon/desktop/yt/YtSearchPlaylist;", "<init>", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "getSongs", "()Ljava/util/List;", "getArtists", "getAlbums", "getPlaylists", "isEmpty", "", "()Z", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
public final class MusicSearchSummary {
    @NotNull
    private final List<LyreonTrack> songs;
    @NotNull
    private final List<YtArtist> artists;
    @NotNull
    private final List<YtAlbum> albums;
    @NotNull
    private final List<YtSearchPlaylist> playlists;
    @JvmField
    public static final int $stable;

    public MusicSearchSummary(@NotNull List<LyreonTrack> songs, @NotNull List<YtArtist> artists, @NotNull List<YtAlbum> albums, @NotNull List<YtSearchPlaylist> playlists) {
        Intrinsics.checkNotNullParameter(songs, (String)"songs");
        Intrinsics.checkNotNullParameter(artists, (String)"artists");
        Intrinsics.checkNotNullParameter(albums, (String)"albums");
        Intrinsics.checkNotNullParameter(playlists, (String)"playlists");
        this.songs = songs;
        this.artists = artists;
        this.albums = albums;
        this.playlists = playlists;
    }

    public /* synthetic */ MusicSearchSummary(List list, List list2, List list3, List list4, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            list = CollectionsKt.emptyList();
        }
        if ((n & 2) != 0) {
            list2 = CollectionsKt.emptyList();
        }
        if ((n & 4) != 0) {
            list3 = CollectionsKt.emptyList();
        }
        if ((n & 8) != 0) {
            list4 = CollectionsKt.emptyList();
        }
        this(list, list2, list3, list4);
    }

    @NotNull
    public final List<LyreonTrack> getSongs() {
        return this.songs;
    }

    @NotNull
    public final List<YtArtist> getArtists() {
        return this.artists;
    }

    @NotNull
    public final List<YtAlbum> getAlbums() {
        return this.albums;
    }

    @NotNull
    public final List<YtSearchPlaylist> getPlaylists() {
        return this.playlists;
    }

    public final boolean isEmpty() {
        return this.songs.isEmpty() && this.artists.isEmpty() && this.albums.isEmpty() && this.playlists.isEmpty();
    }

    @NotNull
    public final List<LyreonTrack> component1() {
        return this.songs;
    }

    @NotNull
    public final List<YtArtist> component2() {
        return this.artists;
    }

    @NotNull
    public final List<YtAlbum> component3() {
        return this.albums;
    }

    @NotNull
    public final List<YtSearchPlaylist> component4() {
        return this.playlists;
    }

    @NotNull
    public final MusicSearchSummary copy(@NotNull List<LyreonTrack> songs, @NotNull List<YtArtist> artists, @NotNull List<YtAlbum> albums, @NotNull List<YtSearchPlaylist> playlists) {
        Intrinsics.checkNotNullParameter(songs, (String)"songs");
        Intrinsics.checkNotNullParameter(artists, (String)"artists");
        Intrinsics.checkNotNullParameter(albums, (String)"albums");
        Intrinsics.checkNotNullParameter(playlists, (String)"playlists");
        return new MusicSearchSummary(songs, artists, albums, playlists);
    }

    public static /* synthetic */ MusicSearchSummary copy$default(MusicSearchSummary musicSearchSummary, List list, List list2, List list3, List list4, int n, Object object) {
        if ((n & 1) != 0) {
            list = musicSearchSummary.songs;
        }
        if ((n & 2) != 0) {
            list2 = musicSearchSummary.artists;
        }
        if ((n & 4) != 0) {
            list3 = musicSearchSummary.albums;
        }
        if ((n & 8) != 0) {
            list4 = musicSearchSummary.playlists;
        }
        return musicSearchSummary.copy(list, list2, list3, list4);
    }

    @NotNull
    public String toString() {
        return "MusicSearchSummary(songs=" + this.songs + ", artists=" + this.artists + ", albums=" + this.albums + ", playlists=" + this.playlists + ")";
    }

    public int hashCode() {
        int result = ((Object)this.songs).hashCode();
        result = result * 31 + ((Object)this.artists).hashCode();
        result = result * 31 + ((Object)this.albums).hashCode();
        result = result * 31 + ((Object)this.playlists).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MusicSearchSummary)) {
            return false;
        }
        MusicSearchSummary musicSearchSummary = (MusicSearchSummary)other;
        if (!Intrinsics.areEqual(this.songs, musicSearchSummary.songs)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.artists, musicSearchSummary.artists)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.albums, musicSearchSummary.albums)) {
            return false;
        }
        return Intrinsics.areEqual(this.playlists, musicSearchSummary.playlists);
    }

    public MusicSearchSummary() {
        this(null, null, null, null, 15, null);
    }
}
