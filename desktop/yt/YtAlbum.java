/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.internal.StabilityInferred
 *  kotlin.Metadata
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.lyreon.desktop.yt;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J;\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010\u0019\u001a\u00020\u001aH\u00d6\u0081\u0004J\n\u0010\u001b\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b\u00ca\u0001\f\b\u001d\u0012\b\b\u001e\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006\u001c"}, d2={"Lcom/lyreon/desktop/yt/YtAlbum;", "", "title", "", "browseId", "artist", "thumbUrl", "subtitle", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getTitle", "()Ljava/lang/String;", "getBrowseId", "getArtist", "getThumbUrl", "getSubtitle", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
public final class YtAlbum {
    @NotNull
    private final String title;
    @NotNull
    private final String browseId;
    @NotNull
    private final String artist;
    @NotNull
    private final String thumbUrl;
    @NotNull
    private final String subtitle;
    @JvmField
    public static final int $stable;

    public YtAlbum(@NotNull String title, @NotNull String browseId, @NotNull String artist, @NotNull String thumbUrl, @NotNull String subtitle) {
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)browseId, (String)"browseId");
        Intrinsics.checkNotNullParameter((Object)artist, (String)"artist");
        Intrinsics.checkNotNullParameter((Object)thumbUrl, (String)"thumbUrl");
        Intrinsics.checkNotNullParameter((Object)subtitle, (String)"subtitle");
        this.title = title;
        this.browseId = browseId;
        this.artist = artist;
        this.thumbUrl = thumbUrl;
        this.subtitle = subtitle;
    }

    public /* synthetic */ YtAlbum(String string, String string2, String string3, String string4, String string5, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            string3 = "";
        }
        if ((n & 8) != 0) {
            string4 = "";
        }
        if ((n & 0x10) != 0) {
            string5 = "";
        }
        this(string, string2, string3, string4, string5);
    }

    @NotNull
    public final String getTitle() {
        return this.title;
    }

    @NotNull
    public final String getBrowseId() {
        return this.browseId;
    }

    @NotNull
    public final String getArtist() {
        return this.artist;
    }

    @NotNull
    public final String getThumbUrl() {
        return this.thumbUrl;
    }

    @NotNull
    public final String getSubtitle() {
        return this.subtitle;
    }

    @NotNull
    public final String component1() {
        return this.title;
    }

    @NotNull
    public final String component2() {
        return this.browseId;
    }

    @NotNull
    public final String component3() {
        return this.artist;
    }

    @NotNull
    public final String component4() {
        return this.thumbUrl;
    }

    @NotNull
    public final String component5() {
        return this.subtitle;
    }

    @NotNull
    public final YtAlbum copy(@NotNull String title, @NotNull String browseId, @NotNull String artist, @NotNull String thumbUrl, @NotNull String subtitle) {
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)browseId, (String)"browseId");
        Intrinsics.checkNotNullParameter((Object)artist, (String)"artist");
        Intrinsics.checkNotNullParameter((Object)thumbUrl, (String)"thumbUrl");
        Intrinsics.checkNotNullParameter((Object)subtitle, (String)"subtitle");
        return new YtAlbum(title, browseId, artist, thumbUrl, subtitle);
    }

    public static /* synthetic */ YtAlbum copy$default(YtAlbum ytAlbum, String string, String string2, String string3, String string4, String string5, int n, Object object) {
        if ((n & 1) != 0) {
            string = ytAlbum.title;
        }
        if ((n & 2) != 0) {
            string2 = ytAlbum.browseId;
        }
        if ((n & 4) != 0) {
            string3 = ytAlbum.artist;
        }
        if ((n & 8) != 0) {
            string4 = ytAlbum.thumbUrl;
        }
        if ((n & 0x10) != 0) {
            string5 = ytAlbum.subtitle;
        }
        return ytAlbum.copy(string, string2, string3, string4, string5);
    }

    @NotNull
    public String toString() {
        return "YtAlbum(title=" + this.title + ", browseId=" + this.browseId + ", artist=" + this.artist + ", thumbUrl=" + this.thumbUrl + ", subtitle=" + this.subtitle + ")";
    }

    public int hashCode() {
        int result = this.title.hashCode();
        result = result * 31 + this.browseId.hashCode();
        result = result * 31 + this.artist.hashCode();
        result = result * 31 + this.thumbUrl.hashCode();
        result = result * 31 + this.subtitle.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof YtAlbum)) {
            return false;
        }
        YtAlbum ytAlbum = (YtAlbum)other;
        if (!Intrinsics.areEqual((Object)this.title, (Object)ytAlbum.title)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.browseId, (Object)ytAlbum.browseId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.artist, (Object)ytAlbum.artist)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.thumbUrl, (Object)ytAlbum.thumbUrl)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.subtitle, (Object)ytAlbum.subtitle);
    }
}
