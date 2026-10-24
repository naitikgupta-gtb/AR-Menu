package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.ArMenuDish
import com.example.model.ArNutritionHotspot
import com.example.model.IngredientLayer3D
import com.example.model.LightingPreset
import com.example.model.ViewerMode
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * 4D Smart Menu Spatial AR Viewport:
 * 1. ROOM_AR_TABLE: 1:1 true-scale table projection with wooden tabletop surface, reticle dots,
 *    and FLOATING AR NUTRITION & INGREDIENT HOLOGRAPHIC CALLOUTS (matching Video 4!).
 * 2. ANIMATED 3D CHEF MASCOT standing on the plate welcoming the guest (matching Video 1 & 2!).
 * 3. ORBITAL_3D: 60fps 3D multi-layer dish turntable with touch drag orbit & pinch zoom.
 * 4. MODEL_VIEWER_WEB: Embedded self-hosted <model-viewer> engine with AR hotspot annotations.
 */
@Composable
fun Interactive3DOrbitalCanvas(
    dish: ArMenuDish,
    viewerMode: ViewerMode,
    autoRotate: Boolean,
    explodeLayers: Boolean,
    showChefMascot: Boolean,
    showNutritionHotspots: Boolean,
    trueScale1To1: Boolean,
    lightingPreset: LightingPreset,
    onLaunchNativeSceneViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (viewerMode == ViewerMode.MODEL_VIEWER_WEB) {
        EmbeddedWebArModelViewer(
            dishId = dish.id,
            onLaunchNativeAr = onLaunchNativeSceneViewer,
            modifier = modifier
        )
        return
    }

    var userYaw by remember(dish.id) { mutableFloatStateOf(0.35f) }
    var userPitch by remember(dish.id) { mutableFloatStateOf(0.48f) }
    var userZoom by remember(dish.id) { mutableFloatStateOf(1.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "orbit_rotation")
    val continuousSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "continuous_yaw"
    )

    val chefWaveAnim by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chef_wave"
    )

    val explodeFactor by animateFloatAsState(
        targetValue = if (explodeLayers) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f),
        label = "explode_factor"
    )

    val scaleFactor by animateFloatAsState(
        targetValue = if (trueScale1To1) 1.0f else 0.84f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
        label = "scale_factor"
    )

    val textMeasurer = rememberTextMeasurer()
    val effectiveYaw = if (autoRotate) userYaw + continuousSpin else userYaw

    Box(
        modifier = modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(lightingPreset.topBgHex),
                        Color(lightingPreset.bottomBgHex)
                    )
                )
            )
            .pointerInput(dish.id) {
                detectTransformGestures { _, pan, zoomChange, _ ->
                    userYaw += pan.x * 0.008f
                    userPitch = (userPitch + pan.y * 0.006f).coerceIn(0.18f, 1.15f)
                    userZoom = (userZoom * zoomChange).coerceIn(0.70f, 1.65f)
                }
            }
            .testTag("interactive_3d_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f
            val cy = h * 0.62f
            val tiltRatio = sin(userPitch).coerceIn(0.24f, 0.88f)
            val effectiveScale = min(w, h) * 0.35f * userZoom * scaleFactor

            if (viewerMode == ViewerMode.ROOM_AR_TABLE) {
                drawRoomTableSurface(cx, cy, w, h, tiltRatio, effectiveYaw)
            } else {
                drawStudioSpatialGrid(cx, cy, effectiveScale, tiltRatio, Color(lightingPreset.rimHex))
            }

            // Contact shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.58f),
                topLeft = Offset(
                    cx - effectiveScale * 0.98f,
                    cy - effectiveScale * 0.98f * tiltRatio + 8.dp.toPx()
                ),
                size = Size(effectiveScale * 1.96f, effectiveScale * 1.96f * tiltRatio)
            )

            // 3D Dish Layers
            dish.layers3D.forEachIndexed { index, layer ->
                draw3DIngredientLayer(
                    cx = cx,
                    baseCy = cy,
                    scale = effectiveScale,
                    tiltRatio = tiltRatio,
                    yaw = effectiveYaw,
                    layer = layer,
                    layerIndex = index,
                    explodeProgress = explodeFactor,
                    exposure = lightingPreset.exposure,
                    textMeasurer = textMeasurer,
                    showCallout = explodeFactor > 0.35f
                )
            }

            // Miniature 3D Chef Mascot (from Video 1 & 2!)
            if (showChefMascot) {
                draw3DChefMascot(
                    cx = cx,
                    cy = cy - effectiveScale * 0.52f,
                    scale = effectiveScale * 0.32f,
                    waveAngle = chefWaveAnim,
                    textMeasurer = textMeasurer
                )
            }

            // Floating AR Holographic Nutrition & Ingredient Callouts (from Video 4!)
            if (showNutritionHotspots && dish.nutritionHotspots.isNotEmpty()) {
                drawFloatingNutritionHotspots(
                    cx = cx,
                    cy = cy,
                    scale = effectiveScale,
                    tiltRatio = tiltRatio,
                    hotspots = dish.nutritionHotspots,
                    textMeasurer = textMeasurer
                )
            }

            // 1:1 Scale Dimension Measurement Ring
            drawDimensionRing(
                cx = cx,
                cy = cy,
                radius = effectiveScale * 1.05f,
                tiltRatio = tiltRatio,
                dimensionsText = "${dish.dimensionsLabel} • 1:1 Life Size on Table",
                textMeasurer = textMeasurer
            )
        }

        // Top Telemetry Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = ObsidianBg.copy(alpha = 0.88f),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronGold.copy(alpha = 0.45f))
            ) {
                Text(
                    text = if (viewerMode == ViewerMode.ROOM_AR_TABLE) "✨ 4D AR TABLE ACTIVE" else "⬡ 3D ORBITAL VIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = SaffronGold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Surface(
                color = ObsidianBg.copy(alpha = 0.88f),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpatialEmerald.copy(alpha = 0.45f))
            ) {
                Text(
                    text = "Holographic Callouts Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = SpatialEmerald,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * Draws the exact Floating Holographic AR Nutrition and Ingredient Callout Cards
 * (as seen in Video 4: The Golden Oak / 4D Smart Menu).
 */
private fun DrawScope.drawFloatingNutritionHotspots(
    cx: Float,
    cy: Float,
    scale: Float,
    tiltRatio: Float,
    hotspots: List<ArNutritionHotspot>,
    textMeasurer: TextMeasurer
) {
    hotspots.forEach { hotspot ->
        val targetX = cx + (hotspot.xOffsetRatio * scale * 0.95f)
        val targetY = cy + (hotspot.yOffsetRatio * scale * tiltRatio * 0.75f)

        // Floating card position above the dish surface
        val cardX = if (hotspot.xOffsetRatio < 0) {
            (targetX - 110.dp.toPx()).coerceAtLeast(10.dp.toPx())
        } else {
            (targetX + 22.dp.toPx()).coerceAtMost(size.width - 125.dp.toPx())
        }
        val cardY = (targetY - 55.dp.toPx()).coerceAtLeast(40.dp.toPx())

        // 1. Glowing anchor point on the food ingredient
        drawCircle(
            color = SaffronGold,
            radius = 3.5.dp.toPx(),
            center = Offset(targetX, targetY)
        )
        drawCircle(
            color = SaffronGold.copy(alpha = 0.35f),
            radius = 7.dp.toPx(),
            center = Offset(targetX, targetY)
        )

        // 2. AR connection callout line with elbow
        val elbowX = if (hotspot.xOffsetRatio < 0) cardX + 115.dp.toPx() else cardX
        val elbowY = cardY + 22.dp.toPx()

        drawLine(
            color = SaffronGold.copy(alpha = 0.85f),
            start = Offset(targetX, targetY),
            end = Offset(elbowX, elbowY),
            strokeWidth = 1.3.dp.toPx()
        )

        // 3. Floating Holographic Card Container
        val cardWidth = 115.dp.toPx()
        val cardHeight = 44.dp.toPx()

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xDD1F1A17),
                    Color(0xF014110F)
                ),
                startY = cardY,
                endY = cardY + cardHeight
            ),
            topLeft = Offset(cardX, cardY),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )

        drawRoundRect(
            color = SaffronGold.copy(alpha = 0.65f),
            topLeft = Offset(cardX, cardY),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        // 4. Ingredient Title & Icon
        val titleText = "${hotspot.iconEmoji} ${hotspot.name}"
        val titleLayout = textMeasurer.measure(
            text = titleText,
            style = TextStyle(
                color = Color(0xFFF7F2EB),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFamily
            )
        )
        drawText(
            textLayoutResult = titleLayout,
            topLeft = Offset(cardX + 7.dp.toPx(), cardY + 5.dp.toPx())
        )

        // 5. Benefit Pills (e.g. "Low Carbs", "High Protein")
        val benefitText = "• ${hotspot.benefit1}\n• ${hotspot.benefit2}"
        val benefitLayout = textMeasurer.measure(
            text = benefitText,
            style = TextStyle(
                color = SpatialEmerald,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = JetBrainsMonoFamily,
                lineHeight = 11.sp
            )
        )
        drawText(
            textLayoutResult = benefitLayout,
            topLeft = Offset(cardX + 7.dp.toPx(), cardY + 20.dp.toPx())
        )
    }
}

