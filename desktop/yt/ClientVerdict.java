/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.lyreon.desktop.yt;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\f\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f\u00a8\u0006\r"}, d2={"Lcom/lyreon/desktop/yt/ClientVerdict;", "", "<init>", "(Ljava/lang/String;I)V", "USABLE", "REJECTED_BY_CDN", "SABR_ONLY", "HLS_ONLY", "DRM_ONLY", "PLAYABILITY_BLOCKED", "NO_STREAMING_DATA", "INVALID_RESPONSE", "TRANSPORT_ERROR", "Lyreon:desktop"})
public final class ClientVerdict
extends Enum<ClientVerdict> {
    public static final /* enum */ ClientVerdict USABLE = new ClientVerdict();
    public static final /* enum */ ClientVerdict REJECTED_BY_CDN = new ClientVerdict();
    public static final /* enum */ ClientVerdict SABR_ONLY = new ClientVerdict();
    public static final /* enum */ ClientVerdict HLS_ONLY = new ClientVerdict();
    public static final /* enum */ ClientVerdict DRM_ONLY = new ClientVerdict();
    public static final /* enum */ ClientVerdict PLAYABILITY_BLOCKED = new ClientVerdict();
    public static final /* enum */ ClientVerdict NO_STREAMING_DATA = new ClientVerdict();
    public static final /* enum */ ClientVerdict INVALID_RESPONSE = new ClientVerdict();
    public static final /* enum */ ClientVerdict TRANSPORT_ERROR = new ClientVerdict();
    private static final /* synthetic */ ClientVerdict[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static ClientVerdict[] values() {
        return (ClientVerdict[])$VALUES.clone();
    }

    public static ClientVerdict valueOf(String value) {
        return Enum.valueOf(ClientVerdict.class, value);
    }

    @NotNull
    public static EnumEntries<ClientVerdict> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = clientVerdictArray = new ClientVerdict[]{ClientVerdict.USABLE, ClientVerdict.REJECTED_BY_CDN, ClientVerdict.SABR_ONLY, ClientVerdict.HLS_ONLY, ClientVerdict.DRM_ONLY, ClientVerdict.PLAYABILITY_BLOCKED, ClientVerdict.NO_STREAMING_DATA, ClientVerdict.INVALID_RESPONSE, ClientVerdict.TRANSPORT_ERROR};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}
