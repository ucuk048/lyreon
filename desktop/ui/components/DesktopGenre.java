/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.internal.StabilityInferred
 *  kotlin.Metadata
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.lyreon.desktop.ui.components;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010\u0013\u001a\u00020\u0014H\u00d6\u0081\u0004J\n\u0010\u0015\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t\u00ca\u0001\f\b\u0017\u0012\b\b\u0018\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006\u0016"}, d2={"Lcom/lyreon/desktop/ui/components/DesktopGenre;", "", "id", "", "name", "searchQuery", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getId", "()Ljava/lang/String;", "getName", "getSearchQuery", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
public final class DesktopGenre {
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final String searchQuery;
    @JvmField
    public static final int $stable;

    public DesktopGenre(@NotNull String id, @NotNull String name, @NotNull String searchQuery) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)searchQuery, (String)"searchQuery");
        this.id = id;
        this.name = name;
        this.searchQuery = searchQuery;
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getSearchQuery() {
        return this.searchQuery;
    }

    @NotNull
    public final String component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.name;
    }

    @NotNull
    public final String component3() {
        return this.searchQuery;
    }

    @NotNull
    public final DesktopGenre copy(@NotNull String id, @NotNull String name, @NotNull String searchQuery) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)searchQuery, (String)"searchQuery");
        return new DesktopGenre(id, name, searchQuery);
    }

    public static /* synthetic */ DesktopGenre copy$default(DesktopGenre desktopGenre, String string, String string2, String string3, int n, Object object) {
        if ((n & 1) != 0) {
            string = desktopGenre.id;
        }
        if ((n & 2) != 0) {
            string2 = desktopGenre.name;
        }
        if ((n & 4) != 0) {
            string3 = desktopGenre.searchQuery;
        }
        return desktopGenre.copy(string, string2, string3);
    }

    @NotNull
    public String toString() {
        return "DesktopGenre(id=" + this.id + ", name=" + this.name + ", searchQuery=" + this.searchQuery + ")";
    }

    public int hashCode() {
        int result = this.id.hashCode();
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.searchQuery.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DesktopGenre)) {
            return false;
        }
        DesktopGenre desktopGenre = (DesktopGenre)other;
        if (!Intrinsics.areEqual((Object)this.id, (Object)desktopGenre.id)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)desktopGenre.name)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.searchQuery, (Object)desktopGenre.searchQuery);
    }
}