/**
 * Draws the Miniature 3D Animated Chef Mascot on the dish (as seen in Video 1 & Video 2).
 */
private fun DrawScope.draw3DChefMascot(
    cx: Float,
    cy: Float,
    scale: Float,
    waveAngle: Float,
    textMeasurer: TextMeasurer
) {
    // Pedestal
    drawOval(
        color = Color(0xFFD4AF37),
        topLeft = Offset(cx - scale * 0.35f, cy + scale * 0.70f),
        size = Size(scale * 0.70f, scale * 0.22f)
    )

    // Chef Body / White Apron
    drawRoundRect(
        color = Color(0xFFF5F5F5),
        topLeft = Offset(cx - scale * 0.22f, cy + scale * 0.15f),
        size = Size(scale * 0.44f, scale * 0.55f),
        cornerRadius = CornerRadius(8.dp.toPx())
    )

    // Red neckerchief
    drawOval(
        color = Color(0xFFC62828),
        topLeft = Offset(cx - scale * 0.14f, cy + scale * 0.12f),
        size = Size(scale * 0.28f, scale * 0.12f)
    )

    // Chef Head
    drawCircle(
        color = Color(0xFFFFD1A4),
        radius = scale * 0.20f,
        center = Offset(cx, cy - scale * 0.05f)
    )

    // Black Mustache
    drawOval(
        color = Color(0xFF212121),
        topLeft = Offset(cx - scale * 0.12f, cy - scale * 0.02f),
        size = Size(scale * 0.24f, scale * 0.08f)
    )

    // Eyes
    drawCircle(color = Color(0xFF1E1E1E), radius = 2.dp.toPx(), center = Offset(cx - scale * 0.07f, cy - scale * 0.08f))
    drawCircle(color = Color(0xFF1E1E1E), radius = 2.dp.toPx(), center = Offset(cx + scale * 0.07f, cy - scale * 0.08f))

    // Chef Hat (Toque Blanche)
    val hatPath = Path().apply {
        moveTo(cx - scale * 0.22f, cy - scale * 0.22f)
        cubicTo(
            cx - scale * 0.32f, cy - scale * 0.55f,
            cx + scale * 0.32f, cy - scale * 0.55f,
            cx + scale * 0.22f, cy - scale * 0.22f
        )
        close()
    }
    drawPath(path = hatPath, color = Color.White)
    drawRoundRect(
        color = Color(0xFFE0E0E0),
        topLeft = Offset(cx - scale * 0.22f, cy - scale * 0.22f),
        size = Size(scale * 0.44f, scale * 0.08f),
        cornerRadius = CornerRadius(2.dp.toPx())
    )

    // Waving Hand
    val handRad = Math.toRadians(waveAngle.toDouble()).toFloat()
    val handX = cx + scale * 0.32f + 8.dp.toPx() * cos(handRad)
    val handY = cy + scale * 0.18f - 12.dp.toPx() * sin(handRad)

    drawLine(
        color = Color.White,
        start = Offset(cx + scale * 0.20f, cy + scale * 0.28f),
        end = Offset(handX, handY),
        strokeWidth = 3.dp.toPx()
    )
    drawCircle(
        color = Color(0xFFFFD1A4),
        radius = 4.dp.toPx(),
        center = Offset(handX, handY)
    )

    // Speech bubble: "Welcome to 4D Menu!"
    val speechLayout = textMeasurer.measure(
        text = "Welcome to 4D Menu! ✨",
        style = TextStyle(
            color = Color(0xFF14110F),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSansFamily
        )
    )
    val bubbleW = speechLayout.size.width + 12.dp.toPx()
    val bubbleH = speechLayout.size.height + 6.dp.toPx()
    val bubbleX = cx - bubbleW * 0.5f
    val bubbleY = cy - scale * 0.65f - bubbleH

    drawRoundRect(
        color = Color(0xFFF7F2EB),
        topLeft = Offset(bubbleX, bubbleY),
        size = Size(bubbleW, bubbleH),
        cornerRadius = CornerRadius(6.dp.toPx())
    )
    drawText(
        textLayoutResult = speechLayout,
        topLeft = Offset(bubbleX + 6.dp.toPx(), bubbleY + 3.dp.toPx())
    )
}

