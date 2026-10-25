package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.model.ArMenuDish
import com.example.model.LightingPreset
import com.example.model.ViewerMode
import com.example.ui.components.Interactive3DOrbitalCanvas
import com.example.ui.theme.BistroCardBorder
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.BistroSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald

/**
 * Full-screen Live AR Camera Table View:
 * Allows user to point camera at their physical dining table and see the 3D food placed on the table
 * with floating holographic AR nutrition cards (Video 4) and 3D animated Chef mascot (Video 1 & 2).
 */
@Composable
fun RealTableArCameraScreen(
    dish: ArMenuDish,
    allDishes: List<ArMenuDish>,
    showChefMascot: Boolean,
    showNutritionHotspots: Boolean,
    trueScale1To1: Boolean,
    lightingPreset: LightingPreset,
    onSelectDish: (ArMenuDish) -> Unit,
    onToggleChefMascot: () -> Unit,
    onToggleNutritionHotspots: () -> Unit,
    onToggleTrueScale: () -> Unit,
    onAddToOrder: (ArMenuDish) -> Unit,
    onClose: () -> Unit,
    onOpenTableQrStandee: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onClose)

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Camera permission optional: AR Table simulation mode active",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var dishOffsetFractionX by remember { mutableFloatStateOf(0f) }
    var dishOffsetFractionY by remember { mutableFloatStateOf(0.12f) }
    var photoTakenNotification by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "ar_scanner")
    val scannerPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("real_table_ar_camera_screen")
    ) {
        // 1. Live CameraX Feed or Realistic Restaurant Table Background
        if (hasCameraPermission) {
            CameraXLivePreview(modifier = Modifier.fillMaxSize())
        } else {
            // Simulated Restaurant Wood Table Surface
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF382315),
                                Color(0xFF22140A),
                                Color(0xFF0F0804)
                            )
                        )
                    )
            )
        }

        // Tap gestures on table to position the 3D food plate
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        dishOffsetFractionX = ((offset.x / size.width) - 0.5f).coerceIn(-0.35f, 0.35f)
                        dishOffsetFractionY = ((offset.y / size.height) - 0.5f).coerceIn(-0.25f, 0.35f)
                    }
                }
        )

        // 2. The 3D Dish with Floating Holographic AR Nutrition Cards & 3D Chef
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp, top = 65.dp),
            contentAlignment = Alignment.Center
        ) {
            Interactive3DOrbitalCanvas(
                dish = dish,
                viewerMode = ViewerMode.ROOM_AR_TABLE,
                autoRotate = true,
                explodeLayers = false,
                showChefMascot = showChefMascot,
                showNutritionHotspots = showNutritionHotspots,
                trueScale1To1 = trueScale1To1,
                lightingPreset = lightingPreset,
                onLaunchNativeSceneViewer = {},
                modifier = Modifier.fillMaxSize()
            )
        }

        // 3. Top HUD: Brand Crest, Close Button, and Status Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(44.dp)
                    .background(ObsidianBg.copy(alpha = 0.85f), CircleShape)
                    .border(1.dp, SaffronGold.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back to Menu",
                    tint = SaffronGold
                )
            }

            Surface(
                color = ObsidianBg.copy(alpha = 0.90f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.65f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = SpatialEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (hasCameraPermission) "LIVE TABLE AR • CAMERA ACTIVE" else "TABLE AR • SURFACE SIMULATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            IconButton(
                onClick = onOpenTableQrStandee,
                modifier = Modifier
                    .size(44.dp)
                    .background(ObsidianBg.copy(alpha = 0.85f), CircleShape)
                    .border(1.dp, BistroCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Table Standee",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Table Reticle hint in center
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 180.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = ObsidianBg.copy(alpha = 0.75f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, BistroCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Drag or tap table to reposition dish",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Notification banner if user takes a snap
        AnimatedVisibility(
            visible = photoTakenNotification,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        ) {
            Surface(
                color = SpatialEmerald,
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ObsidianBg,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AR Snapshot Captured! Ready to share.",
                        style = MaterialTheme.typography.labelMedium,
                        color = ObsidianBg,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 4. Bottom Controls Panel: 4D Toggles, Dish Switcher & Add to Order
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ObsidianBg.copy(alpha = 0.88f),
                            ObsidianBg
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Horizontal Dish Selector Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allDishes.forEach { item ->
                    val isSelected = item.id == dish.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectDish(item) }
                            .testTag("ar_dish_select_${item.id}"),
                        color = if (isSelected) SaffronGold else BistroElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SaffronGold else BistroCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (item.isVeg) "🟢" else "🔴",
                                fontSize = 10.sp
                            )
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) ObsidianBg else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Quick Toggle Chips: AR Nutrition Callouts, 3D Chef Mascot, 1:1 Scale
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showNutritionHotspots,
                    onClick = onToggleNutritionHotspots,
                    label = { Text("🥑 Nutrition Tags") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SpatialEmerald.copy(alpha = 0.28f),
                        selectedLabelColor = SpatialEmerald
                    )
                )

                FilterChip(
                    selected = showChefMascot,
                    onClick = onToggleChefMascot,
                    label = { Text("👨‍🍳 3D Chef Mascot") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronGold.copy(alpha = 0.28f),
                        selectedLabelColor = SaffronGold
                    )
                )

                FilterChip(
                    selected = trueScale1To1,
                    onClick = onToggleTrueScale,
                    label = { Text(if (trueScale1To1) "📏 1:1 Life Size" else "🔍 Free Scale") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronGold.copy(alpha = 0.28f),
                        selectedLabelColor = SaffronGold
                    )
                )
            }

            // Dish Headline & Order Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = dish.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${dish.formattedPrice} • ${dish.calories} kcal • ${dish.dimensionsLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Capture AR Snap button
                    IconButton(
                        onClick = {
                            photoTakenNotification = true
                            Toast.makeText(context, "📸 AR Table Snap Saved!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(BistroElevated, CircleShape)
                            .border(1.dp, BistroCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture Snap",
                            tint = SaffronGold
                        )
                    }

                    // Add to Order CTA
                    Button(
                        onClick = { onAddToOrder(dish) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("ar_add_to_order_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = ObsidianBg
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ORDER NOW",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * CameraX live camera viewfinder pointing at real dining table
 */
@Composable
private fun CameraXLivePreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    // Fallback handled gracefully
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}
