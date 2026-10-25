package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.QrCode2
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ArMenuDish
import com.example.model.DietaryFilter
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
    onSelectDietaryFilter: (DietaryFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenDishAr: (ArMenuDish, ViewerMode) -> Unit,
    onCloseDishAr: () -> Unit,
    onSelectViewerMode: (ViewerMode) -> Unit,
    onToggleAutoRotate: () -> Unit,
    onToggleExplodeLayers: () -> Unit,
    onToggleChefMascot: () -> Unit,
    onToggleNutritionHotspots: () -> Unit,
    onToggleTrueScale: () -> Unit,
    onCycleLighting: () -> Unit,
    onLaunchNativeSceneViewer: (ArMenuDish) -> Unit,
    onAddToOrder: (ArMenuDish) -> Unit,
    onOpenWebArSandbox: () -> Unit,
    onOpenTableQrStandee: () -> Unit,
    onOpenCodeStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 840.dp

        if (isExpandedScreen) {
            // Canonical List-Detail Layout for Tablets / Foldables / Large Screens
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MenuCatalogList(
                    uiState = uiState,
                    onSelectCategory = onSelectCategory,
                    onSelectDietaryFilter = onSelectDietaryFilter,
                    onSearchQueryChange = onSearchQueryChange,
                    onOpenDishAr = onOpenDishAr,
                    onLaunchTableAr = onLaunchNativeSceneViewer,
                    onOpenWebArSandbox = onOpenWebArSandbox,
                    onOpenTableQrStandee = onOpenTableQrStandee,
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
                    showChefMascot = uiState.showChefMascot,
                    showNutritionHotspots = uiState.showNutritionHotspots,
                    trueScale1To1 = uiState.trueScale1To1,
                    lightingPreset = uiState.lightingPreset,
                    statusBanner = uiState.statusBanner,
                    isModal = false,
                    onClose = onCloseDishAr,
                    onSelectViewerMode = onSelectViewerMode,
                    onToggleAutoRotate = onToggleAutoRotate,
                    onToggleExplodeLayers = onToggleExplodeLayers,
                    onToggleChefMascot = onToggleChefMascot,
                    onToggleNutritionHotspots = onToggleNutritionHotspots,
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
                    onSelectDietaryFilter = onSelectDietaryFilter,
                    onSearchQueryChange = onSearchQueryChange,
                    onOpenDishAr = onOpenDishAr,
                    onLaunchTableAr = onLaunchNativeSceneViewer,
                    onOpenWebArSandbox = onOpenWebArSandbox,
                    onOpenTableQrStandee = onOpenTableQrStandee,
                    onOpenCodeStudio = onOpenCodeStudio,
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 620.dp)
                        .align(Alignment.TopCenter)
                )

                if (uiState.isArModalOpen) {
                    DishArModalDialog(
                        dish = uiState.selectedDish,
                        viewerMode = uiState.viewerMode,
                        autoRotate = uiState.autoRotate,
                        explodeLayers = uiState.explodeLayers,
                        showChefMascot = uiState.showChefMascot,
                        showNutritionHotspots = uiState.showNutritionHotspots,
                        trueScale1To1 = uiState.trueScale1To1,
                        lightingPreset = uiState.lightingPreset,
                        statusBanner = uiState.statusBanner,
                        onClose = onCloseDishAr,
                        onSelectViewerMode = onSelectViewerMode,
                        onToggleAutoRotate = onToggleAutoRotate,
                        onToggleExplodeLayers = onToggleExplodeLayers,
                        onToggleChefMascot = onToggleChefMascot,
                        onToggleNutritionHotspots = onToggleNutritionHotspots,
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
    onSelectDietaryFilter: (DietaryFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenDishAr: (ArMenuDish, ViewerMode) -> Unit,
    onLaunchTableAr: (ArMenuDish) -> Unit,
    onOpenWebArSandbox: () -> Unit,
    onOpenTableQrStandee: () -> Unit,
    onOpenCodeStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.testTag("bistro_menu_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Luxury Golden Oak 4D Banner
        item {
            HeroSpatialBanner(
                onOpenWebArSandbox = onOpenWebArSandbox,
                onOpenTableQrStandee = onOpenTableQrStandee,
                onOpenCodeStudio = onOpenCodeStudio
            )
        }

        // 2. Search & Filter Bar (Dietary Chips + Horizontal Category Bar)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Search Field
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_search_input"),
                    placeholder = {
                        Text(
                            text = "Search pizza, salad, wagyu burger, ramen...",
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

                // Dietary Veg/Non-Veg Filter Chips (Matching Video 4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DietaryFilter.entries.forEach { filter ->
                        val isSelected = uiState.dietaryFilter == filter
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectDietaryFilter(filter) }
                                .testTag("dietary_filter_${filter.name.lowercase()}"),
                            color = if (isSelected) {
                                when (filter) {
                                    DietaryFilter.ALL -> SaffronGold
                                    DietaryFilter.VEG -> Color(0xFF1B5E20)
                                    DietaryFilter.NON_VEG -> Color(0xFFB71C1C)
                                }
                            } else BistroSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color.Transparent else BistroCardBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Horizontal Category Scroller with Emojis (Starters, Main Course, Burgers, Salads, Pizza, Desserts)
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
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = category.iconEmoji, fontSize = 14.sp)
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) ObsidianBg else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
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
                            text = "No matching 4D dishes found",
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
                    onOpenRoomAr = { onLaunchTableAr(dish) }
                )
            }
        }
    }
}

