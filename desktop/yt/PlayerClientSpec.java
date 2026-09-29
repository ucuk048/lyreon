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

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b9\n\u0002\u0018\u0002\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u00c7\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0011\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0018\u0010\u0019J\t\u00102\u001a\u00020\u0003H\u00c6\u0003J\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0003H\u00c6\u0003J\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\u000b\u00108\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010:\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010;\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010<\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010=\u001a\u00020\u000fH\u00c6\u0003J\t\u0010>\u001a\u00020\u0011H\u00c6\u0003J\t\u0010?\u001a\u00020\u0011H\u00c6\u0003J\t\u0010@\u001a\u00020\u0011H\u00c6\u0003J\t\u0010A\u001a\u00020\u0011H\u00c6\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010C\u001a\u00020\u0011H\u00c6\u0003J\t\u0010D\u001a\u00020\u0003H\u00c6\u0003J\u00d3\u0001\u0010E\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u00112\b\b\u0002\u0010\u0014\u001a\u00020\u00112\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u00112\b\b\u0002\u0010\u0017\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010F\u001a\u00020\u00112\b\u0010G\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010H\u001a\u00020\u000fH\u00d6\u0081\u0004J\n\u0010I\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001bR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001bR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001bR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001bR\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001bR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0013\u0010\r\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u0011\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010)R\u0011\u0010\u0013\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010)R\u0011\u0010\u0014\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010)R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u001bR\u0011\u0010\u0016\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010)R\u0011\u0010\u0017\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\u001bR\u0011\u00100\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\b1\u0010)\u00ca\u0001\f\bK\u0012\b\bL\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006J"}, d2={"Lcom/lyreon/desktop/yt/PlayerClientSpec;", "", "key", "", "clientName", "clientVersion", "clientId", "userAgent", "host", "altHost", "deviceMake", "deviceModel", "osName", "osVersion", "androidSdkVersion", "", "useSignatureTimestamp", "", "useWebPoTokens", "loginRequired", "preferManifest", "embedUrlValue", "probeOnly", "note", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZZZLjava/lang/String;ZLjava/lang/String;)V", "getKey", "()Ljava/lang/String;", "getClientName", "getClientVersion", "getClientId", "getUserAgent", "getHost", "getAltHost", "getDeviceMake", "getDeviceModel", "getOsName", "getOsVersion", "getAndroidSdkVersion", "()I", "getUseSignatureTimestamp", "()Z", "getUseWebPoTokens", "getLoginRequired", "getPreferManifest", "getEmbedUrlValue", "getProbeOnly", "getNote", "playableAnonymous", "getPlayableAnonymous", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "copy", "equals", "other", "hashCode", "toString", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
public final class PlayerClientSpec {
    @NotNull
    private final String key;
    @NotNull
    private final String clientName;
    @NotNull
    private final String clientVersion;
    @NotNull
    private final String clientId;
    @NotNull
    private final String userAgent;
    @NotNull
    private final String host;
    @Nullable
    private final String altHost;
    @Nullable
    private final String deviceMake;
    @Nullable
    private final String deviceModel;
    @Nullable
    private final String osName;
    @Nullable
    private final String osVersion;
    private final int androidSdkVersion;
    private final boolean useSignatureTimestamp;
    private final boolean useWebPoTokens;
    private final boolean loginRequired;
    private final boolean preferManifest;
    @Nullable
    private final String embedUrlValue;
    private final boolean probeOnly;
    @NotNull
    private final String note;
    @JvmField
    public static final int $stable;

