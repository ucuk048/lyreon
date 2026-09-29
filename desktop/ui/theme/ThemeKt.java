/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.material3.ColorScheme
 *  androidx.compose.material3.ColorSchemeKt
 *  androidx.compose.material3.MaterialThemeKt
 *  androidx.compose.runtime.Composable
 *  androidx.compose.runtime.ComposableInferredTarget
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.RecomposeScopeImplKt
 *  androidx.compose.runtime.ScopeUpdateScope
 *  androidx.compose.runtime.internal.FunctionKeyMeta
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.graphics.ColorKt
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.lyreon.desktop.ui.theme;

import androidx.compose.material3.ColorScheme;
import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.material3.MaterialThemeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 4, 0}, k=2, xi=48, d1={"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0002\b*\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a<\u0010.\u001a\u00020/2\b\b\u0002\u00100\u001a\u0002012\u0011\u00102\u001a\r\u0012\u0004\u0012\u00020/03\u00a2\u0006\u0002\b4H\u0007b\u0002\b4b\f\b6\u0012\b\b7\u0012\u0004\b\b(8\u00a2\u0006\u0002\u00105\"\u0013\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0002\u0010\u0003\"\u0013\u0010\u0005\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0006\u0010\u0003\"\u0013\u0010\u0007\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\b\u0010\u0003\"\u0013\u0010\t\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\n\u0010\u0003\"\u0013\u0010\u000b\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\f\u0010\u0003\"\u0013\u0010\r\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u000e\u0010\u0003\"\u0013\u0010\u000f\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0010\u0010\u0003\"\u0013\u0010\u0011\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0012\u0010\u0003\"\u0013\u0010\u0013\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0014\u0010\u0003\"\u0013\u0010\u0015\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0016\u0010\u0003\"\u0013\u0010\u0017\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0018\u0010\u0003\"\u0013\u0010\u0019\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u001a\u0010\u0003\"\u0013\u0010\u001b\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u001c\u0010\u0003\"\u0013\u0010\u001d\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u001e\u0010\u0003\"\u0013\u0010\u001f\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b \u0010\u0003\"\u0013\u0010!\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\"\u0010\u0003\"\u0013\u0010#\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b$\u0010\u0003\"\u0013\u0010%\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b&\u0010\u0003\"\u0013\u0010'\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b(\u0010\u0003\"\u0013\u0010)\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b*\u0010\u0003\"\u000e\u0010+\u001a\u00020,X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u000e\u0010-\u001a\u00020,X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00069"}, d2={"LyreonCrimson", "Landroidx/compose/ui/graphics/Color;", "getLyreonCrimson", "()J", "J", "LyreonBackground", "getLyreonBackground", "LyreonSurface", "getLyreonSurface", "LyreonElevated", "getLyreonElevated", "LyreonLine", "getLyreonLine", "LyreonLineSoft", "getLyreonLineSoft", "LyreonTextPrimary", "getLyreonTextPrimary", "LyreonTextSecondary", "getLyreonTextSecondary", "LyreonTextMuted", "getLyreonTextMuted", "LyreonHairline", "getLyreonHairline", "CrimsonPrimary", "getCrimsonPrimary", "CrimsonHover", "getCrimsonHover", "CrimsonLight", "getCrimsonLight", "DarkBackground", "getDarkBackground", "DarkSurface", "getDarkSurface", "DarkSurfaceElevated", "getDarkSurfaceElevated", "DarkBorder", "getDarkBorder", "DarkTextPrimary", "getDarkTextPrimary", "DarkTextSecondary", "getDarkTextSecondary", "DarkTextMuted", "getDarkTextMuted", "DarkColors", "Landroidx/compose/material3/ColorScheme;", "LightColors", "LyreonTheme", "", "darkTheme", "", "content", "Lkotlin/Function0;", "Landroidx/compose/runtime/Composable;", "(ZLkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V", "Landroidx/compose/runtime/ComposableInferredTarget;", "scheme", "[0[0]]", "Lyreon:desktop"})
public final class ThemeKt {
    private static final long LyreonCrimson = ColorKt.Color((long)4293477728L);
    private static final long LyreonBackground = ColorKt.Color((long)4278913808L);
    private static final long LyreonSurface = ColorKt.Color((long)4279505948L);
    private static final long LyreonElevated = ColorKt.Color((long)4280032294L);
    private static final long LyreonLine = ColorKt.Color((long)4280821810L);
    private static final long LyreonLineSoft = ColorKt.Color((long)0xFF22222CL);
    private static final long LyreonTextPrimary = ColorKt.Color((long)0xFFF4F4F8L);
    private static final long LyreonTextSecondary = ColorKt.Color((long)0xFFA3A3AFL);
    private static final long LyreonTextMuted = ColorKt.Color((long)4285624701L);
    private static final long LyreonHairline = ColorKt.Color((int)0x14FFFFFF);
    private static final long CrimsonPrimary = LyreonCrimson;
    private static final long CrimsonHover = ColorKt.Color((long)4292227917L);
    private static final long CrimsonLight = ColorKt.Color((long)4294929281L);
    private static final long DarkBackground = LyreonBackground;
    private static final long DarkSurface = LyreonSurface;
    private static final long DarkSurfaceElevated = LyreonElevated;
    private static final long DarkBorder = LyreonLine;
    private static final long DarkTextPrimary = LyreonTextPrimary;
    private static final long DarkTextSecondary = LyreonTextSecondary;
    private static final long DarkTextMuted = LyreonTextMuted;
    @NotNull
    private static final ColorScheme DarkColors = ColorSchemeKt.darkColorScheme-_VG5OTI$default((long)LyreonCrimson, (long)Color.Companion.getWhite-0d7_KjU(), (long)ColorKt.Color((long)4283172121L), (long)ColorKt.Color((long)0xFFFFD9DFL), (long)0L, (long)CrimsonLight, (long)Color.Companion.getBlack-0d7_KjU(), (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)LyreonBackground, (long)LyreonTextPrimary, (long)LyreonSurface, (long)LyreonTextPrimary, (long)LyreonElevated, (long)LyreonTextSecondary, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)LyreonLine, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (int)-67625072, (int)65535, null);
    @NotNull
    private static final ColorScheme LightColors = ColorSchemeKt.lightColorScheme-_VG5OTI$default((long)LyreonCrimson, (long)Color.Companion.getWhite-0d7_KjU(), (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)ColorKt.Color((long)4293979108L), (long)ColorKt.Color((long)4280492062L), (long)ColorKt.Color((long)4294439917L), (long)ColorKt.Color((long)4280492062L), (long)ColorKt.Color((long)4294769398L), (long)ColorKt.Color((long)4283846729L), (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)ColorKt.Color((long)4292925387L), (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (long)0L, (int)-67624964, (int)65535, null);

    public static final long getLyreonCrimson() {
        return LyreonCrimson;
    }

    public static final long getLyreonBackground() {
        return LyreonBackground;
    }

    public static final long getLyreonSurface() {
        return LyreonSurface;
    }

    public static final long getLyreonElevated() {
        return LyreonElevated;
    }

    public static final long getLyreonLine() {
        return LyreonLine;
    }

    public static final long getLyreonLineSoft() {
        return LyreonLineSoft;
    }

    public static final long getLyreonTextPrimary() {
        return LyreonTextPrimary;
    }

    public static final long getLyreonTextSecondary() {
        return LyreonTextSecondary;
    }

    public static final long getLyreonTextMuted() {
        return LyreonTextMuted;
    }

    public static final long getLyreonHairline() {
        return LyreonHairline;
    }

    public static final long getCrimsonPrimary() {
        return CrimsonPrimary;
    }

    public static final long getCrimsonHover() {
        return CrimsonHover;
    }

    public static final long getCrimsonLight() {
        return CrimsonLight;
    }

    public static final long getDarkBackground() {
        return DarkBackground;
    }

    public static final long getDarkSurface() {
        return DarkSurface;
    }

    public static final long getDarkSurfaceElevated() {
        return DarkSurfaceElevated;
    }

    public static final long getDarkBorder() {
        return DarkBorder;
    }

    public static final long getDarkTextPrimary() {
        return DarkTextPrimary;
    }

    public static final long getDarkTextSecondary() {
        return DarkTextSecondary;
    }

    public static final long getDarkTextMuted() {
        return DarkTextMuted;
    }

    @Composable
    @ComposableInferredTarget(scheme="[0[0]]")
    @FunctionKeyMeta(key=-48572235, startOffset=1985, endOffset=2215)
    public static final void LyreonTheme(boolean darkTheme, @NotNull Function2<? super Composer, ? super Integer, Unit> content, @Nullable Composer $composer, int $changed, int n) {
        block9: {
            Intrinsics.checkNotNullParameter(content, (String)"content");
            $composer = $composer.startRestartGroup(-48572235);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(LyreonTheme)N(darkTheme,content)68@2137L76:Theme.kt#nlij0j");
            int $dirty = $changed;
            if ((n & 1) != 0) {
                $dirty |= 6;
            } else if (($changed & 6) == 0) {
                $dirty |= $composer.changed(darkTheme) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changedInstance(content) ? 32 : 16;
            }
            if ($composer.shouldExecute(($dirty & 0x13) != 18, $dirty & 1)) {
                if ((n & 1) != 0) {
                    darkTheme = true;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-48572235, (int)$dirty, (int)-1, (String)"com.lyreon.desktop.ui.theme.LyreonTheme (Theme.kt:66)");
                }
                ColorScheme colors = darkTheme ? DarkColors : LightColors;
                MaterialThemeKt.MaterialTheme((ColorScheme)colors, null, null, content, (Composer)$composer, (int)(0x1C00 & $dirty << 6), (int)6);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block9;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> ThemeKt.LyreonTheme$lambda$0(darkTheme, content, $changed, n, arg_0, arg_1));
        }
    }

    private static final Unit LyreonTheme$lambda$0(boolean $darkTheme, Function2 $content, int $$changed, int $$default, Composer $composer, int $force) {
        ThemeKt.LyreonTheme($darkTheme, (Function2<? super Composer, ? super Integer, Unit>)$content, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }
}