@Composable
private fun HeroSpatialBanner(
    onOpenWebArSandbox: () -> Unit,
    onOpenTableQrStandee: () -> Unit,
    onOpenCodeStudio: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_spatial_banner"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_bistro),
                contentDescription = "The Golden Oak 4D Smart Menu preview",
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
                                Color.Transparent,
                                ObsidianBg.copy(alpha = 0.65f),
                                ObsidianBg.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Surface(
                    color = SaffronGold,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "THE GOLDEN OAK • 4D SMART MENU",
                        style = MaterialTheme.typography.labelSmall,
                        color = ObsidianBg,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "See It Before You Believe It.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "3D dishes on your table + floating holographic nutrition cards & 3D Chef mascot.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenTableQrStandee,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SaffronGold),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("hero_open_qr_standee_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Table QR Standee",
                            style = MaterialTheme.typography.labelMedium,
                            color = SaffronGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenWebArSandbox,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BistroCardBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("hero_open_webar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live WebAR",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenCodeStudio,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BistroCardBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("hero_open_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
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
            .clickable(onClick = onOpenRoomAr)
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
                    .height(190.dp)
            ) {
                val customBitmap = androidx.compose.runtime.remember(dish.customPosterPath) {
                    dish.customPosterPath?.let { path ->
                        android.graphics.BitmapFactory.decodeFile(path)?.asImageBitmap()
                    }
                }
                if (customBitmap != null) {
                    Image(
                        bitmap = customBitmap,
                        contentDescription = dish.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = dish.imageRes),
                        contentDescription = dish.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    ObsidianBg.copy(alpha = 0.88f)
                                )
                            )
                        )
                )

                // Top-left Veg / Non-Veg Indicator badge (Matching Video 4)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = ObsidianBg.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (dish.isVeg) Color(0xFF4CAF50) else Color(0xFFEF5350)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VegNonVegIcon(isVeg = dish.isVeg)
                        Text(
                            text = if (dish.isVeg) "VEG" else "NON-VEG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (dish.isVeg) Color(0xFF81C784) else Color(0xFFE57373)
                        )
                    }
                }

                // Top-right Calorie & 3D Layer Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = ObsidianBg.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, BistroCardBorder)
                ) {
                    Text(
                        text = "🔥 ${dish.calories} kcal • 🥑 ${dish.nutritionHotspots.size} Holograms",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold,
                        fontWeight = FontWeight.SemiBold,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = dish.formattedPrice,
                        style = MaterialTheme.typography.headlineSmall,
                        color = SaffronGold,
                        fontWeight = FontWeight.Bold
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

                // Ingredient preview chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dish.nutritionHotspots.forEach { hotspot ->
                        Surface(
                            color = BistroElevated,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, BistroCardBorder)
                        ) {
                            Text(
                                text = "${hotspot.iconEmoji} ${hotspot.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real 1:1 Scale: ${dish.dimensionsLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (orderQty > 0) {
                        Surface(
                            color = SpatialEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Ordered: ${orderQty}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = SpatialEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The CTA Buttons - Primary: "VIEW ON YOUR TABLE" (Matching Video 4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenRoomAr,
                        modifier = Modifier
                            .weight(1.35f)
                            .height(48.dp)
                            .testTag("open_room_ar_${dish.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = ObsidianBg
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VIEW ON YOUR TABLE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpen3DOrbital,
                        modifier = Modifier
                            .weight(0.75f)
                            .height(48.dp)
                            .testTag("open_3d_orbit_${dish.id}"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3D Orbit",
                            style = MaterialTheme.typography.labelLarge,
                            color = SaffronGold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard Veg / Non-Veg square & dot indicator
 */
@Composable
private fun VegNonVegIcon(isVeg: Boolean) {
    val borderColor = if (isVeg) Color(0xFF4CAF50) else Color(0xFFE53935)
    val dotColor = if (isVeg) Color(0xFF4CAF50) else Color(0xFFE53935)

    Box(
        modifier = Modifier
            .size(15.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(dotColor, CircleShape)
        )
    }
}
