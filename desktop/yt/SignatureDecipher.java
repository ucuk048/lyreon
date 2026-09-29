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
 *  org.mozilla.javascript.Context
 *  org.mozilla.javascript.Scriptable
 *  org.mozilla.javascript.ScriptableObject
 */
package com.lyreon.desktop.yt;

import androidx.compose.runtime.internal.StabilityInferred;
import com.lyreon.desktop.core.LyreonLog;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\b\u00c1\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0005J\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u0005H\u0002J\u0012\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u0005H\u0002J\"\u0010\u0013\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00ca\u0001\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\u0003\u0010\u0002\u00a8\u0006\u0017"}, d2={"Lcom/lyreon/desktop/yt/SignatureDecipher;", "", "<init>", "()V", "TAG", "", "cache", "Ljava/util/concurrent/ConcurrentHashMap;", "SIG_FN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "SIG_FN_ALT", "decipherS", "baseJs", "s", "decipherN", "n", "extractSignatureScript", "extractNScript", "runInRhino", "func", "fnName", "input", "Lyreon:desktop", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"})
@StabilityInferred(parameters=1)
@SourceDebugExtension(value={"SMAP\nSignatureDecipher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SignatureDecipher.kt\ncom/lyreon/desktop/yt/SignatureDecipher\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,121:1\n113#2,2:122\n113#2,2:125\n1#3:124\n1#3:127\n1#3:128\n*S KotlinDebug\n*F\n+ 1 SignatureDecipher.kt\ncom/lyreon/desktop/yt/SignatureDecipher\n*L\n24#1:122,2\n31#1:125,2\n24#1:124\n31#1:127\n*E\n"})
public final class SignatureDecipher {
    @NotNull
    public static final SignatureDecipher INSTANCE = new SignatureDecipher();
    @NotNull
    private static final String TAG = "SignatureDecipher";
    @NotNull
    private static final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap();
    private static final Pattern SIG_FN = Pattern.compile("(?:var\\s+)?([a-zA-Z0-9$]+)\\s*=\\s*function\\s*\\(([a-zA-Z0-9$]+)\\)\\{\\s*\\2=\\2\\.split\\(\"\"\\);([\\s\\S]*?)return \\2\\.join\\(\"\"\\)\\}");
    private static final Pattern SIG_FN_ALT = Pattern.compile("function\\s*\\(([a-zA-Z0-9$]+)\\)\\{\\s*\\1=\\1\\.split\\(\"\"\\);([\\s\\S]*?)return \\1\\.join\\(\"\"\\)\\}");
    @JvmField
    public static final int $stable;

