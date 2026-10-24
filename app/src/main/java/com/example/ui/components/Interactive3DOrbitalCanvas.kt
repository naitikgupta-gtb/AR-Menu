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
import androidx.compose.foundation.layout.Column
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
import com.example.model.IngredientLayer3D
import com.example.model.LightingPreset
import com.example.model.ViewerMode
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Interactive 3D & Spatial AR Viewport supporting:
 * 1. ORBITAL_3D: 60fps 3D multi-layer dish turntable with touch drag orbit, pinch zoom, and exploded layer X-Ray.
 * 2. ROOM_AR_TABLE: Simulated 1:1 AR tabletop surface placement with perspective dining table grid, reticle, and dimension rings.
 * 3. MODEL_VIEWER_WEB: Embedded self-hosted HTML5/JS <model-viewer> engine loading real binary .glb files from assets/webar/models/.
 */
@Composable
fun Interactive3DOrbitalCanvas(
    dish: ArMenuDish,
    viewerMode: ViewerMode,
    autoRotate: Boolean,
    explodeLayers: Boolean,
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
    var userPitch by remember(dish.id) { mutableFloatStateOf(0.46f) }
    var userZoom by remember(dish.id) { mutableFloatStateOf(1.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "orbit_rotation")
    val continuousSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "continuous_yaw"
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
            val cy = h * 0.64f
            val tiltRatio = sin(userPitch).coerceIn(0.22f, 0.88f)
            val effectiveScale = min(w, h) * 0.36f * userZoom * scaleFactor

            if (viewerMode == ViewerMode.ROOM_AR_TABLE) {
                drawRoomTableSurface(cx, cy, w, h, tiltRatio, effectiveYaw)
            } else {
                drawStudioSpatialGrid(cx, cy, effectiveScale, tiltRatio, Color(lightingPreset.rimHex))
            }

            // Draw soft contact shadow under plate
            drawOval(
                color = Color.Black.copy(alpha = 0.55f),
                topLeft = Offset(
                    cx - effectiveScale * 0.98f,
                    cy - effectiveScale * 0.98f * tiltRatio + 8.dp.toPx()
                ),
                size = Size(effectiveScale * 1.96f, effectiveScale * 1.96f * tiltRatio)
            )

            // Draw each 3D culinary layer from bottom to top
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

            // Draw 1:1 AR Dimension Measurement Ring around base
            drawDimensionRing(
                cx = cx,
                cy = cy,
                radius = effectiveScale * 1.06f,
                tiltRatio = tiltRatio,
                dimensionsText = "${dish.dimensionsLabel} (${if (trueScale1To1) "1:1 Fixed" else "Auto Fit"})",
                textMeasurer = textMeasurer
            )
        }

        // Top HUD Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = ObsidianBg.copy(alpha = 0.82f),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronGold.copy(alpha = 0.35f))
            ) {
                Text(
                    text = if (viewerMode == ViewerMode.ROOM_AR_TABLE) {
                        "⬡ AR TABLE PLANE LOCKED • 1:1"
                    } else {
                        "⬡ ${dish.glbAsset.substringAfterLast('/')} • ${dish.polyCount}"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (viewerMode == ViewerMode.ROOM_AR_TABLE) SpatialEmerald else SaffronGold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Surface(
                color = ObsidianBg.copy(alpha = 0.82f),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronGold.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "Drag 360° • Pinch Zoom",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
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
    // Draw warm walnut tabletop surface plane in lower 65% of viewport
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

    // Draw ARCore / WebXR spatial plane detection grid dots
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

    // Side wall path connecting bottom ellipse and top ellipse
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

    // Bottom ellipse rim
    drawOval(
        color = baseColor,
        topLeft = Offset(cx - rBot, yBottom - rBot * tiltRatio),
        size = Size(rBot * 2f, rBot * 2f * tiltRatio)
    )

    if (layer.isDome) {
        // Sculpted 3D dome cap (e.g., Brioche crown, Burrata, Tamago yolk, Ice sphere)
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
        // Top cap surface ellipse with subtle rotating specular sheen
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

    // Draw X-Ray Callout Line & Label when Explode Layers is active
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

/**
 * Hardware-accelerated Android WebView running the self-hosted HTML5/CSS/JS + <model-viewer>
 * application from `assets/webar/`, serving `.glb` binary files over `https://ar-bistro.local/`
 * with proper CORS and MIME headers (`model/gltf-binary`).
 */
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