private fun DrawScope.drawStudioSpatialGrid(
    cx: Float,
    cy: Float,
    scale: Float,
    tiltRatio: Float,
    accentColor: Color
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
    listOf(0.75f, 1.05f, 1.32f).forEachIndexed { idx, mult ->
        val rx = scale * mult
        val ry = rx * tiltRatio
        drawOval(
            color = accentColor.copy(alpha = if (idx == 1) 0.35f else 0.16f),
            topLeft = Offset(cx - rx, cy - ry),
            size = Size(rx * 2f, ry * 2f),
            style = Stroke(
                width = if (idx == 1) 1.8.dp.toPx() else 1.dp.toPx(),
                pathEffect = if (idx == 1) dashEffect else null
            )
        )
    }
}

private fun DrawScope.drawRoomTableSurface(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    tiltRatio: Float,
    yaw: Float
) {
    val horizonY = cy - h * 0.22f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2B1E16),
                Color(0xFF3E2B1F),
                Color(0xFF1E140E)
            ),
            startY = horizonY,
            endY = h
        ),
        topLeft = Offset(0f, horizonY),
        size = Size(w, h - horizonY)
    )

    val dotSpacing = 28.dp.toPx()
    val cols = 9
    val rows = 6
    for (r in -rows..rows) {
        for (c in -cols..cols) {
            val gx = cx + c * dotSpacing + (r * 6f * sin(yaw * 0.2f))
            val gy = cy + r * dotSpacing * tiltRatio
            if (gy > horizonY + 12f && gy < h - 12f) {
                val distNorm = (kotlin.math.hypot((gx - cx).toDouble(), ((gy - cy) / tiltRatio).toDouble()) / (w * 0.48)).toFloat()
                val alpha = (1f - distNorm).coerceIn(0f, 1f) * 0.55f
                if (alpha > 0.05f) {
                    drawCircle(
                        color = SpatialEmerald.copy(alpha = alpha),
                        radius = 2.2.dp.toPx(),
                        center = Offset(gx, gy)
                    )
                }
            }
        }
    }
}