    private SignatureDecipher() {
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public final String decipherS(@NotNull String baseJs, @NotNull String s) {
        void $this$getOrPut$iv;
        Intrinsics.checkNotNullParameter((Object)baseJs, (String)"baseJs");
        Intrinsics.checkNotNullParameter((Object)s, (String)"s");
        String key = "s:" + baseJs.hashCode();
        ConcurrentMap concurrentMap = cache;
        String key$iv = key;
        boolean $i$f$getOrPut = false;
        Object object = $this$getOrPut$iv.get(key$iv);
        if (object == null) {
            boolean bl = false;
            String string = INSTANCE.extractSignatureScript(baseJs);
            if (string == null) {
                string = "";
            }
            String default$iv = string;
            boolean bl2 = false;
            object = $this$getOrPut$iv.putIfAbsent(key$iv, default$iv);
            if (object == null) {
                object = default$iv;
            }
        }
        String script = (String)object;
        Intrinsics.checkNotNull((Object)script);
        if (StringsKt.isBlank((CharSequence)script)) {
            return null;
        }
        return this.runInRhino(script, "sig", s);
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public final String decipherN(@NotNull String baseJs, @NotNull String n) {
        void $this$getOrPut$iv;
        Intrinsics.checkNotNullParameter((Object)baseJs, (String)"baseJs");
        Intrinsics.checkNotNullParameter((Object)n, (String)"n");
        String key = "n:" + baseJs.hashCode();
        ConcurrentMap concurrentMap = cache;
        String key$iv = key;
        boolean $i$f$getOrPut = false;
        Object object = $this$getOrPut$iv.get(key$iv);
        if (object == null) {
            boolean bl = false;
            String string = INSTANCE.extractNScript(baseJs);
            if (string == null) {
                string = "";
            }
            String default$iv = string;
            boolean bl2 = false;
            object = $this$getOrPut$iv.putIfAbsent(key$iv, default$iv);
            if (object == null) {
                object = default$iv;
            }
        }
        String script = (String)object;
        Intrinsics.checkNotNull((Object)script);
        if (StringsKt.isBlank((CharSequence)script)) {
            return null;
        }
        return this.runInRhino(script, "n", n);
    }

    private final String extractSignatureScript(String baseJs) {
        String objName;
        String escapedObjName;
        Pattern helperPattern;
        Matcher hm;
        String funcBody = "";
        Object rawFunc = "";
        Matcher m1 = SIG_FN.matcher(baseJs);
        if (m1.find()) {
            String string = m1.group();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"group(...)");
            rawFunc = string;
            String string2 = m1.group(3);
            if (string2 == null) {
                string2 = "";
            }
            funcBody = string2;
        } else {
            Matcher m2 = SIG_FN_ALT.matcher(baseJs);
            if (m2.find()) {
                rawFunc = "var __sig = " + m2.group();
                String string = m2.group(2);
                if (string == null) {
                    string = "";
                }
                funcBody = string;
            } else {
                return null;
            }
        }
        Pattern objPattern = Pattern.compile("([a-zA-Z0-9$]+)\\.[a-zA-Z0-9$]+\\(");
        Matcher objMatcher = objPattern.matcher(funcBody);
        String helperObjDef = objMatcher.find() ? ((hm = (helperPattern = Pattern.compile("(?:var\\s+|const\\s+|let\\s+)?" + (escapedObjName = Pattern.quote(objName = objMatcher.group(1))) + "\\s*=\\s*\\{[\\s\\S]*?\\n*\\};")).matcher(baseJs)).find() ? hm.group() : "") : "";
        Object fullFn = StringsKt.startsWith$default((String)rawFunc, (String)"var __sig", (boolean)false, (int)2, null) ? rawFunc : "var __sig = " + (String)rawFunc + ";";
        return helperObjDef + "\n" + (String)fullFn;
    }

    private final String extractNScript(String baseJs) {
        Pattern[] patternArray = new Pattern[]{Pattern.compile("function\\s*\\([a-zA-Z0-9$]+\\)\\{[^{}]*\\}[\\s\\S]{0,150}?\\.join\\(\"\"\\)\\}"), Pattern.compile("([a-zA-Z0-9$]+)\\s*=\\s*function\\s*\\([a-zA-Z0-9$]+\\)\\{var [a-zA-Z0-9$]+=[a-zA-Z0-9$]+\\.split\\(\"\"\\)[\\s\\S]*?return [a-zA-Z0-9$]+\\.join\\(\"\"\\)\\}")};
        List nPatterns = CollectionsKt.listOf((Object[])patternArray);
        for (Pattern pattern : nPatterns) {
            String objName;
            String escapedObjName;
            Pattern hp;
            Matcher hm;
            Matcher m = pattern.matcher(baseJs);
            if (!m.find()) continue;
            String funcStr = m.group();
            Pattern helperPattern = Pattern.compile("([a-zA-Z0-9$]+)\\.[a-zA-Z0-9$]+\\(");
            Matcher objMatcher = helperPattern.matcher(funcStr);
            String helperObjDef = objMatcher.find() ? ((hm = (hp = Pattern.compile("(?:var\\s+|const\\s+|let\\s+)?" + (escapedObjName = Pattern.quote(objName = objMatcher.group(1))) + "\\s*=\\s*\\{[\\s\\S]*?\\n*\\};")).matcher(baseJs)).find() ? hm.group() : "") : "";
            return helperObjDef + "\nvar __n = " + funcStr + ";";
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final String runInRhino(String func, String fnName, String input) {
        Scriptable scriptable;
        try {
            Scriptable scope;
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1);
            try {
                Object object;
                ScriptableObject scriptableObject = cx.initStandardObjects();
                Intrinsics.checkNotNullExpressionValue((Object)scriptableObject, (String)"initStandardObjects(...)");
                scope = (Scriptable)scriptableObject;
                scope.put("window", scope, (Object)cx.newObject(scope));
                scope.put("globalThis", scope, (Object)scope);
                cx.evaluateString(scope, func, "def", 1, null);
                String quoted = StringsKt.replace$default((String)StringsKt.replace$default((String)input, (String)"\\", (String)"\\\\", (boolean)false, (int)4, null), (String)"\"", (String)"\\\"", (boolean)false, (int)4, null);
                String varName = Intrinsics.areEqual((Object)fnName, (Object)"n") ? "__n" : "__sig";
                String call = "(typeof " + varName + "==='function' ? " + varName + "(String(" + quoted + ")) : String(" + quoted + "))";
                Object out = Context.jsToJava((Object)cx.evaluateString(scope, call, "call", 1, null), Object.class);
                Object object2 = out;
                if (object2 != null && (object2 = object2.toString()) != null) {
                    Object object3;
                    Object it = object3 = object2;
                    boolean bl = false;
                    object = !StringsKt.isBlank((CharSequence)((CharSequence)it)) ? object3 : null;
                } else {
                    object = null;
                }
                scope = object;
            }
            finally {
                Context.exit();
            }
            scriptable = scope;
        }
        catch (Exception e) {
            LyreonLog.w$default(LyreonLog.INSTANCE, TAG, "Rhino eval (" + fnName + ") gagal: " + e.getMessage(), null, 4, null);
            scriptable = null;
        }
        return scriptable;
    }
}
