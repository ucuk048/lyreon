/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.internal.StabilityInferred
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.lyreon.desktop.yt;

import androidx.compose.runtime.internal.StabilityInferred;
import com.lyreon.desktop.core.LyreonLog;
import com.lyreon.desktop.yt.ClientVerdict;
import com.lyreon.desktop.yt.InnertubeConfig;
import com.lyreon.desktop.yt.PlayerClientSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\b\u00c1\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u001b\u001a\u00020\u0005H\u0002J\u0016\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\b\b\u0002\u0010\"\u001a\u00020#J\u0010\u0010$\u001a\u0004\u0018\u00010\u00142\u0006\u0010%\u001a\u00020\u0005J\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013J\u001c\u0010'\u001a\u0004\u0018\u00010\u00052\b\u0010(\u001a\u0004\u0018\u00010\u00052\b\u0010)\u001a\u0004\u0018\u00010\u0005J\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013J(\u0010+\u001a\u00020,2\u0006\u0010%\u001a\u00020\u00052\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\u00072\b\b\u0002\u00100\u001a\u00020\u0005J\u000e\u00101\u001a\u00020,2\u0006\u00100\u001a\u00020\u0005J\u0006\u00102\u001a\u00020,J\u0006\u00103\u001a\u00020#J\u0010\u00104\u001a\u00020#2\b\b\u0002\u00105\u001a\u00020\u0007J\u0006\u00106\u001a\u00020\nJ\f\u00107\u001a\b\u0012\u0004\u0012\u00020\u00050\u0013J\u000e\u00108\u001a\u00020,2\u0006\u00109\u001a\u00020\u0005J\u0006\u0010:\u001a\u00020,J\u0016\u0010;\u001a\u00020.2\u0006\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020\u0005J\u000e\u0010?\u001a\u00020#2\u0006\u0010@\u001a\u00020=J\u000e\u0010A\u001a\u00020#2\u0006\u0010B\u001a\u00020=J\u000e\u0010C\u001a\u00020#2\u0006\u0010@\u001a\u00020=J\u0012\u0010D\u001a\u0004\u0018\u00010\u00052\b\u0010<\u001a\u0004\u0018\u00010=J\u000e\u0010E\u001a\u00020\u00052\u0006\u0010<\u001a\u00020=J\u0006\u0010F\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0080T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00070\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00050\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00ca\u0001\f\bH\u0012\b\bI\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006G"}, d2={"Lcom/lyreon/desktop/yt/PlayerClientLadder;", "", "<init>", "()V", "TAG", "", "COOLDOWN_MS", "", "EXTRACTOR_BYPASS_MS", "EXTRACTOR_BYPASS_AFTER", "", "DIAGNOSTIC_LIMIT", "WEB_UA_FIREFOX", "VISIONOS_UA", "TIZEN_TV_UA", "SAFARI_MAC_UA", "MWEB_UA", "CHROME_UA", "SPECS", "", "Lcom/lyreon/desktop/yt/PlayerClientSpec;", "lastGood", "Ljava/util/concurrent/atomic/AtomicReference;", "cooldownUntil", "Ljava/util/concurrent/ConcurrentHashMap;", "diagnostics", "Ljava/util/concurrent/ConcurrentLinkedDeque;", "timestamp", "lastSabrAtMs", "sabrTotal", "Ljava/util/concurrent/atomic/AtomicInteger;", "extractorSabrStreak", "extractorBypassUntilMs", "ordered", "forPlayback", "", "specOf", "key", "hlsSpecs", "streamUserAgentFor", "clientName", "clientVersion", "allSpecs", "note", "", "verdict", "Lcom/lyreon/desktop/yt/ClientVerdict;", "elapsedMs", "detail", "noteExtractorSabr", "noteExtractorSuccess", "extractorBypassed", "sabrPressureRecently", "windowMs", "sabrCount", "report", "push", "line", "reset", "inspect", "root", "Lorg/json/JSONObject;", "videoId", "hasDirectStreamUrl", "streamingData", "isDrmLocked", "format", "allFormatsDrmLocked", "hlsManifest", "playabilityReason", "snapshot", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
@SourceDebugExtension(value={"SMAP\nPlayerClientLadder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PlayerClientLadder.kt\ncom/lyreon/desktop/yt/PlayerClientLadder\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,708:1\n777#2:709\n873#2,2:710\n363#2,7:712\n2068#2,2:719\n296#2,2:721\n777#2:723\n873#2,2:724\n296#2,2:726\n296#2,2:728\n2068#2,2:730\n2068#2,2:732\n1#3:734\n*S KotlinDebug\n*F\n+ 1 PlayerClientLadder.kt\ncom/lyreon/desktop/yt/PlayerClientLadder\n*L\n442#1:709\n442#1:710,2\n444#1:712,7\n450#1:719,2\n456#1:721,2\n465#1:723\n465#1:724,2\n478#1:726,2\n482#1:728,2\n639#1:730,2\n665#1:732,2\n*E\n"})
public final class PlayerClientLadder {
    @NotNull
    public static final PlayerClientLadder INSTANCE = new PlayerClientLadder();
    @NotNull
    private static final String TAG = "PlayerClientLadder";
    private static final long COOLDOWN_MS = 180000L;
    private static final long EXTRACTOR_BYPASS_MS = 600000L;
    private static final int EXTRACTOR_BYPASS_AFTER = 2;
    private static final int DIAGNOSTIC_LIMIT = 40;
    @NotNull
    public static final String WEB_UA_FIREFOX = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0";
    @NotNull
    private static final String VISIONOS_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0 Safari/605.1.15";
    @NotNull
    private static final String TIZEN_TV_UA = "Mozilla/5.0(SMART-TV; Linux; Tizen 4.0.0.2) AppleWebkit/605.1.15 (KHTML, like Gecko) SamsungBrowser/9.2 TV Safari/605.1.15";
    @NotNull
    private static final String SAFARI_MAC_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.5 Safari/605.1.15,gzip(gfe)";
    @NotNull
    private static final String MWEB_UA = "Mozilla/5.0 (iPad; CPU OS 16_7_10 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1,gzip(gfe)";
    @NotNull
    private static final String CHROME_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36,gzip(gfe)";
    @NotNull
    private static final List<PlayerClientSpec> SPECS;
    @NotNull
    private static final AtomicReference<String> lastGood;
    @NotNull
    private static final ConcurrentHashMap<String, Long> cooldownUntil;
    @NotNull
    private static final ConcurrentLinkedDeque<String> diagnostics;
    private static volatile long lastSabrAtMs;
    @NotNull
    private static final AtomicInteger sabrTotal;
    @NotNull
    private static final AtomicInteger extractorSabrStreak;
    private static volatile long extractorBypassUntilMs;
    @JvmField
    public static final int $stable;

    private PlayerClientLadder() {
    }

    private final String timestamp() {
        String string = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"format(...)");
        return string;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<PlayerClientSpec> ordered(boolean forPlayback) {
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv = SPECS;
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            PlayerClientSpec it = (PlayerClientSpec)element$iv$iv;
            boolean bl = false;
            if (!(!forPlayback || it.getPlayableAnonymous())) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List base = CollectionsKt.toMutableList((Collection)((List)destination$iv$iv));
        String string = lastGood.get();
        if (string != null) {
            int index;
            block7: {
                int n;
                String good = string;
                boolean bl = false;
                List $this$indexOfFirst$iv = base;
                boolean $i$f$indexOfFirst = false;
                int index$iv = 0;
                for (Object item$iv : $this$indexOfFirst$iv) {
                    PlayerClientSpec it = (PlayerClientSpec)item$iv;
                    boolean bl2 = false;
                    if (Intrinsics.areEqual((Object)it.getKey(), (Object)good)) {
                        n = index$iv;
                        break block7;
                    }
                    ++index$iv;
                }
                n = index = -1;
            }
            if (index > 0) {
                base.add(0, base.remove(index));
            }
        }
        long now = System.currentTimeMillis();
        ArrayList warm = new ArrayList(base.size());
        ArrayList cold = new ArrayList(4);
        Iterable $this$forEach$iv = base;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            PlayerClientSpec spec = (PlayerClientSpec)element$iv;
            boolean bl = false;
            Long l = cooldownUntil.get(spec.getKey());
            if ((l != null ? l : 0L) > now) {
                ((Collection)cold).add(spec);
                continue;
            }
            ((Collection)warm).add(spec);
        }
        return CollectionsKt.plus((Collection)warm, (Iterable)cold);
    }

    public static /* synthetic */ List ordered$default(PlayerClientLadder playerClientLadder, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        return playerClientLadder.ordered(bl);
    }

    @Nullable
    public final PlayerClientSpec specOf(@NotNull String key) {
        Object v0;
        block1: {
            Intrinsics.checkNotNullParameter((Object)key, (String)"key");
            Iterable $this$firstOrNull$iv = SPECS;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                PlayerClientSpec it = (PlayerClientSpec)element$iv;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getKey(), (Object)key)) continue;
                v0 = element$iv;
                break block1;
            }
            v0 = null;
        }
        return v0;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<PlayerClientSpec> hlsSpecs() {
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv = SPECS;
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            PlayerClientSpec it = (PlayerClientSpec)element$iv$iv;
            boolean bl = false;
            if (!it.getPreferManifest()) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    @Nullable
    public final String streamUserAgentFor(@Nullable String clientName, @Nullable String clientVersion) {
        PlayerClientSpec playerClientSpec;
        PlayerClientSpec exact;
        PlayerClientSpec playerClientSpec2;
        Object v0;
        block4: {
            CharSequence charSequence = clientName;
            if (charSequence == null || StringsKt.isBlank((CharSequence)charSequence)) {
                return null;
            }
            Iterable $this$firstOrNull$iv = SPECS;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                CharSequence charSequence2;
                PlayerClientSpec it = (PlayerClientSpec)element$iv;
                boolean bl = false;
                if (!(StringsKt.equals((String)it.getClientName(), (String)clientName, (boolean)true) && !((charSequence2 = (CharSequence)clientVersion) == null || StringsKt.isBlank((CharSequence)charSequence2)) && Intrinsics.areEqual((Object)it.getClientVersion(), (Object)clientVersion))) continue;
                v0 = element$iv;
                break block4;
            }
            v0 = null;
        }
        if ((playerClientSpec2 = (exact = (PlayerClientSpec)v0)) == null) {
            Object v2;
            block5: {
                Iterable $this$firstOrNull$iv = SPECS;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    PlayerClientSpec it = (PlayerClientSpec)element$iv;
                    boolean bl = false;
                    if (!StringsKt.equals((String)it.getClientName(), (String)clientName, (boolean)true)) continue;
                    v2 = element$iv;
                    break block5;
                }
                v2 = null;
            }
            playerClientSpec2 = v2;
        }
        return (playerClientSpec = playerClientSpec2) != null ? playerClientSpec.getUserAgent() : null;
    }

    @NotNull
    public final List<PlayerClientSpec> allSpecs() {
        return SPECS;
    }

    public final void note(@NotNull String key, @NotNull ClientVerdict verdict, long elapsedMs, @NotNull String detail) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)((Object)verdict), (String)"verdict");
        Intrinsics.checkNotNullParameter((Object)detail, (String)"detail");
        if (verdict == ClientVerdict.USABLE || verdict == ClientVerdict.HLS_ONLY) {
            lastGood.set(key);
            cooldownUntil.remove(key);
        }
        if (verdict == ClientVerdict.SABR_ONLY || verdict == ClientVerdict.DRM_ONLY || verdict == ClientVerdict.REJECTED_BY_CDN) {
            lastSabrAtMs = System.currentTimeMillis();
            ((Map)cooldownUntil).put(key, lastSabrAtMs + 180000L);
        }
        if (verdict == ClientVerdict.SABR_ONLY) {
            sabrTotal.incrementAndGet();
        }
        if (verdict == ClientVerdict.TRANSPORT_ERROR) {
            ((Map)cooldownUntil).put(key, System.currentTimeMillis() + 180000L);
        }
        Object suffix = StringsKt.isBlank((CharSequence)detail) ? "" : " \u00b7 " + detail;
        this.push(this.timestamp() + " " + key + " \u2192 " + verdict + " (" + elapsedMs + "ms)" + (String)suffix);
        if (verdict == ClientVerdict.SABR_ONLY) {
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "klien '" + key + "' membalas SABR-only (tanpa URL stream). Total kejadian SABR sesi ini: " + sabrTotal.get(), null, 4, null);
        }
        if (verdict == ClientVerdict.DRM_ONLY) {
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "klien '" + key + "' membalas format ber-DRM (butuh cookie guest) \u2014 dilewati", null, 4, null);
        }
        if (verdict == ClientVerdict.REJECTED_BY_CDN) {
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "klien '" + key + "' memberi URL tetapi CDN menolaknya (" + detail + ") \u2014 kemungkinan pratinjau ~1 MiB atau 403; lanjut ke klien berikutnya", null, 4, null);
        }
    }

    public static /* synthetic */ void note$default(PlayerClientLadder playerClientLadder, String string, ClientVerdict clientVerdict, long l, String string2, int n, Object object) {
        if ((n & 8) != 0) {
            string2 = "";
        }
        playerClientLadder.note(string, clientVerdict, l, string2);
    }

    public final void noteExtractorSabr(@NotNull String detail) {
        Intrinsics.checkNotNullParameter((Object)detail, (String)"detail");
        lastSabrAtMs = System.currentTimeMillis();
        sabrTotal.incrementAndGet();
        int streak = extractorSabrStreak.incrementAndGet();
        if (streak >= 2) {
            extractorBypassUntilMs = System.currentTimeMillis() + 600000L;
            this.push(this.timestamp() + " extractor di-bypass 10 menit (SABR " + streak + "\u00d7 beruntun) \u2014 langsung ke tangga klien");
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "extractor di-bypass 600000 ms setelah " + streak + " kegagalan SABR", null, 4, null);
        } else {
            this.push(this.timestamp() + " extractor \u2192 SABR_ONLY \u00b7 " + detail);
        }
        LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "extractor membalas SABR-only (" + detail + ") \u2014 total sesi ini: " + sabrTotal.get(), null, 4, null);
    }

    public final void noteExtractorSuccess() {
        if (extractorSabrStreak.get() > 0 || extractorBypassUntilMs > 0L) {
            this.push(this.timestamp() + " extractor pulih \u2014 bypass dicabut");
        }
        extractorSabrStreak.set(0);
        extractorBypassUntilMs = 0L;
    }

    public final boolean extractorBypassed() {
        return System.currentTimeMillis() < extractorBypassUntilMs;
    }

    public final boolean sabrPressureRecently(long windowMs) {
        return System.currentTimeMillis() - lastSabrAtMs < windowMs;
    }

    public static /* synthetic */ boolean sabrPressureRecently$default(PlayerClientLadder playerClientLadder, long l, int n, Object object) {
        if ((n & 1) != 0) {
            l = 600000L;
        }
        return playerClientLadder.sabrPressureRecently(l);
    }

    public final int sabrCount() {
        return sabrTotal.get();
    }

    @NotNull
    public final List<String> report() {
        return CollectionsKt.toList((Iterable)diagnostics);
    }

    public final void push(@NotNull String line) {
        Intrinsics.checkNotNullParameter((Object)line, (String)"line");
        diagnostics.addFirst(line);
        while (diagnostics.size() > 40) {
            diagnostics.pollLast();
        }
    }

    public final void reset() {
        diagnostics.clear();
        cooldownUntil.clear();
        lastGood.set(null);
        lastSabrAtMs = 0L;
        sabrTotal.set(0);
        extractorSabrStreak.set(0);
        extractorBypassUntilMs = 0L;
    }

    @NotNull
    public final ClientVerdict inspect(@NotNull JSONObject root, @NotNull String videoId) {
        String sabrUrl;
        String status;
        String returnedId;
        Intrinsics.checkNotNullParameter((Object)root, (String)"root");
        Intrinsics.checkNotNullParameter((Object)videoId, (String)"videoId");
        JSONObject jSONObject = root.optJSONObject("videoDetails");
        String string = jSONObject != null ? jSONObject.optString("videoId") : null;
        if (string == null) {
            string = "";
        }
        if (!StringsKt.isBlank((CharSequence)(returnedId = string)) && !Intrinsics.areEqual((Object)returnedId, (Object)videoId)) {
            return ClientVerdict.INVALID_RESPONSE;
        }
        JSONObject jSONObject2 = root.optJSONObject("playabilityStatus");
        String string2 = jSONObject2 != null ? jSONObject2.optString("status") : null;
        if (string2 == null) {
            string2 = "";
        }
        if (!StringsKt.isBlank((CharSequence)(status = string2)) && !StringsKt.equals((String)status, (String)"OK", (boolean)true)) {
            return ClientVerdict.PLAYABILITY_BLOCKED;
        }
        JSONObject jSONObject3 = root.optJSONObject("streamingData");
        if (jSONObject3 == null) {
            return ClientVerdict.NO_STREAMING_DATA;
        }
        JSONObject streamingData = jSONObject3;
        if (this.hasDirectStreamUrl(streamingData)) {
            return ClientVerdict.USABLE;
        }
        CharSequence charSequence = streamingData.optString("hlsManifestUrl");
        if (!(charSequence == null || StringsKt.isBlank((CharSequence)charSequence))) {
            return ClientVerdict.HLS_ONLY;
        }
        JSONArray jSONArray = streamingData.optJSONArray("adaptiveFormats");
        JSONArray jSONArray2 = streamingData.optJSONArray("formats");
        int formatCount = (jSONArray != null ? jSONArray.length() : 0) + (jSONArray2 != null ? jSONArray2.length() : 0);
        if (formatCount > 0 && this.allFormatsDrmLocked(streamingData)) {
            return ClientVerdict.DRM_ONLY;
        }
        String string3 = streamingData.optString("serverAbrStreamingUrl");
        if (string3 == null) {
            string3 = sabrUrl = "";
        }
        return formatCount > 0 || !StringsKt.isBlank((CharSequence)sabrUrl) ? ClientVerdict.SABR_ONLY : ClientVerdict.NO_STREAMING_DATA;
    }

    public final boolean hasDirectStreamUrl(@NotNull JSONObject streamingData) {
        Intrinsics.checkNotNullParameter((Object)streamingData, (String)"streamingData");
        Object[] objectArray = new String[]{"adaptiveFormats", "formats"};
        Iterable $this$forEach$iv = CollectionsKt.listOf((Object[])objectArray);
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            JSONArray array;
            String key = (String)element$iv;
            boolean bl = false;
            if (streamingData.optJSONArray(key) == null) continue;
            int n = array.length();
            for (int i = 0; i < n; ++i) {
                JSONObject format;
                if (array.optJSONObject(i) == null || INSTANCE.isDrmLocked(format)) continue;
                CharSequence charSequence = format.optString("url");
                if (!(charSequence == null || StringsKt.isBlank((CharSequence)charSequence))) {
                    return true;
                }
                charSequence = format.optString("signatureCipher");
                if (!(charSequence == null || StringsKt.isBlank((CharSequence)charSequence))) {
                    return true;
                }
                charSequence = format.optString("cipher");
                if (charSequence == null || StringsKt.isBlank((CharSequence)charSequence)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean isDrmLocked(@NotNull JSONObject format) {
        Intrinsics.checkNotNullParameter((Object)format, (String)"format");
        JSONArray families = format.optJSONArray("drmFamilies");
        if (families != null && families.length() > 0) {
            return true;
        }
        if (format.optInt("drmTrackCount", 0) > 0) {
            return true;
        }
        String string = format.optString("drmFamilies");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"optString(...)");
        if (!StringsKt.isBlank((CharSequence)string)) {
            return true;
        }
        boolean bl = false;
        if (bl) return true;
        String string2 = format.optString("drmTrackType");
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"optString(...)");
        if (StringsKt.isBlank((CharSequence)string2)) return false;
        return true;
    }

    public final boolean allFormatsDrmLocked(@NotNull JSONObject streamingData) {
        Intrinsics.checkNotNullParameter((Object)streamingData, (String)"streamingData");
        int total = 0;
        int locked = 0;
        Object[] objectArray = new String[]{"adaptiveFormats", "formats"};
        Iterable $this$forEach$iv = CollectionsKt.listOf((Object[])objectArray);
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            JSONArray array;
            String key = (String)element$iv;
            boolean bl = false;
            if (streamingData.optJSONArray(key) == null) continue;
            int n = array.length();
            for (int i = 0; i < n; ++i) {
                JSONObject format;
                if (array.optJSONObject(i) == null) continue;
                ++total;
                if (!INSTANCE.isDrmLocked(format)) continue;
                ++locked;
            }
        }
        return total > 0 && locked == total;
    }

    @Nullable
    public final String hlsManifest(@Nullable JSONObject root) {
        String string;
        String string2;
        JSONObject jSONObject;
        if (root != null && (jSONObject = root.optJSONObject("streamingData")) != null && (string2 = jSONObject.optString("hlsManifestUrl")) != null) {
            String string3;
            String it = string3 = string2;
            boolean bl = false;
            string = !StringsKt.isBlank((CharSequence)it) ? string3 : null;
        } else {
            string = null;
        }
        return string;
    }

    @NotNull
    public final String playabilityReason(@NotNull JSONObject root) {
        String string;
        String reason;
        Intrinsics.checkNotNullParameter((Object)root, (String)"root");
        JSONObject jSONObject = root.optJSONObject("playabilityStatus");
        if (jSONObject == null) {
            return "";
        }
        JSONObject status = jSONObject;
        String string2 = status.optString("reason");
        if (string2 == null) {
            string2 = reason = "";
        }
        if ((string = status.optString("status")) == null) {
            string = "";
        }
        return string + (String)(StringsKt.isBlank((CharSequence)reason) ? "" : ": " + reason);
    }

    @NotNull
    public final String snapshot() {
        StringBuilder stringBuilder;
        StringBuilder $this$snapshot_u24lambda_u240 = stringBuilder = new StringBuilder();
        boolean bl = false;
        $this$snapshot_u24lambda_u240.append("ladder: playback=").append(CollectionsKt.joinToString$default((Iterable)INSTANCE.ordered(true), (CharSequence)",", null, null, (int)0, null, PlayerClientLadder::snapshot$lambda$0$0, (int)30, null)).append('\n');
        $this$snapshot_u24lambda_u240.append("hls=").append(CollectionsKt.joinToString$default((Iterable)INSTANCE.hlsSpecs(), (CharSequence)",", null, null, (int)0, null, PlayerClientLadder::snapshot$lambda$0$1, (int)30, null)).append('\n');
        $this$snapshot_u24lambda_u240.append("probe=").append(CollectionsKt.joinToString$default((Iterable)INSTANCE.ordered(false), (CharSequence)",", null, null, (int)0, null, PlayerClientLadder::snapshot$lambda$0$2, (int)30, null)).append('\n');
        StringBuilder stringBuilder2 = $this$snapshot_u24lambda_u240.append("lastGood=");
        String string = lastGood.get();
        if (string == null) {
            string = "-";
        }
        stringBuilder2.append(string).append('\n');
        StringBuilder stringBuilder3 = $this$snapshot_u24lambda_u240.append("visitor=").append(InnertubeConfig.INSTANCE.visitorOrigin()).append(" present=").append(InnertubeConfig.INSTANCE.visitor() != null).append(" sts=");
        Object object = InnertubeConfig.INSTANCE.signatureTimestamp();
        if (object == null) {
            object = "-";
        }
        stringBuilder3.append(object).append('\n');
        $this$snapshot_u24lambda_u240.append("sabrTotal=").append(sabrTotal.get()).append(" extractorBypassed=").append(INSTANCE.extractorBypassed()).append('\n');
        return stringBuilder.toString();
    }

    private static final CharSequence snapshot$lambda$0$0(PlayerClientSpec it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getKey();
    }

    private static final CharSequence snapshot$lambda$0$1(PlayerClientSpec it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getKey();
    }

    private static final CharSequence snapshot$lambda$0$2(PlayerClientSpec it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getKey();
    }

    static {
        Object[] objectArray = new PlayerClientSpec[]{new PlayerClientSpec("visionos", "VISIONOS", "0.1", "101", VISIONOS_UA, null, null, "Apple", "RealityDevice14,1", "visionOS", "1.3.21O771", 0, false, false, false, false, null, false, "satu-satunya klien yang terukur melayani file utuh (100% 206)", 260192, null), new PlayerClientSpec("android_vr", "ANDROID_VR", "1.65.10", "28", "com.google.android.apps.youtube.vr.oculus/1.65.10 (Linux; U; Android 12L; eureka-user Build/SQ3A.220605.009.A1) gzip", null, null, "Oculus", "Quest 3", "Android", "12L", 32, false, false, false, false, null, false, "pin yt-dlp/YouTube.js \u2014 WAJIB visitorData, tanpa buildId/cronet/packageName", 258144, null), new PlayerClientSpec("android_vr_1_43_32", "ANDROID_VR", "1.43.32", "28", "com.google.android.apps.youtube.vr.oculus/1.43.32 (Linux; U; Android 12; en_US; Quest 3; Build/SQ3A.220605.009.A1; Cronet/107.0.5284.2)", null, null, "Oculus", "Quest 3", "Android", "12", 32, false, false, false, false, null, false, "bitrate non-adaptive (audio tak patah), tanpa AV1; ter-gate per versi", 258144, null), new PlayerClientSpec("ipados", "IOS", "21.03.3", "5", "com.google.ios.youtube/21.03.3 (iPad7,6; U; CPU iPadOS 17_7_10 like Mac OS X; en-US)", null, null, "Apple", "iPad7,6", "iPadOS", "17.7.10.21H450", 0, false, false, false, false, null, false, "pratinjau ~1 MiB \u2014 hanya bila semua di atasnya gagal", 260192, null), new PlayerClientSpec("ios", "IOS", "21.03.1", "5", "com.google.ios.youtube/21.03.1 (iPhone16,2; U; CPU iOS 18_2 like Mac OS X;)", null, null, "Apple", "iPhone16,2", "iOS", "18.2.22C152", 0, false, false, false, false, null, false, "pratinjau ~1 MiB \u2014 cadangan paling akhir", 260192, null), new PlayerClientSpec("web_safari", "WEB", "", "1", SAFARI_MAC_UA, "www.youtube.com", null, null, null, null, null, 0, false, true, false, true, null, false, "Safari UA \u2192 HLS pre-merged (m3u8); URL langsungnya butuh poToken", 221120, null), new PlayerClientSpec("tv_simply", "TVHTML5_SIMPLY", "1.0", "75", CHROME_UA, "www.youtube.com", null, null, null, null, null, 0, false, true, false, true, null, false, "HTTPS butuh poToken, HLS tidak", 221120, null), new PlayerClientSpec("tvhtml5", "TVHTML5", "7.20260213.00.00", "7", TIZEN_TV_UA, null, null, null, null, null, null, 0, true, true, true, false, null, false, "Meld: untuk track unggahan pribadi (MLPT); butuh login", 233440, null), new PlayerClientSpec("web_creator", "WEB_CREATOR", "1.20260213.00.00", "62", WEB_UA_FIREFOX, null, null, null, null, null, null, 0, true, true, true, false, null, false, "satu-satunya klien OK untuk konten dibatasi umur \u2014 butuh login + pot=", 233440, null), new PlayerClientSpec("web_remix", "WEB_REMIX", "1.20260213.01.00", "67", WEB_UA_FIREFOX, null, null, null, null, null, null, 0, true, true, false, false, null, false, "klien metadata Meld; formatnya di balik cipher/n-challenge", 249824, null), new PlayerClientSpec("web", "WEB", "2.20260213.00.00", "1", WEB_UA_FIREFOX, null, null, null, null, null, null, 0, false, false, false, false, null, false, "SABR-only tanpa poToken", 262112, null), new PlayerClientSpec("android_vr_1_61_48", "ANDROID_VR", "1.61.48", "28", "com.google.android.apps.youtube.vr.oculus/1.61.48 (Linux; U; Android 12; en_US; Quest 3; Build/SQ3A.220605.009.A1; Cronet/132.0.6808.3)", null, null, "Oculus", "Quest 3", "Android", "12", 32, false, false, false, false, null, true, "KONTROL: versi ini ter-gate (LOGIN_REQUIRED) sedangkan 1.65.10 OK", 127072, null), new PlayerClientSpec("mweb", "MWEB", "", "2", MWEB_UA, "www.youtube.com", null, null, null, null, null, 0, false, true, false, false, null, true, "sering memberi URL, tapi URL web butuh n-transform \u2192 403/throttle", 122816, null)};
        SPECS = CollectionsKt.listOf((Object[])objectArray);
        lastGood = new AtomicReference<Object>(null);
        cooldownUntil = new ConcurrentHashMap();
        diagnostics = new ConcurrentLinkedDeque();
        sabrTotal = new AtomicInteger(0);
        extractorSabrStreak = new AtomicInteger(0);
    }
}