private fun DrawScope.draw3DIngredientLayer(
    cx: Float,
    baseCy: Float,
    scale: Float,
    tiltRatio: Float,
    yaw: Float,
    layer: IngredientLayer3D,
    layerIndex: Int,
    explodeProgress: Float,
    exposure: Float,
    textMeasurer: TextMeasurer,
    showCallout: Boolean
) {
    val explodeLift = layerIndex * 20.dp.toPx() * explodeProgress
    val yCenterPx = baseCy - (layer.yCenterRatio * scale * 1.15f) - explodeLift
    val layerHeightPx = max(6.dp.toPx(), layer.heightRatio * scale * 1.15f)
    val rTop = layer.radiusTopRatio * scale
    val rBot = layer.radiusBottomRatio * scale

    val yBottom = yCenterPx + layerHeightPx * 0.5f
    val yTop = yCenterPx - layerHeightPx * 0.5f

    val baseColor = applyExposure(Color(layer.baseColorHex), exposure * 0.88f)
    val topColor = applyExposure(Color(layer.topColorHex), exposure * 1.05f)
    val highlightColor = applyExposure(Color(layer.topColorHex), exposure * 1.22f)

    val sidePath = Path().apply {
        moveTo(cx - rBot, yBottom)
        lineTo(cx - rTop, yTop)
        lineTo(cx + rTop, yTop)
        lineTo(cx + rBot, yBottom)
        close()
    }

    drawPath(
        path = sidePath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.95f),
                highlightColor,
                baseColor
            ),
            startX = cx - max(rBot, rTop),
            endX = cx + max(rBot, rTop)
        )
    )

    drawOval(
        color = baseColor,
        topLeft = Offset(cx - rBot, yBottom - rBot * tiltRatio),
        size = Size(rBot * 2f, rBot * 2f * tiltRatio)
    )

    if (layer.isDome) {
        val domeHeight = layerHeightPx * 1.25f
        val domePath = Path().apply {
            moveTo(cx - rTop, yBottom)
            cubicTo(
                cx - rTop, yBottom - domeHeight,
                cx + rTop, yBottom - domeHeight,
                cx + rTop, yBottom
            )
            close()
        }
        drawPath(
            path = domePath,
            brush = Brush.radialGradient(
                colors = listOf(highlightColor, topColor, baseColor),
                center = Offset(cx - rTop * 0.25f * cos(yaw), yBottom - domeHeight * 0.55f),
                radius = max(rTop * 1.4f, 10f)
            )
        )
    } else {
        drawOval(
            brush = Brush.linearGradient(
                colors = listOf(highlightColor, topColor, baseColor),
                start = Offset(cx - rTop * cos(yaw), yTop - rTop * tiltRatio * sin(yaw)),
                end = Offset(cx + rTop * cos(yaw), yTop + rTop * tiltRatio * sin(yaw))
            ),
            topLeft = Offset(cx - rTop, yTop - rTop * tiltRatio),
            size = Size(rTop * 2f, rTop * 2f * tiltRatio)
        )
    }

    if (showCallout && layerIndex % 2 == 1) {
        val isRightSide = (layerIndex / 2) % 2 == 0
        val anchorX = if (isRightSide) cx + rTop * 0.85f else cx - rTop * 0.85f
        val endX = if (isRightSide) (cx + scale * 1.18f).coerceAtMost(size.width - 110.dp.toPx())
        else (cx - scale * 1.18f).coerceAtLeast(110.dp.toPx())

        drawLine(
            color = SaffronGold.copy(alpha = 0.85f),
            start = Offset(anchorX, yTop),
            end = Offset(endX, yTop),
            strokeWidth = 1.2.dp.toPx()
        )
        drawCircle(
            color = SaffronGold,
            radius = 3.dp.toPx(),
            center = Offset(anchorX, yTop)
        )

        val measured = textMeasurer.measure(
            text = layer.name,
            style = TextStyle(
                color = Color(0xFFF7F2EB),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = JetBrainsMonoFamily
            )
        )
        val textX = if (isRightSide) endX + 4.dp.toPx() else endX - measured.size.width - 4.dp.toPx()
        drawText(
            textLayoutResult = measured,
            topLeft = Offset(
                textX.coerceIn(8f, max(8f, size.width - measured.size.width - 8f)),
                yTop - measured.size.height * 0.5f
            )
        )
    }
}

