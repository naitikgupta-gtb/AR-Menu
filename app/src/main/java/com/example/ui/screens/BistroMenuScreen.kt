package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ArMenuDish
import com.example.model.DishCategory
import com.example.model.ViewerMode
import com.example.ui.theme.BistroCardBorder
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.BistroSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald
import com.example.viewmodel.ArMenuUiState

@Composable
fun BistroMenuScreen(
    uiState: ArMenuUiState,
    onSelectCategory: (DishCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenDishAr: (ArMenuDish, ViewerMode) -> Unit,
    onCloseDishAr: () -> Unit,
    onSelectViewerMode: (ViewerMode) -> Unit,
    onToggleAutoRotate: () -> Unit,
    onToggleExplodeLayers: () -> Unit,
    onToggleTrueScale: () -> Unit,
    onCycleLighting: () -> Unit,
    onLaunchNativeSceneViewer: (ArMenuDish) -> Unit,
    onAddToOrder: (ArMenuDish) -> Unit,
    onOpenWebArSandbox: () -> Unit,
    onOpenCodeStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 840.dp

        if (isExpandedScreen) {
            // Canonical List-Detail Layout for Tablets / Foldables / Desktop
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MenuCatalogList(
                    uiState = uiState,
                    onSelectCategory = onSelectCategory,
                    onSearchQueryChange = onSearchQueryChange,
                    onOpenDishAr = onOpenDishAr,
                    onOpenWebArSandbox = onOpenWebArSandbox,
                    onOpenCodeStudio = onOpenCodeStudio,
                    modifier = Modifier
                        .weight(1.05f)
                        .fillMaxHeight()
                )

                DishArDetailPanel(
                    dish = uiState.selectedDish,
                    viewerMode = uiState.viewerMode,
                    autoRotate = uiState.autoRotate,
                    explodeLayers = uiState.explodeLayers,
                    trueScale1To1 = uiState.trueScale1To1,
                    lightingPreset = uiState.lightingPreset,
                    statusBanner = uiState.statusBanner,
                    isModal = false,
                    onClose = onCloseDishAr,
                    onSelectViewerMode = onSelectViewerMode,
                    onToggleAutoRotate = onToggleAutoRotate,
                    onToggleExplodeLayers = onToggleExplodeLayers,
                    onToggleTrueScale = onToggleTrueScale,
                    onCycleLighting = onCycleLighting,
                    onLaunchNativeSceneViewer = { onLaunchNativeSceneViewer(uiState.selectedDish) },
                    onAddToOrder = { onAddToOrder(uiState.selectedDish) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        } else {
            // Compact Mobile Layout + Bottom-Sheet 3D/AR Modal
            Box(modifier = Modifier.fillMaxSize()) {
                MenuCatalogList(
                    uiState = uiState,
                    onSelectCategory = onSelectCategory,
                    onSearchQueryChange = onSearchQueryChange,
                    onOpenDishAr = onOpenDishAr,
                    onOpenWebArSandbox = onOpenWebArSandbox,
                    onOpenCodeStudio = onOpenCodeStudio,
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp)
                        .align(Alignment.TopCenter)
                )

                if (uiState.isArModalOpen) {
                    DishArModalDialog(
                        dish = uiState.selectedDish,
                        viewerMode = uiState.viewerMode,
                        autoRotate = uiState.autoRotate,
                        explodeLayers = uiState.explodeLayers,
                        trueScale1To1 = uiState.trueScale1To1,
                        lightingPreset = uiState.lightingPreset,
                        statusBanner = uiState.statusBanner,
                        onClose = onCloseDishAr,
                        onSelectViewerMode = onSelectViewerMode,
                        onToggleAutoRotate = onToggleAutoRotate,
                        onToggleExplodeLayers = onToggleExplodeLayers,
                        onToggleTrueScale = onToggleTrueScale,
                        onCycleLighting = onCycleLighting,
                        onLaunchNativeSceneViewer = { onLaunchNativeSceneViewer(uiState.selectedDish) },
                        onAddToOrder = { onAddToOrder(uiState.selectedDish) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuCatalogList(
    uiState: ArMenuUiState,
    onSelectCategory: (DishCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenDishAr: (ArMenuDish, ViewerMode) -> Unit,
    onOpenWebArSandbox: () -> Unit,
    onOpenCodeStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.testTag("bistro_menu_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner Card
        item {
            HeroSpatialBanner(
                onOpenWebArSandbox = onOpenWebArSandbox,
                onOpenCodeStudio = onOpenCodeStudio
            )
        }

        // 2. Search & Category Filter Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_search_input"),
                    placeholder = {
                        Text(
                            text = "Search Wagyu, Ramen, Truffle Pizza, Matcha...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search dishes",
                            tint = SaffronGold
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronGold,
                        unfocusedBorderColor = BistroCardBorder,
                        focusedContainerColor = BistroSurface,
                        unfocusedContainerColor = BistroSurface
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DishCategory.entries.forEach { category ->
                        val isSelected = uiState.selectedCategory == category
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onSelectCategory(category) }
                                .testTag("category_chip_${category.name.lowercase()}"),
                            color = if (isSelected) SaffronGold else BistroSurface,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) SaffronGold else BistroCardBorder
                            )
                        ) {
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) ObsidianBg else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Menu Dish Cards or Empty State
        if (uiState.filteredDishes.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    color = BistroSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BistroCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No matching spatial dishes found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing your search filter or selecting 'All Dishes'.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(uiState.filteredDishes, key = { it.id }) { dish ->
                DishMenuCard(
                    dish = dish,
                    isPreloaded = dish.id in uiState.preloadedDishIds,
                    orderQty = uiState.tableOrderQuantities[dish.id] ?: 0,
                    onOpen3DOrbital = { onOpenDishAr(dish, ViewerMode.ORBITAL_3D) },
                    onOpenRoomAr = { onOpenDishAr(dish, ViewerMode.ROOM_AR_TABLE) }
                )
            }
        }
    }
}

@Composable
private fun HeroSpatialBanner(
    onOpenWebArSandbox: () -> Unit,
    onOpenCodeStudio: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_spatial_banner"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.38f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_bistro),
                contentDescription = "Aura AR Bistro spatial dining table preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ObsidianBg.copy(alpha = 0.25f),
                                ObsidianBg.copy(alpha = 0.82f),
                                ObsidianBg.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .align(Alignment.BottomStart)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = SpatialEmerald.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, SpatialEmerald)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SpatialEmerald)
                            )
                            Text(
                                text = "<model-viewer> • .GLB + .USDZ READY",
                                style = MaterialTheme.typography.labelSmall,
                                color = SpatialEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Inspect Every Dish in 1:1 Spatial AR",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Zero-download WebAR powered by Google <model-viewer>, Android Scene Viewer (.glb), iOS Quick Look (.usdz), and defensive 3D orbital fallback.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onOpenWebArSandbox,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SaffronGold),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("hero_open_webar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Launch Live WebAR DOM",
                            style = MaterialTheme.typography.labelLarge,
                            color = SaffronGold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenCodeStudio,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BistroCardBorder),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("hero_open_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Inspect Code",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DishMenuCard(
    dish: ArMenuDish,
    isPreloaded: Boolean,
    orderQty: Int,
    onOpen3DOrbital: () -> Unit,
    onOpenRoomAr: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen3DOrbital)
            .testTag("dish_card_${dish.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BistroSurface),
        border = BorderStroke(1.dp, BistroCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Dish Photography & Spatial Asset Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                Image(
                    painter = painterResource(id = dish.imageRes),
                    contentDescription = dish.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    ObsidianBg.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Top-left Preload / Asset Pair Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = ObsidianBg.copy(alpha = 0.84f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (isPreloaded) SpatialEmerald else SaffronGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${dish.glbAsset.substringAfterLast('/')} + .usdz",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Top-right Dimension Tag
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = ObsidianBg.copy(alpha = 0.84f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, BistroCardBorder)
                ) {
                    Text(
                        text = dish.dimensionsLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                // Bottom Overlay Title & Price
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dish.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGold
                        )
                        Text(
                            text = dish.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = dish.formattedPrice,
                        style = MaterialTheme.typography.headlineSmall,
                        color = SaffronGold
                    )
                }
            }

            // Card Body & Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = dish.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${dish.calories} kcal • ${dish.prepMinutes}m prep • ${dish.layers3D.size} 3D layers",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (orderQty > 0) {
                        Surface(
                            color = SpatialEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "In Order: ${orderQty}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = SpatialEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpen3DOrbital,
                        modifier = Modifier
                            .weight(1.35f)
                            .height(48.dp)
                            .testTag("open_3d_ar_${dish.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = ObsidianBg
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive 3D / View in Room",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenRoomAr,
                        modifier = Modifier
                            .weight(0.75f)
                            .height(48.dp)
                            .testTag("quick_room_ar_${dish.id}"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.55f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1:1 AR",
                            style = MaterialTheme.typography.labelLarge,
                            color = SaffronGold
                        )
                    }
                }
            }
        }
    }
}
