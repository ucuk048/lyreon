/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.foundation.BackgroundKt
 *  androidx.compose.foundation.BorderKt
 *  androidx.compose.foundation.ClickableKt
 *  androidx.compose.foundation.layout.Arrangement
 *  androidx.compose.foundation.layout.Arrangement$Vertical
 *  androidx.compose.foundation.layout.BoxKt
 *  androidx.compose.foundation.layout.BoxScope
 *  androidx.compose.foundation.layout.BoxScopeInstance
 *  androidx.compose.foundation.layout.ColumnKt
 *  androidx.compose.foundation.layout.ColumnScope
 *  androidx.compose.foundation.layout.ColumnScopeInstance
 *  androidx.compose.foundation.layout.PaddingKt
 *  androidx.compose.foundation.layout.SizeKt
 *  androidx.compose.foundation.layout.SpacerKt
 *  androidx.compose.foundation.shape.RoundedCornerShape
 *  androidx.compose.foundation.shape.RoundedCornerShapeKt
 *  androidx.compose.material.icons.Icons
 *  androidx.compose.material.icons.Icons$Filled
 *  androidx.compose.material.icons.filled.PlayArrowKt
 *  androidx.compose.material3.IconKt
 *  androidx.compose.material3.TextKt
 *  androidx.compose.runtime.Applier
 *  androidx.compose.runtime.Composable
 *  androidx.compose.runtime.ComposableTarget
 *  androidx.compose.runtime.ComposablesKt
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocalMap
 *  androidx.compose.runtime.RecomposeScopeImplKt
 *  androidx.compose.runtime.ScopeUpdateScope
 *  androidx.compose.runtime.Updater
 *  androidx.compose.runtime.internal.FunctionKeyMeta
 *  androidx.compose.ui.Alignment
 *  androidx.compose.ui.Alignment$Horizontal
 *  androidx.compose.ui.ComposedModifierKt
 *  androidx.compose.ui.Modifier
 *  androidx.compose.ui.draw.ClipKt
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.graphics.Shape
 *  androidx.compose.ui.graphics.vector.ImageVector
 *  androidx.compose.ui.layout.MeasurePolicy
 *  androidx.compose.ui.node.ComposeUiNode
 *  androidx.compose.ui.text.font.FontWeight
 *  androidx.compose.ui.text.style.TextOverflow
 *  androidx.compose.ui.unit.Dp
 *  androidx.compose.ui.unit.TextUnitKt
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.lyreon.desktop.ui.components;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.PlayArrowKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import com.lyreon.desktop.model.LyreonTrack;
import com.lyreon.desktop.ui.components.AsyncArtworkKt;
import com.lyreon.desktop.ui.theme.ThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 4, 0}, k=2, xi=48, d1={"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aG\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0007b\u0002\b\u000bb\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u000f"}, d2={"QuickCard", "", "track", "Lcom/lyreon/desktop/model/LyreonTrack;", "active", "", "onClick", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "(Lcom/lyreon/desktop/model/LyreonTrack;ZLkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "Landroidx/compose/runtime/Composable;", "Landroidx/compose/runtime/ComposableTarget;", "applier", "androidx.compose.ui.UiComposable", "Lyreon:desktop"})
@SourceDebugExtension(value={"SMAP\nQuickCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuickCard.kt\ncom/lyreon/desktop/ui/components/QuickCardKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,121:1\n118#2:122\n118#2:123\n118#2:124\n118#2:125\n118#2:158\n118#2:159\n118#2:192\n118#2:193\n118#2:194\n118#2:195\n118#2:232\n118#2:233\n118#2:265\n118#2:274\n118#2:307\n87#3:126\n84#3,9:127\n87#3:275\n84#3,9:276\n94#3:312\n94#3:316\n81#4,6:136\n88#4,6:151\n81#4,6:170\n88#4,6:185\n81#4,6:206\n88#4,6:221\n96#4:230\n81#4,6:243\n88#4,6:258\n96#4:268\n96#4:272\n81#4,6:285\n88#4,6:300\n96#4:311\n96#4:315\n402#5,9:142\n411#5:157\n402#5,9:176\n411#5:191\n402#5,9:212\n411#5,3:227\n402#5,9:249\n411#5:264\n412#5,2:266\n412#5,2:270\n402#5,9:291\n411#5:306\n412#5,2:309\n412#5,2:313\n70#6:160\n67#6,9:161\n70#6:196\n67#6,9:197\n77#6:231\n70#6:234\n68#6,8:235\n77#6:269\n77#6:273\n1#7:308\n*S KotlinDebug\n*F\n+ 1 QuickCard.kt\ncom/lyreon/desktop/ui/components/QuickCardKt\n*L\n32#1:122\n36#1:123\n40#1:124\n45#1:125\n49#1:158\n50#1:159\n55#1:192\n56#1:193\n64#1:194\n67#1:195\n81#1:232\n82#1:233\n91#1:265\n100#1:274\n110#1:307\n34#1:126\n34#1:127,9\n97#1:275\n97#1:276,9\n97#1:312\n34#1:316\n34#1:136,6\n34#1:151,6\n47#1:170,6\n47#1:185,6\n61#1:206,6\n61#1:221,6\n61#1:230\n78#1:243,6\n78#1:258,6\n78#1:268\n47#1:272\n97#1:285,6\n97#1:300,6\n97#1:311\n34#1:315\n34#1:142,9\n34#1:157\n47#1:176,9\n47#1:191\n61#1:212,9\n61#1:227,3\n78#1:249,9\n78#1:264\n78#1:266,2\n47#1:270,2\n97#1:291,9\n97#1:306\n97#1:309,2\n34#1:313,2\n47#1:160\n47#1:161,9\n61#1:196\n61#1:197,9\n61#1:231\n78#1:234\n78#1:235,8\n78#1:269\n47#1:273\n*E\n"})
public final class QuickCardKt {
    /*
     * WARNING - void declaration
     */
    @Composable
    @ComposableTarget(applier="androidx.compose.ui.UiComposable")
    @FunctionKeyMeta(key=-1035208157, startOffset=969, endOffset=4136)
    public static final void QuickCard(@NotNull LyreonTrack track, boolean active, @NotNull Function0<Unit> onClick, @Nullable Modifier modifier, @Nullable Composer $composer, int $changed, int n) {
        block30: {
            Intrinsics.checkNotNullParameter((Object)track, (String)"track");
            Intrinsics.checkNotNullParameter(onClick, (String)"onClick");
            $composer = $composer.startRestartGroup(-1035208157);
            ComposerKt.sourceInformation((Composer)$composer, (String)"C(QuickCard)N(track,active,onClick,modifier)33@1143L2991:QuickCard.kt#oqn87w");
            int $dirty = $changed;
            if (($changed & 6) == 0) {
                $dirty |= (($changed & 8) == 0 ? $composer.changed((Object)track) : $composer.changedInstance((Object)track)) ? 4 : 2;
            }
            if (($changed & 0x30) == 0) {
                $dirty |= $composer.changed(active) ? 32 : 16;
            }
            if (($changed & 0x180) == 0) {
                $dirty |= $composer.changedInstance(onClick) ? 256 : 128;
            }
            if ((n & 8) != 0) {
                $dirty |= 0xC00;
            } else if (($changed & 0xC00) == 0) {
                $dirty |= $composer.changed((Object)modifier) ? 2048 : 1024;
            }
            if ($composer.shouldExecute(($dirty & 0x493) != 1170, $dirty & 1)) {
                CharSequence charSequence;
                String modifier$iv;
                void $composer2;
                int $changed$iv$iv$iv;
                MeasurePolicy measurePolicy$iv$iv;
                Function0 factory$iv$iv$iv;
                void $composer$iv$iv$iv;
                int $changed$iv$iv;
                void modifier$iv$iv;
                void $composer$iv$iv;
                int $changed$iv;
                void modifier$iv2;
                void $composer$iv;
                void $composer3;
                void $changed$iv$iv$iv2;
                void measurePolicy$iv$iv2;
                void $composer$iv$iv$iv2;
                void $changed$iv$iv2;
                void modifier$iv$iv2;
                void $composer$iv$iv2;
                void modifier$iv3;
                void $changed$iv2;
                void $composer$iv2;
                if ((n & 8) != 0) {
                    modifier = (Modifier)Modifier.Companion;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)-1035208157, (int)$dirty, (int)-1, (String)"com.lyreon.desktop.ui.components.QuickCard (QuickCard.kt:30)");
                }
                int $this$dp$iv = 16;
                boolean $i$f$getDp = false;
                RoundedCornerShape cardShape = RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)$this$dp$iv));
                $this$dp$iv = 160;
                $i$f$getDp = false;
                Modifier modifier2 = BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)SizeKt.width-3ABfNKs((Modifier)modifier, (float)Dp.constructor-impl((float)$this$dp$iv)), (Shape)((Shape)cardShape)), (long)(active ? ThemeKt.getLyreonElevated() : ThemeKt.getLyreonSurface()), null, (int)2, null);
                $this$dp$iv = 1;
                $i$f$getDp = false;
                float f = Dp.constructor-impl((float)$this$dp$iv);
                $this$dp$iv = 12;
                $i$f$getDp = false;
                Modifier $this$dp$iv2 = PaddingKt.padding-qDBjuR0$default((Modifier)ClickableKt.clickable-oSLSa3U$default((Modifier)BorderKt.border-xT4_qwU((Modifier)modifier2, (float)f, (long)(active ? Color.copy-wmQWz5c$default((long)ThemeKt.getLyreonCrimson(), (float)0.6f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)ThemeKt.getLyreonLine(), (float)0.35f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null)), (Shape)((Shape)cardShape)), (boolean)false, null, null, null, onClick, (int)15, null), (float)0.0f, (float)0.0f, (float)0.0f, (float)Dp.constructor-impl((float)$this$dp$iv), (int)7, null);
                Composer composer = $composer;
                boolean bl = false;
                boolean $i$f$Column = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv2, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                Arrangement.Vertical verticalArrangement$iv = Arrangement.INSTANCE.getTop();
                Alignment.Horizontal horizontalAlignment$iv = Alignment.Companion.getStart();
                MeasurePolicy measurePolicy$iv = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)verticalArrangement$iv, (Alignment.Horizontal)horizontalAlignment$iv, (Composer)$composer$iv2, (int)(0xE & $changed$iv2 >> 3 | 0x70 & $changed$iv2 >> 3));
                void var16_18 = modifier$iv3;
                MeasurePolicy measurePolicy = measurePolicy$iv;
                void var18_20 = $composer$iv2;
                int n2 = 0x70 & $changed$iv2 << 3;
                boolean $i$f$Layout = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv2, (int)-1159599143, (String)"CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
                int compositeKeyHash$iv$iv = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer$iv$iv2, (int)0));
                CompositionLocalMap localMap$iv$iv = $composer$iv$iv2.getCurrentCompositionLocalMap();
                Modifier materialized$iv$iv = ComposedModifierKt.materializeModifier((Composer)$composer$iv$iv2, (Modifier)modifier$iv$iv2);
                Function0 function0 = ComposeUiNode.Companion.getConstructor();
                void var25_27 = $composer$iv$iv2;
                int n3 = 6 | 0x380 & $changed$iv$iv2 << 6;
                boolean $i$f$ReusableComposeNode = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv$iv2, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)410@16216L9:Composables.kt#9igjgp");
                if (!($composer$iv$iv$iv2.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer$iv$iv$iv2.startReusableNode();
                if ($composer$iv$iv$iv2.getInserting()) {
                    void factory$iv$iv$iv2;
                    $composer$iv$iv$iv2.createNode((Function0)factory$iv$iv$iv2);
                } else {
                    $composer$iv$iv$iv2.useNode();
                }
                Composer $this$Layout_u24lambda_u240$iv$iv = Updater.constructor-impl((Composer)$composer$iv$iv$iv2);
                boolean bl2 = false;
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv, (Object)measurePolicy$iv$iv2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv, (Object)localMap$iv$iv, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv, (Object)compositeKeyHash$iv$iv, (Function2)ComposeUiNode.Companion.getSetCompositeKeyHash());
                Updater.reconcile-impl((Composer)$this$Layout_u24lambda_u240$iv$iv, (Function1)ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv, (Object)materialized$iv$iv, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n4 = 0xE & $changed$iv$iv$iv2 >> 6;
                void $composer$iv3 = $composer$iv$iv$iv2;
                boolean bl3 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv3, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
                int n5 = 6 | 0x70 & $changed$iv2 >> 6;
                void var34_36 = $composer$iv3;
                ColumnScope $this$QuickCard_u24lambda_u240 = (ColumnScope)ColumnScopeInstance.INSTANCE;
                boolean bl4 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer3, (int)-1651701476, (String)"C46@1606L1774,96@3390L738:QuickCard.kt#oqn87w");
                int $this$dp$iv3 = 160;
                boolean $i$f$getDp2 = false;
                Modifier modifier3 = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)$this$dp$iv3));
                $this$dp$iv3 = 16;
                $i$f$getDp2 = false;
                float f2 = Dp.constructor-impl((float)$this$dp$iv3);
                $this$dp$iv3 = 16;
                $i$f$getDp2 = false;
                Modifier $this$dp$iv32 = ClipKt.clip((Modifier)modifier3, (Shape)((Shape)RoundedCornerShapeKt.RoundedCornerShape-a9UjIt4$default((float)f2, (float)Dp.constructor-impl((float)$this$dp$iv3), (float)0.0f, (float)0.0f, (int)12, null)));
                void var39_47 = $composer3;
                boolean bl5 = false;
                boolean $i$f$Box = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                Alignment contentAlignment$iv = Alignment.Companion.getTopStart();
                boolean propagateMinConstraints$iv = false;
                MeasurePolicy measurePolicy$iv2 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment$iv, (boolean)propagateMinConstraints$iv);
                void var44_53 = modifier$iv2;
                MeasurePolicy measurePolicy2 = measurePolicy$iv2;
                void var46_55 = $composer$iv;
                int n6 = 0x70 & $changed$iv << 3;
                boolean $i$f$Layout2 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv, (int)-1159599143, (String)"CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
                int compositeKeyHash$iv$iv2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer$iv$iv, (int)0));
                CompositionLocalMap localMap$iv$iv2 = $composer$iv$iv.getCurrentCompositionLocalMap();
                Modifier materialized$iv$iv2 = ComposedModifierKt.materializeModifier((Composer)$composer$iv$iv, (Modifier)modifier$iv$iv);
                Function0 function02 = ComposeUiNode.Companion.getConstructor();
                void var53_62 = $composer$iv$iv;
                int n7 = 6 | 0x380 & $changed$iv$iv << 6;
                boolean $i$f$ReusableComposeNode2 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv$iv, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)410@16216L9:Composables.kt#9igjgp");
                if (!($composer$iv$iv$iv.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer$iv$iv$iv.startReusableNode();
                if ($composer$iv$iv$iv.getInserting()) {
                    $composer$iv$iv$iv.createNode(factory$iv$iv$iv);
                } else {
                    $composer$iv$iv$iv.useNode();
                }
                Composer $this$Layout_u24lambda_u240$iv$iv2 = Updater.constructor-impl((Composer)$composer$iv$iv$iv);
                $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$iv = false;
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)measurePolicy$iv$iv, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)localMap$iv$iv2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)compositeKeyHash$iv$iv2, (Function2)ComposeUiNode.Companion.getSetCompositeKeyHash());
                Updater.reconcile-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Function1)ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)materialized$iv$iv2, (Function2)ComposeUiNode.Companion.getSetModifier());
                int n8 = 0xE & $changed$iv$iv$iv >> 6;
                void $composer$iv4 = $composer$iv$iv$iv;
                boolean bl6 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv4, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                int n9 = 6 | 0x70 & $changed$iv >> 6;
                void var62_71 = $composer$iv4;
                BoxScope $this$QuickCard_u24lambda_u240_u240 = (BoxScope)BoxScopeInstance.INSTANCE;
                boolean bl7 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer2, (int)618246943, (String)"C51@1773L224:QuickCard.kt#oqn87w");
                int $this$dp$iv4 = 160;
                boolean $i$f$getDp3 = false;
                float f3 = Dp.constructor-impl((float)$this$dp$iv4);
                $this$dp$iv4 = 0;
                $i$f$getDp3 = false;
                AsyncArtworkKt.AsyncArtwork-vz2T9sI(track.getThumbnailUrl(), track.getTitle(), f3, Dp.constructor-impl((float)$this$dp$iv4), SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (Composer)$composer2, 28032, 0);
                if (active) {
                    void $composer4;
                    void $changed$iv$iv$iv3;
                    void measurePolicy$iv$iv3;
                    void $composer$iv$iv$iv3;
                    void $changed$iv$iv3;
                    void $composer$iv$iv3;
                    void $composer$iv5;
                    $composer2.startReplaceGroup(618463849);
                    ComposerKt.sourceInformation((Composer)$composer2, (String)"60@2041L562");
                    $this$dp$iv4 = 8;
                    $i$f$getDp3 = false;
                    Modifier modifier4 = BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)PaddingKt.padding-3ABfNKs((Modifier)$this$QuickCard_u24lambda_u240_u240.align((Modifier)Modifier.Companion, Alignment.Companion.getTopEnd()), (float)Dp.constructor-impl((float)$this$dp$iv4)), (Shape)((Shape)RoundedCornerShapeKt.getCircleShape())), (long)ThemeKt.getLyreonCrimson(), null, (int)2, null);
                    $this$dp$iv4 = 8;
                    $i$f$getDp3 = false;
                    float f4 = Dp.constructor-impl((float)$this$dp$iv4);
                    $this$dp$iv4 = 3;
                    $i$f$getDp3 = false;
                    $this$dp$iv4 = PaddingKt.padding-VpY3zN4((Modifier)modifier4, (float)f4, (float)Dp.constructor-impl((float)$this$dp$iv4));
                    void var67_81 = $composer2;
                    boolean bl8 = false;
                    boolean $i$f$Box2 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv5, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    Alignment contentAlignment$iv2 = Alignment.Companion.getTopStart();
                    boolean propagateMinConstraints$iv2 = false;
                    measurePolicy$iv = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment$iv2, (boolean)propagateMinConstraints$iv2);
                    void var72_91 = modifier$iv;
                    MeasurePolicy measurePolicy3 = measurePolicy$iv;
                    void var74_97 = $composer$iv5;
                    int n10 = 0x70 & $changed$iv << 3;
                    $i$f$Layout = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv3, (int)-1159599143, (String)"CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
                    int compositeKeyHash$iv$iv3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer$iv$iv3, (int)0));
                    CompositionLocalMap localMap$iv$iv3 = $composer$iv$iv3.getCurrentCompositionLocalMap();
                    Modifier materialized$iv$iv3 = ComposedModifierKt.materializeModifier((Composer)$composer$iv$iv3, (Modifier)modifier$iv$iv);
                    Function0 function03 = ComposeUiNode.Companion.getConstructor();
                    void var81_110 = $composer$iv$iv3;
                    int n11 = 6 | 0x380 & $changed$iv$iv3 << 6;
                    boolean $i$f$ReusableComposeNode3 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv$iv3, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)410@16216L9:Composables.kt#9igjgp");
                    if (!($composer$iv$iv$iv3.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer$iv$iv$iv3.startReusableNode();
                    if ($composer$iv$iv$iv3.getInserting()) {
                        void factory$iv$iv$iv3;
                        $composer$iv$iv$iv3.createNode((Function0)factory$iv$iv$iv3);
                    } else {
                        $composer$iv$iv$iv3.useNode();
                    }
                    Composer $this$Layout_u24lambda_u240$iv$iv3 = Updater.constructor-impl((Composer)$composer$iv$iv$iv3);
                    $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$iv = false;
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv3, (Object)measurePolicy$iv$iv3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv3, (Object)localMap$iv$iv3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv3, (Object)compositeKeyHash$iv$iv3, (Function2)ComposeUiNode.Companion.getSetCompositeKeyHash());
                    Updater.reconcile-impl((Composer)$this$Layout_u24lambda_u240$iv$iv3, (Function1)ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv3, (Object)materialized$iv$iv3, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int n12 = 0xE & $changed$iv$iv$iv3 >> 6;
                    void $composer$iv6 = $composer$iv$iv$iv3;
                    $i$a$-Layout-BoxKt$Box$1$iv = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv6, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int n13 = 6 | 0x70 & $changed$iv >> 6;
                    void var90_128 = $composer$iv6;
                    BoxScope $this$QuickCard_u24lambda_u240_u240_u240 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl9 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer4, (int)379127501, (String)"C68@2377L208:QuickCard.kt#oqn87w");
                    long l = TextUnitKt.getSp((int)9);
                    FontWeight fontWeight = FontWeight.Companion.getBold();
                    long l2 = Color.Companion.getWhite-0d7_KjU();
                    TextKt.Text-Nvy7gAk((String)"MEMUTAR", null, (long)l2, null, (long)l, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)$composer4, (int)0x186186, (int)0, (int)262058);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer4);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv6);
                    $composer$iv$iv$iv3.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv$iv3);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv3);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv5);
                    $composer2.endReplaceGroup();
                } else {
                    void contentAlignment$iv3;
                    $composer2.startReplaceGroup(619063792);
                    ComposerKt.sourceInformation((Composer)$composer2, (String)"77@2713L643");
                    $this$dp$iv4 = 8;
                    $i$f$getDp3 = false;
                    Modifier modifier5 = PaddingKt.padding-3ABfNKs((Modifier)$this$QuickCard_u24lambda_u240_u240.align((Modifier)Modifier.Companion, Alignment.Companion.getBottomEnd()), (float)Dp.constructor-impl((float)$this$dp$iv4));
                    $this$dp$iv4 = 32;
                    $i$f$getDp3 = false;
                    $this$dp$iv4 = BackgroundKt.background-bw27NRU$default((Modifier)ClipKt.clip((Modifier)SizeKt.size-3ABfNKs((Modifier)modifier5, (float)Dp.constructor-impl((float)$this$dp$iv4)), (Shape)((Shape)RoundedCornerShapeKt.getCircleShape())), (long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.6f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), null, (int)2, null);
                    Alignment $i$f$getDp4 = Alignment.Companion.getCenter();
                    void $composer$iv5 = $composer2;
                    $changed$iv = 48;
                    boolean $i$f$Box3 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv5, (int)1042775818, (String)"CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
                    boolean propagateMinConstraints$iv3 = false;
                    measurePolicy$iv = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)contentAlignment$iv3, (boolean)propagateMinConstraints$iv3);
                    modifier$iv$iv = modifier$iv;
                    MeasurePolicy measurePolicy$iv$iv3 = measurePolicy$iv;
                    void $composer$iv$iv3 = $composer$iv5;
                    int $changed$iv$iv3 = 0x70 & $changed$iv << 3;
                    $i$f$Layout = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv3, (int)-1159599143, (String)"CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
                    int compositeKeyHash$iv$iv4 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer$iv$iv3, (int)0));
                    CompositionLocalMap localMap$iv$iv4 = $composer$iv$iv3.getCurrentCompositionLocalMap();
                    Modifier materialized$iv$iv4 = ComposedModifierKt.materializeModifier((Composer)$composer$iv$iv3, (Modifier)modifier$iv$iv);
                    Function0 factory$iv$iv$iv3 = ComposeUiNode.Companion.getConstructor();
                    void $composer$iv$iv$iv3 = $composer$iv$iv3;
                    int $changed$iv$iv$iv3 = 6 | 0x380 & $changed$iv$iv3 << 6;
                    boolean $i$f$ReusableComposeNode4 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv$iv3, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)410@16216L9:Composables.kt#9igjgp");
                    if (!($composer$iv$iv$iv3.getApplier() instanceof Applier)) {
                        ComposablesKt.invalidApplier();
                    }
                    $composer$iv$iv$iv3.startReusableNode();
                    if ($composer$iv$iv$iv3.getInserting()) {
                        $composer$iv$iv$iv3.createNode(factory$iv$iv$iv3);
                    } else {
                        $composer$iv$iv$iv3.useNode();
                    }
                    Composer $this$Layout_u24lambda_u240$iv$iv4 = Updater.constructor-impl((Composer)$composer$iv$iv$iv3);
                    $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$iv = false;
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv4, (Object)measurePolicy$iv$iv3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv4, (Object)localMap$iv$iv4, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv4, (Object)compositeKeyHash$iv$iv4, (Function2)ComposeUiNode.Companion.getSetCompositeKeyHash());
                    Updater.reconcile-impl((Composer)$this$Layout_u24lambda_u240$iv$iv4, (Function1)ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
                    Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv4, (Object)materialized$iv$iv4, (Function2)ComposeUiNode.Companion.getSetModifier());
                    int $changed$iv3 = 0xE & $changed$iv$iv$iv3 >> 6;
                    void $composer$iv7 = $composer$iv$iv$iv3;
                    $i$a$-Layout-BoxKt$Box$1$iv = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv7, (int)1833054614, (String)"C72@3469L9:Box.kt#2w3rfo");
                    int $changed2 = 6 | 0x70 & $changed$iv >> 6;
                    void $composer4 = $composer$iv7;
                    BoxScope $this$QuickCard_u24lambda_u240_u240_u241 = (BoxScope)BoxScopeInstance.INSTANCE;
                    boolean bl10 = false;
                    ComposerKt.sourceInformationMarkerStart((Composer)$composer4, (int)841522418, (String)"C86@3094L244:QuickCard.kt#oqn87w");
                    ImageVector imageVector = PlayArrowKt.getPlayArrow((Icons.Filled)Icons.INSTANCE.getDefault());
                    long l = Color.Companion.getWhite-0d7_KjU();
                    int $this$dp$iv5 = 18;
                    boolean $i$f$getDp5 = false;
                    Modifier modifier6 = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)$this$dp$iv5));
                    IconKt.Icon-ww6aTOc((ImageVector)imageVector, (String)"Putar", (Modifier)modifier6, (long)l, (Composer)$composer4, (int)3504, (int)0);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer4);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv7);
                    $composer$iv$iv$iv3.endNode();
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv$iv3);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv3);
                    ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv5);
                    $composer2.endReplaceGroup();
                }
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer2);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv4);
                $composer$iv$iv$iv.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv$iv);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv);
                $this$dp$iv = 12;
                $i$f$getDp = false;
                float f5 = Dp.constructor-impl((float)$this$dp$iv);
                $this$dp$iv = 10;
                $i$f$getDp = false;
                Modifier $this$dp$iv5 = PaddingKt.padding-VpY3zN4((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)f5, (float)Dp.constructor-impl((float)$this$dp$iv));
                $composer$iv = $composer3;
                $changed$iv = 6;
                boolean $i$f$Column2 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv, (int)1341605231, (String)"CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                Arrangement.Vertical verticalArrangement$iv2 = Arrangement.INSTANCE.getTop();
                Alignment.Horizontal horizontalAlignment$iv2 = Alignment.Companion.getStart();
                measurePolicy$iv2 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)verticalArrangement$iv2, (Alignment.Horizontal)horizontalAlignment$iv2, (Composer)$composer$iv, (int)(0xE & $changed$iv >> 3 | 0x70 & $changed$iv >> 3));
                modifier$iv$iv = modifier$iv;
                measurePolicy$iv$iv = measurePolicy$iv2;
                $composer$iv$iv = $composer$iv;
                $changed$iv$iv = 0x70 & $changed$iv << 3;
                $i$f$Layout2 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv, (int)-1159599143, (String)"CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
                compositeKeyHash$iv$iv2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode((Composer)$composer$iv$iv, (int)0));
                localMap$iv$iv2 = $composer$iv$iv.getCurrentCompositionLocalMap();
                materialized$iv$iv2 = ComposedModifierKt.materializeModifier((Composer)$composer$iv$iv, (Modifier)modifier$iv$iv);
                factory$iv$iv$iv = ComposeUiNode.Companion.getConstructor();
                $composer$iv$iv$iv = $composer$iv$iv;
                $changed$iv$iv$iv = 6 | 0x380 & $changed$iv$iv << 6;
                $i$f$ReusableComposeNode2 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv$iv$iv, (int)-553112988, (String)"CC(ReusableComposeNode)N(factory,update,content)410@16216L9:Composables.kt#9igjgp");
                if (!($composer$iv$iv$iv.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer$iv$iv$iv.startReusableNode();
                if ($composer$iv$iv$iv.getInserting()) {
                    $composer$iv$iv$iv.createNode(factory$iv$iv$iv);
                } else {
                    $composer$iv$iv$iv.useNode();
                }
                $this$Layout_u24lambda_u240$iv$iv2 = Updater.constructor-impl((Composer)$composer$iv$iv$iv);
                $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$iv = false;
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)measurePolicy$iv$iv, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)localMap$iv$iv2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)compositeKeyHash$iv$iv2, (Function2)ComposeUiNode.Companion.getSetCompositeKeyHash());
                Updater.reconcile-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Function1)ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
                Updater.set-impl((Composer)$this$Layout_u24lambda_u240$iv$iv2, (Object)materialized$iv$iv2, (Function2)ComposeUiNode.Companion.getSetModifier());
                int $changed$iv4 = 0xE & $changed$iv$iv$iv >> 6;
                $composer$iv4 = $composer$iv$iv$iv;
                $i$a$-Layout-ColumnKt$Column$1$iv = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer$iv4, (int)2093002350, (String)"C89@4557L9:Column.kt#2w3rfo");
                int $changed3 = 6 | 0x70 & $changed$iv >> 6;
                $composer2 = $composer$iv4;
                ColumnScope $this$QuickCard_u24lambda_u240_u241 = (ColumnScope)ColumnScopeInstance.INSTANCE;
                boolean bl11 = false;
                ComposerKt.sourceInformationMarkerStart((Composer)$composer2, (int)128335635, (String)"C101@3549L261,109@3823L40,110@3876L242:QuickCard.kt#oqn87w");
                modifier$iv = track.getTitle();
                long l = TextUnitKt.getSp((int)13);
                FontWeight fontWeight = FontWeight.Companion.getSemiBold();
                long l3 = ThemeKt.getLyreonTextPrimary();
                int n14 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                TextKt.Text-Nvy7gAk((String)modifier$iv, null, (long)l3, null, (long)l, null, (FontWeight)fontWeight, null, (long)0L, null, null, (long)0L, (int)n14, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer2, (int)1597440, (int)24960, (int)241578);
                $this$dp$iv = 2;
                boolean $i$f$getDp6 = false;
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)$this$dp$iv)), (Composer)$composer2, (int)6);
                CharSequence charSequence2 = track.getArtist();
                if (StringsKt.isBlank((CharSequence)charSequence2)) {
                    boolean bl12 = false;
                    charSequence = "YouTube Music";
                } else {
                    charSequence = charSequence2;
                }
                String string = (String)charSequence;
                l = TextUnitKt.getSp((int)11);
                long l4 = ThemeKt.getLyreonTextSecondary();
                int n15 = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                TextKt.Text-Nvy7gAk((String)string, null, (long)l4, null, (long)l, null, null, null, (long)0L, null, null, (long)0L, (int)n15, (boolean)false, (int)1, (int)0, null, null, (Composer)$composer2, (int)24576, (int)24960, (int)241642);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer2);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv4);
                $composer$iv$iv$iv.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv$iv);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer3);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv3);
                $composer$iv$iv$iv2.endNode();
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv$iv2);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv$iv2);
                ComposerKt.sourceInformationMarkerEnd((Composer)$composer$iv2);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                $composer.skipToGroupEnd();
            }
            ScopeUpdateScope scopeUpdateScope = $composer.endRestartGroup();
            if (scopeUpdateScope == null) break block30;
            scopeUpdateScope.updateScope((arg_0, arg_1) -> QuickCardKt.QuickCard$lambda$1(track, active, onClick, modifier, $changed, n, arg_0, arg_1));
        }
    }

    private static final Unit QuickCard$lambda$1(LyreonTrack $track, boolean $active, Function0 $onClick, Modifier $modifier, int $$changed, int $$default, Composer $composer, int $force) {
        QuickCardKt.QuickCard($track, $active, (Function0<Unit>)$onClick, $modifier, $composer, RecomposeScopeImplKt.updateChangedFlags((int)($$changed | 1)), $$default);
        return Unit.INSTANCE;
    }
}
