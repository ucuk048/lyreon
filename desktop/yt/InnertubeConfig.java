/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.internal.StabilityInferred
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.MatchResult
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.json.JSONArray
 *  org.json.JSONObject
 *  org.json.JSONTokener
 */
package com.lyreon.desktop.yt;

import androidx.compose.runtime.internal.StabilityInferred;
import com.lyreon.desktop.core.LyreonLog;
import com.lyreon.desktop.yt.InnertubeRequest;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\b\u00c1\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u0005J\u0016\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0005J\u0006\u0010\u001e\u001a\u00020\u0019J\u0016\u0010\"\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0005J\b\u0010#\u001a\u00020\u0013H\u0002J\u0012\u0010$\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u001e\u0010'\u001a\u0004\u0018\u00010\u00052\b\u0010(\u001a\u0004\u0018\u00010\u00012\b\b\u0002\u0010)\u001a\u00020\u000fH\u0002J\u0010\u0010*\u001a\u00020\u00132\b\u0010+\u001a\u0004\u0018\u00010,J\u0016\u0010-\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0005J\u0006\u0010.\u001a\u00020\u0005J\u0006\u0010\t\u001a\u00020\u0005J\r\u0010/\u001a\u0004\u0018\u00010\u000f\u00a2\u0006\u0002\u00100J\u0006\u00101\u001a\u00020\u0005J\u0006\u00102\u001a\u00020\u0005J\b\u00103\u001a\u0004\u0018\u00010\u0005J\b\u00104\u001a\u0004\u0018\u00010\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000RN\u0010\u0010\u001aB\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00050\u0005\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00130\u0013 \u0012* \u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00050\u0005\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00130\u0013\u0018\u00010\u00110\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0015X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020&X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00ca\u0001\f\b6\u0012\b\b7\u0012\u0004\b\u0003\u0010\u0002\u00a8\u00065"}, d2={"Lcom/lyreon/desktop/yt/InnertubeConfig;", "", "<init>", "()V", "TAG", "", "FALLBACK_KEY", "FALLBACK_WEB_VERSION", "FALLBACK_ANDROID_VERSION", "apiKey", "webVersion", "androidVersion", "visitorData", "playerJsUrl", "signatureTs", "", "fetched", "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", "kotlin.jvm.PlatformType", "", "RETRY_AFTER_MS", "", "scrapeOk", "lastScrapeAtMs", "warmUp", "", "ioClient", "Lokhttp3/OkHttpClient;", "userAgent", "ensure", "invalidateVisitor", "visitorSource", "VISITOR_TTL_MS", "visitorFetchedAtMs", "ensureVisitorData", "isVisitorStale", "fetchVisitorFromSwJs", "VISITOR_REGEX", "Lkotlin/text/Regex;", "findVisitorIn", "node", "depth", "adoptVisitor", "root", "Lorg/json/JSONObject;", "refreshVisitor", "visitorOrigin", "signatureTimestamp", "()Ljava/lang/Integer;", "webClientVersion", "androidClientVersion", "visitor", "baseJsUrl", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
@SourceDebugExtension(value={"SMAP\nInnertubeConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InnertubeConfig.kt\ncom/lyreon/desktop/yt/InnertubeConfig\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,199:1\n1#2:200\n161#3,6:201\n*S KotlinDebug\n*F\n+ 1 InnertubeConfig.kt\ncom/lyreon/desktop/yt/InnertubeConfig\n*L\n143#1:201,6\n*E\n"})
public final class InnertubeConfig {
    @NotNull
    public static final InnertubeConfig INSTANCE = new InnertubeConfig();
    @NotNull
    private static final String TAG = "InnertubeConfig";
    @NotNull
    private static final String FALLBACK_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
    @NotNull
    private static final String FALLBACK_WEB_VERSION = "2.20260917.01.00";
    @NotNull
    private static final String FALLBACK_ANDROID_VERSION = "19.45.38";
    @NotNull
    private static volatile String apiKey = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
    @NotNull
    private static volatile String webVersion = "2.20260917.01.00";
    @NotNull
    private static volatile String androidVersion = "19.45.38";
    @Nullable
    private static volatile String visitorData;
    @Nullable
    private static volatile String playerJsUrl;
    private static volatile int signatureTs;
    private static final ConcurrentHashMap.KeySetView<String, Boolean> fetched;
    private static final long RETRY_AFTER_MS = 300000L;
    private static volatile boolean scrapeOk;
    private static volatile long lastScrapeAtMs;
    @NotNull
    private static volatile String visitorSource;
    private static final long VISITOR_TTL_MS = 43200000L;
    private static volatile long visitorFetchedAtMs;
    @NotNull
    private static final Regex VISITOR_REGEX;
    @JvmField
    public static final int $stable;

    private InnertubeConfig() {
    }

    public final void warmUp(@NotNull OkHttpClient ioClient, @NotNull String userAgent) {
        Intrinsics.checkNotNullParameter((Object)ioClient, (String)"ioClient");
        Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
        this.ensure(ioClient, userAgent);
        new Thread(() -> InnertubeConfig.warmUp$lambda$0(ioClient, userAgent)).start();
    }

    public static /* synthetic */ void warmUp$default(InnertubeConfig innertubeConfig, OkHttpClient okHttpClient, String string, int n, Object object) {
        if ((n & 2) != 0) {
            string = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0";
        }
        innertubeConfig.warmUp(okHttpClient, string);
    }

    public final void ensure(@NotNull OkHttpClient ioClient, @NotNull String userAgent) {
        Intrinsics.checkNotNullParameter((Object)ioClient, (String)"ioClient");
        Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
        if (scrapeOk) {
            return;
        }
        long now = System.currentTimeMillis();
        if (lastScrapeAtMs > 0L && now - lastScrapeAtMs < 300000L) {
            return;
        }
        lastScrapeAtMs = now;
        new Thread(() -> InnertubeConfig.ensure$lambda$0(userAgent, ioClient)).start();
    }

    public final synchronized void invalidateVisitor() {
        visitorData = null;
        visitorSource = "-";
        visitorFetchedAtMs = 0L;
        fetched.remove("visitor");
        LyreonLog.INSTANCE.i(TAG, "visitorData dibuang \u2014 akan diambil ulang pada permintaan berikut");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final synchronized void ensureVisitorData(@NotNull OkHttpClient ioClient, @NotNull String userAgent) {
        block18: {
            Object object;
            Object $this$ensureVisitorData_u24lambda_u240;
            Intrinsics.checkNotNullParameter((Object)ioClient, (String)"ioClient");
            Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
            String existing = visitorData;
            if (existing != null && !this.isVisitorStale()) {
                return;
            }
            if (existing == null && !fetched.add("visitor")) {
                return;
            }
            Object object2 = this;
            try {
                $this$ensureVisitorData_u24lambda_u240 = object2;
                boolean bl = false;
                $this$ensureVisitorData_u24lambda_u240 = Result.constructor-impl((Object)super.fetchVisitorFromSwJs(ioClient));
            }
            catch (Throwable bl) {
                $this$ensureVisitorData_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            object2 = $this$ensureVisitorData_u24lambda_u240;
            String fromSwJs = (String)(Result.isFailure-impl((Object)object2) ? null : object2);
            if (fromSwJs != null) {
                visitorData = fromSwJs;
                visitorSource = "sw.js";
                visitorFetchedAtMs = System.currentTimeMillis();
                LyreonLog.INSTANCE.i(TAG, "visitorData dari sw.js_data: " + StringsKt.take((String)fromSwJs, (int)12) + "\u2026 (" + fromSwJs.length() + " char)");
                return;
            }
            object2 = this;
            try {
                InnertubeConfig $this$ensureVisitorData_u24lambda_u241 = (InnertubeConfig)object2;
                boolean bl = false;
                Request req = new Request.Builder().url("https://www.youtube.com/youtubei/v1/guide?prettyPrint=false").header("User-Agent", userAgent).post(RequestBody.Companion.create(InnertubeRequest.INSTANCE.baseContextJson(webVersion), MediaType.Companion.get("application/json"))).build();
                Closeable closeable = (Closeable)ioClient.newCall(req).execute();
                Throwable throwable = null;
                try {
                    String v;
                    String string;
                    Response resp = (Response)closeable;
                    boolean bl2 = false;
                    ResponseBody responseBody = resp.body();
                    String string2 = responseBody != null ? responseBody.string() : null;
                    if (string2 == null) {
                        string2 = "";
                    }
                    JSONObject body = new JSONObject(string2);
                    String it = string = body.optString("visitorData");
                    boolean bl3 = false;
                    Intrinsics.checkNotNull((Object)it);
                    String string3 = !StringsKt.isBlank((CharSequence)it) ? string : null;
                    if (string3 == null) {
                        String string4;
                        string = body.optJSONObject("responseContext");
                        if (string != null && (string4 = string.optString("visitorData")) != null) {
                            String string5;
                            String it2 = string5 = string4;
                            boolean bl4 = false;
                            string3 = !StringsKt.isBlank((CharSequence)it2) ? string5 : null;
                        } else {
                            string3 = null;
                        }
                    }
                    if ((v = string3) != null) {
                        visitorData = v;
                        visitorSource = "guide";
                        visitorFetchedAtMs = System.currentTimeMillis();
                        LyreonLog.INSTANCE.i(TAG, "visitorData dari guide: " + StringsKt.take((String)v, (int)12) + "\u2026");
                    }
                    Unit unit = Unit.INSTANCE;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
                }
                object = Result.constructor-impl((Object)Unit.INSTANCE);
            }
            catch (Throwable bl) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            object2 = object;
            Throwable throwable = Result.exceptionOrNull-impl((Object)object2);
            if (throwable == null) break block18;
            Object e = object = throwable;
            boolean bl = false;
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "gagal ambil visitorData: " + e.getClass().getSimpleName() + ": " + ((Throwable)e).getMessage(), null, 4, null);
        }
    }

    private final boolean isVisitorStale() {
        return visitorFetchedAtMs > 0L && System.currentTimeMillis() - visitorFetchedAtMs > 43200000L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final String fetchVisitorFromSwJs(OkHttpClient ioClient) {
        JSONArray arr0;
        Object object;
        int start;
        Object text;
        block16: {
            int n;
            Object resp;
            Request req = new Request.Builder().url("https://music.youtube.com/sw.js_data").header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0").header("Accept", "application/json").header("Accept-Language", "en-US,en;q=0.9").header("Referer", "https://music.youtube.com/").get().build();
            Closeable closeable = (Closeable)ioClient.newCall(req).execute();
            Throwable throwable = null;
            try {
                resp = (Response)closeable;
                boolean bl = false;
                if (!resp.isSuccessful()) {
                    String string = null;
                    return string;
                }
                ResponseBody responseBody = resp.body();
                String string = responseBody != null ? responseBody.string() : null;
                if (string == null) {
                    string = "";
                }
                resp = string;
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
            }
            text = resp;
            if (StringsKt.isBlank((CharSequence)((CharSequence)text))) {
                return null;
            }
            CharSequence $this$indexOfFirst$iv = (CharSequence)text;
            boolean $i$f$indexOfFirst = false;
            int n2 = $this$indexOfFirst$iv.length();
            for (int index$iv = 0; index$iv < n2; ++index$iv) {
                char it = $this$indexOfFirst$iv.charAt(index$iv);
                boolean bl = false;
                if (!(it == '[' || it == '{')) continue;
                n = index$iv;
                break block16;
            }
            n = start = -1;
        }
        if (start < 0) {
            return null;
        }
        Object index$iv = this;
        try {
            InnertubeConfig $this$fetchVisitorFromSwJs_u24lambda_u242 = index$iv;
            boolean bl = false;
            String string = ((String)text).substring(start);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            object = Result.constructor-impl((Object)new JSONTokener(string).nextValue());
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        index$iv = object;
        Object object2 = Result.isFailure-impl((Object)index$iv) ? null : index$iv;
        if (object2 == null) {
            return null;
        }
        Object root = object2;
        JSONArray jSONArray = root instanceof JSONArray ? (JSONArray)root : null;
        JSONArray jSONArray2 = arr0 = jSONArray != null ? jSONArray.optJSONArray(0) : null;
        Object arr2 = jSONArray2 != null ? jSONArray2.optJSONArray(2) : null;
        object = InnertubeConfig.findVisitorIn$default(this, arr2, 0, 2, null);
        if (object != null) {
            Object it = object;
            boolean bl = false;
            return it;
        }
        return this.findVisitorIn(root, 0);
    }

    private final String findVisitorIn(Object node, int depth) {
        String string;
        if (node == null || depth > 6) {
            return null;
        }
        Object object = node;
        if (object instanceof String) {
            Object object2 = node;
            String it = (String)object2;
            boolean bl = false;
            string = (String)(VISITOR_REGEX.containsMatchIn((CharSequence)it) && it.length() >= 20 ? object2 : null);
        } else if (object instanceof JSONArray) {
            int n = ((JSONArray)node).length();
            for (int i = 0; i < n; ++i) {
                String bl = this.findVisitorIn(((JSONArray)node).opt(i), depth + 1);
                if (bl == null) continue;
                String it = bl;
                boolean bl2 = false;
                return it;
            }
            string = null;
        } else if (object instanceof JSONObject) {
            String string2;
            String it = string2 = ((JSONObject)node).optString("visitorData");
            boolean bl = false;
            Intrinsics.checkNotNull((Object)it);
            string = !StringsKt.isBlank((CharSequence)it) && VISITOR_REGEX.containsMatchIn((CharSequence)it) ? string2 : null;
            if (string == null) {
                String string3;
                string2 = ((JSONObject)node).optJSONObject("responseContext");
                if (string2 != null && (string3 = string2.optString("visitorData")) != null) {
                    String string4;
                    String it2 = string4 = string3;
                    boolean bl3 = false;
                    string = !StringsKt.isBlank((CharSequence)it2) ? string4 : null;
                } else {
                    string = null;
                }
            }
        } else {
            string = null;
        }
        return string;
    }

    static /* synthetic */ String findVisitorIn$default(InnertubeConfig innertubeConfig, Object object, int n, int n2, Object object2) {
        if ((n2 & 2) != 0) {
            n = 0;
        }
        return innertubeConfig.findVisitorIn(object, n);
    }

    public final boolean adoptVisitor(@Nullable JSONObject root) {
        String v;
        String string;
        block5: {
            block4: {
                String string2;
                String string3;
                if (root == null || visitorData != null) {
                    return false;
                }
                JSONObject jSONObject = root.optJSONObject("responseContext");
                if (jSONObject == null || (string3 = jSONObject.optString("visitorData")) == null) break block4;
                String it = string2 = string3;
                boolean bl = false;
                String string4 = string = !StringsKt.isBlank((CharSequence)it) ? string2 : null;
                if (string != null) break block5;
            }
            return false;
        }
        visitorData = v = string;
        visitorSource = "response";
        visitorFetchedAtMs = System.currentTimeMillis();
        LyreonLog.INSTANCE.i(TAG, "visitorData diadopsi dari responseContext: " + StringsKt.take((String)v, (int)12) + "\u2026");
        return true;
    }

    public final void refreshVisitor(@NotNull OkHttpClient ioClient, @NotNull String userAgent) {
        Intrinsics.checkNotNullParameter((Object)ioClient, (String)"ioClient");
        Intrinsics.checkNotNullParameter((Object)userAgent, (String)"userAgent");
        this.invalidateVisitor();
        this.ensureVisitorData(ioClient, userAgent);
    }

    @NotNull
    public final String visitorOrigin() {
        return visitorData == null ? "-" : visitorSource;
    }

    @NotNull
    public final String apiKey() {
        return apiKey;
    }

    @Nullable
    public final Integer signatureTimestamp() {
        Integer n = signatureTs;
        int it = ((Number)n).intValue();
        boolean bl = false;
        return it > 0 ? n : null;
    }

    @NotNull
    public final String webClientVersion() {
        return webVersion;
    }

    @NotNull
    public final String androidClientVersion() {
        return androidVersion;
    }

    @Nullable
    public final String visitor() {
        return visitorData;
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public final String baseJsUrl() {
        Object object;
        String string = playerJsUrl;
        if (string != null) {
            void var1_1;
            String it = string;
            boolean bl = false;
            object = StringsKt.startsWith$default((String)it, (String)"//", (boolean)false, (int)2, null) ? "https:" + it : var1_1;
        } else {
            object = null;
        }
        return object;
    }

    private static final void warmUp$lambda$0(OkHttpClient $ioClient, String $userAgent) {
        INSTANCE.ensureVisitorData($ioClient, $userAgent);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void ensure$lambda$0(String $userAgent, OkHttpClient $ioClient) {
        block13: {
            Object object;
            Object object2 = INSTANCE;
            try {
                InnertubeConfig $this$ensure_u24lambda_u240_u240 = object2;
                boolean bl = false;
                Request req = new Request.Builder().url("https://www.youtube.com/watch?v=dQw4w9WgXcQ").header("User-Agent", $userAgent).header("Accept-Language", "en-US,en;q=0.9").get().build();
                Closeable closeable = (Closeable)$ioClient.newCall(req).execute();
                Throwable throwable = null;
                try {
                    Integer n;
                    String string;
                    String string2;
                    List list;
                    Response resp = (Response)closeable;
                    boolean bl2 = false;
                    ResponseBody responseBody = resp.body();
                    String string3 = responseBody != null ? responseBody.string() : null;
                    if (string3 == null) {
                        string3 = "";
                    }
                    String html = string3;
                    MatchResult matchResult = Regex.find$default((Regex)new Regex("\"INNERTUBE_API_KEY\"\\s*:\\s*\"([^\"]+)\""), (CharSequence)html, (int)0, (int)2, null);
                    apiKey = matchResult != null && (list = matchResult.getGroupValues()) != null && (string2 = (String)CollectionsKt.getOrNull((List)list, (int)1)) != null ? string2 : apiKey;
                    matchResult = Regex.find$default((Regex)new Regex("\"INNERTUBE_CONTEXT_CLIENT_VERSION\"\\s*:\\s*\"([^\"]+)\""), (CharSequence)html, (int)0, (int)2, null);
                    webVersion = matchResult != null && (list = matchResult.getGroupValues()) != null && (string2 = (String)CollectionsKt.getOrNull((List)list, (int)1)) != null ? string2 : webVersion;
                    matchResult = Regex.find$default((Regex)new Regex("\"PLAYER_JS_URL\"\\s*:\\s*\"([^\"]+)\""), (CharSequence)html, (int)0, (int)2, null);
                    if (matchResult != null && (list = matchResult.getGroupValues()) != null && (string2 = (String)CollectionsKt.getOrNull((List)list, (int)1)) != null) {
                        string = string2;
                    } else {
                        Object object3 = Regex.find$default((Regex)new Regex("src=\"(/s/player/[^\"]+/base\\.js)\""), (CharSequence)html, (int)0, (int)2, null);
                        if ((object3 != null && (object3 = object3.getGroupValues()) != null ? (String)CollectionsKt.getOrNull((List)object3, (int)1) : (string = null)) == null) {
                            Object object4 = Regex.find$default((Regex)new Regex("src=\"(https://www\\.youtube\\.com/s/player/[^\"]+/base\\.js)\""), (CharSequence)html, (int)0, (int)2, null);
                            string = object4 != null && (object4 = object4.getGroupValues()) != null ? (String)CollectionsKt.getOrNull((List)object4, (int)1) : null;
                        }
                    }
                    playerJsUrl = string;
                    matchResult = Regex.find$default((Regex)new Regex("\"STS\"\\s*:\\s*(\\d{4,6})"), (CharSequence)html, (int)0, (int)2, null);
                    signatureTs = matchResult != null && (list = matchResult.getGroupValues()) != null && (string2 = (String)CollectionsKt.getOrNull((List)list, (int)1)) != null && (n = StringsKt.toIntOrNull((String)string2)) != null ? n : signatureTs;
                    boolean bl3 = scrapeOk = !StringsKt.isBlank((CharSequence)html);
                    if (scrapeOk) {
                        LyreonLog.INSTANCE.i(TAG, "scrape ytcfg OK: versi=" + webVersion + " sts=" + signatureTs);
                    }
                    Unit unit = Unit.INSTANCE;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
                }
                object = Result.constructor-impl((Object)Unit.INSTANCE);
            }
            catch (Throwable bl) {
                object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            object2 = object;
            Throwable throwable = Result.exceptionOrNull-impl((Object)object2);
            if (throwable == null) break block13;
            Object e = object = throwable;
            boolean bl = false;
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "ensure() gagal scrape: " + ((Throwable)e).getMessage() + " \u2014 pakai nilai cadangan", null, 4, null);
        }
    }

    static {
        signatureTs = 20710;
        fetched = ConcurrentHashMap.newKeySet();
        visitorSource = "-";
        VISITOR_REGEX = new Regex("^Cg[t|s]");
    }
}