    public PlayerClientSpec(@NotNull String key, @NotNull String clientName, @NotNull String clientVersion, @NotNull String clientId, @NotNull String userAgent, @NotNull String host, @Nullable String altHost, @Nullable String deviceMake, @Nullable String deviceModel, @Nullable String osName, @Nullable String osVersion, int androidSdkVersion, boolean useSignatureTimestamp, boolean useWebPoTokens, boolean loginRequired, boolean preferManifest, @Nullable String embedUrlValue, boolean probeOnly, @NotNull String note) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)clientName, (String)"clientName");
        Intrinsics.checkNotNullParameter((Object)clientVersion, (String)"clientVersion");
        Intrinsics.checkNotNullParameter((Object)clientId, (String)"clientId");
        Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
        Intrinsics.checkNotNullParameter((Object)host, (String)"host");
        Intrinsics.checkNotNullParameter((Object)note, (String)"note");
        this.key = key;
        this.clientName = clientName;
        this.clientVersion = clientVersion;
        this.clientId = clientId;
        this.userAgent = userAgent;
        this.host = host;
        this.altHost = altHost;
        this.deviceMake = deviceMake;
        this.deviceModel = deviceModel;
        this.osName = osName;
        this.osVersion = osVersion;
        this.androidSdkVersion = androidSdkVersion;
        this.useSignatureTimestamp = useSignatureTimestamp;
        this.useWebPoTokens = useWebPoTokens;
        this.loginRequired = loginRequired;
        this.preferManifest = preferManifest;
        this.embedUrlValue = embedUrlValue;
        this.probeOnly = probeOnly;
        this.note = note;
    }

    public /* synthetic */ PlayerClientSpec(String string, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11, int n, boolean bl, boolean bl2, boolean bl3, boolean bl4, String string12, boolean bl5, String string13, int n2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n2 & 0x20) != 0) {
            string6 = "music.youtube.com";
        }
        if ((n2 & 0x40) != 0) {
            string7 = "www.youtube.com";
        }
        if ((n2 & 0x80) != 0) {
            string8 = null;
        }
        if ((n2 & 0x100) != 0) {
            string9 = null;
        }
        if ((n2 & 0x200) != 0) {
            string10 = null;
        }
        if ((n2 & 0x400) != 0) {
            string11 = null;
        }
        if ((n2 & 0x800) != 0) {
            n = 0;
        }
        if ((n2 & 0x1000) != 0) {
            bl = false;
        }
        if ((n2 & 0x2000) != 0) {
            bl2 = false;
        }
        if ((n2 & 0x4000) != 0) {
            bl3 = false;
        }
        if ((n2 & 0x8000) != 0) {
            bl4 = false;
        }
        if ((n2 & 0x10000) != 0) {
            string12 = null;
        }
        if ((n2 & 0x20000) != 0) {
            bl5 = false;
        }
        if ((n2 & 0x40000) != 0) {
            string13 = "";
        }
        this(string, string2, string3, string4, string5, string6, string7, string8, string9, string10, string11, n, bl, bl2, bl3, bl4, string12, bl5, string13);
    }

    @NotNull
    public final String getKey() {
        return this.key;
    }

    @NotNull
    public final String getClientName() {
        return this.clientName;
    }

    @NotNull
    public final String getClientVersion() {
        return this.clientVersion;
    }

    @NotNull
    public final String getClientId() {
        return this.clientId;
    }

    @NotNull
    public final String getUserAgent() {
        return this.userAgent;
    }

    @NotNull
    public final String getHost() {
        return this.host;
    }

    @Nullable
    public final String getAltHost() {
        return this.altHost;
    }

    @Nullable
    public final String getDeviceMake() {
        return this.deviceMake;
    }

    @Nullable
    public final String getDeviceModel() {
        return this.deviceModel;
    }

    @Nullable
    public final String getOsName() {
        return this.osName;
    }

    @Nullable
    public final String getOsVersion() {
        return this.osVersion;
    }

    public final int getAndroidSdkVersion() {
        return this.androidSdkVersion;
    }

    public final boolean getUseSignatureTimestamp() {
        return this.useSignatureTimestamp;
    }

    public final boolean getUseWebPoTokens() {
        return this.useWebPoTokens;
    }

    public final boolean getLoginRequired() {
        return this.loginRequired;
    }

    public final boolean getPreferManifest() {
        return this.preferManifest;
    }

    @Nullable
    public final String getEmbedUrlValue() {
        return this.embedUrlValue;
    }

    public final boolean getProbeOnly() {
        return this.probeOnly;
    }

    @NotNull
    public final String getNote() {
        return this.note;
    }

    public final boolean getPlayableAnonymous() {
        return !this.loginRequired && !this.useWebPoTokens && !this.probeOnly;
    }

    @NotNull
    public final String component1() {
        return this.key;
    }

    @NotNull
    public final String component2() {
        return this.clientName;
    }

    @NotNull
    public final String component3() {
        return this.clientVersion;
    }

    @NotNull
    public final String component4() {
        return this.clientId;
    }

    @NotNull
    public final String component5() {
        return this.userAgent;
    }

    @NotNull
    public final String component6() {
        return this.host;
    }

    @Nullable
    public final String component7() {
        return this.altHost;
    }

    @Nullable
    public final String component8() {
        return this.deviceMake;
    }

    @Nullable
    public final String component9() {
        return this.deviceModel;
    }

    @Nullable
    public final String component10() {
        return this.osName;
    }

    @Nullable
    public final String component11() {
        return this.osVersion;
    }

    public final int component12() {
        return this.androidSdkVersion;
    }

    public final boolean component13() {
        return this.useSignatureTimestamp;
    }

    public final boolean component14() {
        return this.useWebPoTokens;
    }

    public final boolean component15() {
        return this.loginRequired;
    }

    public final boolean component16() {
        return this.preferManifest;
    }

    @Nullable
    public final String component17() {
        return this.embedUrlValue;
    }

    public final boolean component18() {
        return this.probeOnly;
    }

    @NotNull
    public final String component19() {
        return this.note;
    }

    @NotNull
    public final PlayerClientSpec copy(@NotNull String key, @NotNull String clientName, @NotNull String clientVersion, @NotNull String clientId, @NotNull String userAgent, @NotNull String host, @Nullable String altHost, @Nullable String deviceMake, @Nullable String deviceModel, @Nullable String osName, @Nullable String osVersion, int androidSdkVersion, boolean useSignatureTimestamp, boolean useWebPoTokens, boolean loginRequired, boolean preferManifest, @Nullable String embedUrlValue, boolean probeOnly, @NotNull String note) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)clientName, (String)"clientName");
        Intrinsics.checkNotNullParameter((Object)clientVersion, (String)"clientVersion");
        Intrinsics.checkNotNullParameter((Object)clientId, (String)"clientId");
        Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
        Intrinsics.checkNotNullParameter((Object)host, (String)"host");
        Intrinsics.checkNotNullParameter((Object)note, (String)"note");
        return new PlayerClientSpec(key, clientName, clientVersion, clientId, userAgent, host, altHost, deviceMake, deviceModel, osName, osVersion, androidSdkVersion, useSignatureTimestamp, useWebPoTokens, loginRequired, preferManifest, embedUrlValue, probeOnly, note);
    }

    public static /* synthetic */ PlayerClientSpec copy$default(PlayerClientSpec playerClientSpec, String string, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11, int n, boolean bl, boolean bl2, boolean bl3, boolean bl4, String string12, boolean bl5, String string13, int n2, Object object) {
        if ((n2 & 1) != 0) {
            string = playerClientSpec.key;
        }
        if ((n2 & 2) != 0) {
            string2 = playerClientSpec.clientName;
        }
        if ((n2 & 4) != 0) {
            string3 = playerClientSpec.clientVersion;
        }
        if ((n2 & 8) != 0) {
            string4 = playerClientSpec.clientId;
        }
        if ((n2 & 0x10) != 0) {
            string5 = playerClientSpec.userAgent;
        }
        if ((n2 & 0x20) != 0) {
            string6 = playerClientSpec.host;
        }
        if ((n2 & 0x40) != 0) {
            string7 = playerClientSpec.altHost;
        }
        if ((n2 & 0x80) != 0) {
            string8 = playerClientSpec.deviceMake;
        }
        if ((n2 & 0x100) != 0) {
            string9 = playerClientSpec.deviceModel;
        }
        if ((n2 & 0x200) != 0) {
            string10 = playerClientSpec.osName;
        }
        if ((n2 & 0x400) != 0) {
            string11 = playerClientSpec.osVersion;
        }
        if ((n2 & 0x800) != 0) {
            n = playerClientSpec.androidSdkVersion;
        }
        if ((n2 & 0x1000) != 0) {
            bl = playerClientSpec.useSignatureTimestamp;
        }
        if ((n2 & 0x2000) != 0) {
            bl2 = playerClientSpec.useWebPoTokens;
        }
        if ((n2 & 0x4000) != 0) {
            bl3 = playerClientSpec.loginRequired;
        }
        if ((n2 & 0x8000) != 0) {
            bl4 = playerClientSpec.preferManifest;
        }
        if ((n2 & 0x10000) != 0) {
            string12 = playerClientSpec.embedUrlValue;
        }
        if ((n2 & 0x20000) != 0) {
            bl5 = playerClientSpec.probeOnly;
        }
        if ((n2 & 0x40000) != 0) {
            string13 = playerClientSpec.note;
        }
        return playerClientSpec.copy(string, string2, string3, string4, string5, string6, string7, string8, string9, string10, string11, n, bl, bl2, bl3, bl4, string12, bl5, string13);
    }

    @NotNull
    public String toString() {
        return "PlayerClientSpec(key=" + this.key + ", clientName=" + this.clientName + ", clientVersion=" + this.clientVersion + ", clientId=" + this.clientId + ", userAgent=" + this.userAgent + ", host=" + this.host + ", altHost=" + this.altHost + ", deviceMake=" + this.deviceMake + ", deviceModel=" + this.deviceModel + ", osName=" + this.osName + ", osVersion=" + this.osVersion + ", androidSdkVersion=" + this.androidSdkVersion + ", useSignatureTimestamp=" + this.useSignatureTimestamp + ", useWebPoTokens=" + this.useWebPoTokens + ", loginRequired=" + this.loginRequired + ", preferManifest=" + this.preferManifest + ", embedUrlValue=" + this.embedUrlValue + ", probeOnly=" + this.probeOnly + ", note=" + this.note + ")";
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = result * 31 + this.clientName.hashCode();
        result = result * 31 + this.clientVersion.hashCode();
        result = result * 31 + this.clientId.hashCode();
        result = result * 31 + this.userAgent.hashCode();
        result = result * 31 + this.host.hashCode();
        result = result * 31 + (this.altHost == null ? 0 : this.altHost.hashCode());
        result = result * 31 + (this.deviceMake == null ? 0 : this.deviceMake.hashCode());
        result = result * 31 + (this.deviceModel == null ? 0 : this.deviceModel.hashCode());
        result = result * 31 + (this.osName == null ? 0 : this.osName.hashCode());
        result = result * 31 + (this.osVersion == null ? 0 : this.osVersion.hashCode());
        result = result * 31 + Integer.hashCode(this.androidSdkVersion);
        result = result * 31 + Boolean.hashCode(this.useSignatureTimestamp);
        result = result * 31 + Boolean.hashCode(this.useWebPoTokens);
        result = result * 31 + Boolean.hashCode(this.loginRequired);
        result = result * 31 + Boolean.hashCode(this.preferManifest);
        result = result * 31 + (this.embedUrlValue == null ? 0 : this.embedUrlValue.hashCode());
        result = result * 31 + Boolean.hashCode(this.probeOnly);
        result = result * 31 + this.note.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlayerClientSpec)) {
            return false;
        }
        PlayerClientSpec playerClientSpec = (PlayerClientSpec)other;
        if (!Intrinsics.areEqual((Object)this.key, (Object)playerClientSpec.key)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.clientName, (Object)playerClientSpec.clientName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.clientVersion, (Object)playerClientSpec.clientVersion)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.clientId, (Object)playerClientSpec.clientId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userAgent, (Object)playerClientSpec.userAgent)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.host, (Object)playerClientSpec.host)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.altHost, (Object)playerClientSpec.altHost)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.deviceMake, (Object)playerClientSpec.deviceMake)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.deviceModel, (Object)playerClientSpec.deviceModel)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.osName, (Object)playerClientSpec.osName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.osVersion, (Object)playerClientSpec.osVersion)) {
            return false;
        }
        if (this.androidSdkVersion != playerClientSpec.androidSdkVersion) {
            return false;
        }
        if (this.useSignatureTimestamp != playerClientSpec.useSignatureTimestamp) {
            return false;
        }
        if (this.useWebPoTokens != playerClientSpec.useWebPoTokens) {
            return false;
        }
        if (this.loginRequired != playerClientSpec.loginRequired) {
            return false;
        }
        if (this.preferManifest != playerClientSpec.preferManifest) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.embedUrlValue, (Object)playerClientSpec.embedUrlValue)) {
            return false;
        }
        if (this.probeOnly != playerClientSpec.probeOnly) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.note, (Object)playerClientSpec.note);
    }
}