private fun DrawScope.drawDimensionRing(
    cx: Float,
    cy: Float,
    radius: Float,
    tiltRatio: Float,
    dimensionsText: String,
    textMeasurer: TextMeasurer
) {
    val ry = radius * tiltRatio
    drawOval(
        color = SaffronGold.copy(alpha = 0.55f),
        topLeft = Offset(cx - radius, cy - ry),
        size = Size(radius * 2f, ry * 2f),
        style = Stroke(
            width = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    )

    val layout = textMeasurer.measure(
        text = dimensionsText,
        style = TextStyle(
            color = SaffronGold,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = JetBrainsMonoFamily
        )
    )
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(cx - layout.size.width * 0.5f, cy + ry + 6.dp.toPx())
    )
}

private fun applyExposure(color: Color, factor: Float): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EmbeddedWebArModelViewer(
    dishId: String?,
    onLaunchNativeAr: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.testTag("embedded_webar_webview"),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun launchNativeSceneViewer(id: String, title: String, glbSrc: String) {
                            post { onLaunchNativeAr() }
                        }
                    },
                    "AndroidBridge"
                )

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val url = request?.url ?: return null
                        if (url.host == "ar-bistro.local") {
                            val cleanPath = url.path?.trimStart('/')?.ifEmpty { "index.html" } ?: "index.html"
                            val mimeType = when {
                                cleanPath.endsWith(".html") -> "text/html"
                                cleanPath.endsWith(".css") -> "text/css"
                                cleanPath.endsWith(".js") -> "application/javascript"
                                cleanPath.endsWith(".glb") -> "model/gltf-binary"
                                cleanPath.endsWith(".usdz") -> "model/vnd.usdz+zip"
                                cleanPath.endsWith(".jpg") || cleanPath.endsWith(".jpeg") -> "image/jpeg"
                                cleanPath.endsWith(".png") -> "image/png"
                                else -> "application/octet-stream"
                            }
                            return try {
                                val stream = if (cleanPath.startsWith("custom/")) {
                                    val customFile = java.io.File(
                                        context.filesDir,
                                        "custom_models/${cleanPath.removePrefix("custom/")}"
                                    )
                                    java.io.FileInputStream(customFile)
                                } else {
                                    context.assets.open("webar/$cleanPath")
                                }
                                val headers = mapOf(
                                    "Access-Control-Allow-Origin" to "*",
                                    "Cache-Control" to "public, max-age=3600"
                                )
                                WebResourceResponse(mimeType, "UTF-8", 200, "OK", headers, stream)
                            } catch (_: Exception) {
                                null
                            }
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (dishId != null) {
                            view?.evaluateJavascript(
                                "if (window.AuraWebAR) { window.AuraWebAR.openDishById('$dishId'); }",
                                null
                            )
                        }
                    }
                }

                loadUrl("https://ar-bistro.local/index.html")
            }
        },
        update = { webView ->
            if (dishId != null) {
                webView.evaluateJavascript(
                    "if (window.AuraWebAR) { window.AuraWebAR.openDishById('$dishId'); }",
                    null
                )
            }
        }
    )
}
