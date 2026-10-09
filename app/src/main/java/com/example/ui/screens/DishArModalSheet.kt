package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DishArDetailPanel(
    dish: ArMenuDish,
    viewerMode: ViewerMode,
    autoRotate: Boolean,
    explodeLayers: Boolean,
    trueScale1To1: Boolean,
    lightingPreset: LightingPreset,
    statusBanner: String?,
    isModal: Boolean,
    onClose: () -> Unit,
    onSelectViewerMode: (ViewerMode) -> Unit,
    onToggleAutoRotate: () -> Unit,
    onToggleExplodeLayers: () -> Unit,
    onToggleTrueScale: () -> Unit,
    onCycleLighting: () -> Unit,
    onLaunchNativeSceneViewer: () -> Unit,
    onAddToOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isModal) {
        BackHandler(onBack = onClose)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BistroSurface,
        shape = if (isModal) RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp) else RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.32f)),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${dish.category.displayName.uppercase()} • SPATIAL ASSET PAIR",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dish.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isModal) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(BistroElevated)
                            .testTag("close_ar_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close 3D AR Viewer",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Mode Switcher Bar (3D Orbital | View in Room | <model-viewer> Web)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ViewerMode.entries.forEach { mode ->
                    val selected = viewerMode == mode
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectViewerMode(mode) }
                            .testTag("viewer_mode_${mode.name.lowercase()}"),
                        color = if (selected) SaffronGold else BistroElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (selected) SaffronGold else BistroCardBorder
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) ObsidianBg else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3D / AR Interactive Stage
            Interactive3DOrbitalCanvas(
                dish = dish,
                viewerMode = viewerMode,
                autoRotate = autoRotate,
                explodeLayers = explodeLayers,
                trueScale1To1 = trueScale1To1,
                lightingPreset = lightingPreset,
                onLaunchNativeSceneViewer = onLaunchNativeSceneViewer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            )

            // Interactive 3D Controls Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = autoRotate,
                    onClick = onToggleAutoRotate,
                    label = { Text("Auto-Orbit") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronGold.copy(alpha = 0.22f),
                        selectedLabelColor = SaffronGold,
                        selectedLeadingIconColor = SaffronGold
                    ),
                    modifier = Modifier.testTag("toggle_auto_orbit")
                )

                FilterChip(
                    selected = explodeLayers,
                    onClick = onToggleExplodeLayers,
                    label = { Text("Ingredient X-Ray") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronGold.copy(alpha = 0.22f),
                        selectedLabelColor = SaffronGold,
                        selectedLeadingIconColor = SaffronGold
                    ),
                    modifier = Modifier.testTag("toggle_explode_layers")
                )

                FilterChip(
                    selected = trueScale1To1,
                    onClick = onToggleTrueScale,
                    label = { Text(if (trueScale1To1) "1:1 True Scale" else "Table Fit Scale") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SpatialEmerald.copy(alpha = 0.22f),
                        selectedLabelColor = SpatialEmerald,
                        selectedLeadingIconColor = SpatialEmerald
                    ),
                    modifier = Modifier.testTag("toggle_true_scale")
                )

                FilterChip(
                    selected = true,
                    onClick = onCycleLighting,
                    label = { Text(lightingPreset.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BistroElevated,
                        selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                        selectedLeadingIconColor = SaffronGold
                    ),
                    modifier = Modifier.testTag("cycle_lighting_button")
                )
            }

            // Defensive Fallback / Status Notice
            AnimatedVisibility(visible = statusBanner != null) {
                if (statusBanner != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        color = SaffronGold.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = statusBanner,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Dish Metadata, Asset Pair Telemetry & Actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dish.formattedPrice,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SaffronGold
                    )
                    Text(
                        text = "${dish.calories} kcal • Prep ${dish.prepMinutes}m • ${dish.dimensionsLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dish.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sommelier & Chef Note: ${dish.chefPairing}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SaffronGold.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Asset Pair & Allergen Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssetBadgePill("Android: ${dish.glbAsset}", SpatialEmerald)
                    AssetBadgePill("iOS: ${dish.usdzAsset}", SaffronGold)
                    dish.allergens.forEach { allergen ->
                        AssetBadgePill("Allergen: $allergen", MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onLaunchNativeSceneViewer,
                        modifier = Modifier
                            .weight(1.15f)
                            .height(52.dp)
                            .testTag("launch_scene_viewer_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = ObsidianBg
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View in Room (AR)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onAddToOrder,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("add_to_order_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add to Table",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AssetBadgePill(text: String, tint: Color) {
    Surface(
        color = BistroElevated,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.35f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun DishArModalDialog(
    dish: ArMenuDish,
    viewerMode: ViewerMode,
    autoRotate: Boolean,
    explodeLayers: Boolean,
    trueScale1To1: Boolean,
    lightingPreset: LightingPreset,
    statusBanner: String?,
    onClose: () -> Unit,
    onSelectViewerMode: (ViewerMode) -> Unit,
    onToggleAutoRotate: () -> Unit,
    onToggleExplodeLayers: () -> Unit,
    onToggleTrueScale: () -> Unit,
    onCycleLighting: () -> Unit,
    onLaunchNativeSceneViewer: () -> Unit,
    onAddToOrder: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter
    ) {
        DishArDetailPanel(
            dish = dish,
            viewerMode = viewerMode,
            autoRotate = autoRotate,
            explodeLayers = explodeLayers,
            trueScale1To1 = trueScale1To1,
            lightingPreset = lightingPreset,
            statusBanner = statusBanner,
            isModal = true,
            onClose = onClose,
            onSelectViewerMode = onSelectViewerMode,
            onToggleAutoRotate = onToggleAutoRotate,
            onToggleExplodeLayers = onToggleExplodeLayers,
            onToggleTrueScale = onToggleTrueScale,
            onCycleLighting = onCycleLighting,
            onLaunchNativeSceneViewer = onLaunchNativeSceneViewer,
            onAddToOrder = onAddToOrder,
            modifier = Modifier
                .widthIn(max = 680.dp)
                .clickable(enabled = false) {}
        )
    }
}
