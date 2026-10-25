package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppDestination
import com.example.model.ViewerMode
import com.example.ui.screens.BistroMenuScreen
import com.example.ui.screens.LiveWebArSandboxScreen
import com.example.ui.screens.ModelCreatorScreen
import com.example.ui.screens.RealTableArCameraScreen
import com.example.ui.screens.TableQrStandeeScreen
import com.example.ui.screens.WebArCodeStudioScreen
import com.example.ui.theme.BistroCardBorder
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.BistroSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald
import com.example.viewmodel.ArMenuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AuraArBistroApp()
            }
        }
    }
}

@Composable
fun AuraArBistroApp(
    viewModel: ArMenuViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val codeArtifacts = remember(context) { viewModel.loadCodeArtifacts(context) }

    if (uiState.currentDestination != AppDestination.BISTRO_MENU) {
        BackHandler {
            viewModel.selectDestination(AppDestination.BISTRO_MENU)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = ObsidianBg,
        topBar = {
            if (uiState.currentDestination != AppDestination.TABLE_AR) {
                AuraTopBar(
                    totalOrderItems = uiState.totalOrderItems,
                    totalOrderPrice = uiState.totalOrderPrice,
                    onClearOrder = viewModel::clearTableOrder
                )
            }
        },
        bottomBar = {
            if (uiState.currentDestination != AppDestination.TABLE_AR) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = BistroSurface,
                    tonalElevation = 8.dp
                ) {
                    AppDestination.entries.forEach { destination ->
                        val selected = uiState.currentDestination == destination
                        val icon = when (destination) {
                            AppDestination.BISTRO_MENU -> Icons.Default.RestaurantMenu
                            AppDestination.TABLE_AR -> Icons.Default.ViewInAr
                            AppDestination.TABLE_QR -> Icons.Default.QrCode2
                            AppDestination.MODEL_CREATOR -> Icons.Default.CloudUpload
                            AppDestination.LIVE_WEBAR -> Icons.Default.Devices
                            AppDestination.CODE_STUDIO -> Icons.Default.Code
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.selectDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianBg,
                                selectedTextColor = SaffronGold,
                                indicatorColor = SaffronGold,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${destination.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentDestination) {
                AppDestination.BISTRO_MENU -> {
                    BistroMenuScreen(
                        uiState = uiState,
                        onSelectCategory = viewModel::selectCategory,
                        onSelectDietaryFilter = viewModel::selectDietaryFilter,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onOpenDishAr = viewModel::openDishArViewer,
                        onCloseDishAr = viewModel::closeDishArViewer,
                        onSelectViewerMode = viewModel::setViewerMode,
                        onToggleAutoRotate = viewModel::toggleAutoRotate,
                        onToggleExplodeLayers = viewModel::toggleExplodeLayers,
                        onToggleChefMascot = viewModel::toggleChefMascot,
                        onToggleNutritionHotspots = viewModel::toggleNutritionHotspots,
                        onToggleTrueScale = viewModel::toggleTrueScale,
                        onCycleLighting = viewModel::cycleLightingPreset,
                        onLaunchNativeSceneViewer = { dish ->
                            viewModel.launchNativeSceneViewerOrFallback(context, dish)
                        },
                        onAddToOrder = viewModel::addToTableOrder,
                        onOpenWebArSandbox = {
                            viewModel.selectDestination(AppDestination.LIVE_WEBAR)
                        },
                        onOpenTableQrStandee = {
                            viewModel.selectDestination(AppDestination.TABLE_QR)
                        },
                        onOpenCodeStudio = {
                            viewModel.selectDestination(AppDestination.CODE_STUDIO)
                        }
                    )
                }

                AppDestination.TABLE_AR -> {
                    RealTableArCameraScreen(
                        dish = uiState.selectedDish,
                        allDishes = uiState.dishes,
                        showChefMascot = uiState.showChefMascot,
                        showNutritionHotspots = uiState.showNutritionHotspots,
                        trueScale1To1 = uiState.trueScale1To1,
                        lightingPreset = uiState.lightingPreset,
                        onSelectDish = { newDish ->
                            viewModel.openTableArForDish(newDish)
                        },
                        onToggleChefMascot = viewModel::toggleChefMascot,
                        onToggleNutritionHotspots = viewModel::toggleNutritionHotspots,
                        onToggleTrueScale = viewModel::toggleTrueScale,
                        onAddToOrder = viewModel::addToTableOrder,
                        onClose = {
                            viewModel.selectDestination(AppDestination.BISTRO_MENU)
                        },
                        onOpenTableQrStandee = {
                            viewModel.selectDestination(AppDestination.TABLE_QR)
                        }
                    )
                }

                AppDestination.TABLE_QR -> {
                    TableQrStandeeScreen(
                        selectedDish = uiState.selectedDish,
                        onLaunchDishAr = { dish ->
                            viewModel.launchNativeSceneViewerOrFallback(context, dish)
                        },
                        onOpenLiveWebAr = {
                            viewModel.selectDestination(AppDestination.LIVE_WEBAR)
                        }
                    )
                }

                AppDestination.MODEL_CREATOR -> {
                    ModelCreatorScreen(
                        lastUploadReport = uiState.lastUploadReport,
                        onImportCustomGlbUri = { uri, title, price, category, diam, height ->
                            viewModel.importCustom3DModelUri(
                                context = context,
                                uri = uri,
                                dishTitle = title,
                                dishPrice = price,
                                category = category,
                                diameterCm = diam,
                                heightCm = height
                            )
                        },
                        onGenerate3DFromPhotoUri = { uri, title, price, category, diam, height ->
                            viewModel.generate3DModelFromSinglePhoto(
                                context = context,
                                photoUri = uri,
                                dishTitle = title,
                                dishPrice = price,
                                category = category,
                                diameterCm = diam,
                                heightCm = height
                            )
                        },
                        onCreateStudioDish = viewModel::createCustomDishInStudio
                    )
                }

                AppDestination.LIVE_WEBAR -> {
                    LiveWebArSandboxScreen(
                        selectedDish = uiState.selectedDish,
                        onLaunchNativeSceneViewer = { dish ->
                            viewModel.launchNativeSceneViewerOrFallback(context, dish)
                        }
                    )
                }

                AppDestination.CODE_STUDIO -> {
                    WebArCodeStudioScreen(
                        artifacts = codeArtifacts,
                        selectedArtifactId = uiState.selectedCodeArtifactId,
                        onSelectArtifact = viewModel::selectCodeArtifact,
                        onLaunchLiveSandbox = {
                            viewModel.selectDestination(AppDestination.LIVE_WEBAR)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AuraTopBar(
    totalOrderItems: Int,
    totalOrderPrice: Double,
    onClearOrder: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BistroSurface,
        border = BorderStroke(1.dp, BistroCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "THE GOLDEN OAK • 4D SMART MENU",
                    style = MaterialTheme.typography.labelSmall,
                    color = SaffronGold
                )
                Text(
                    text = "Aura 4D Bistro",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (totalOrderItems > 0) {
                Surface(
                    modifier = Modifier
                        .clickable(onClick = onClearOrder)
                        .testTag("table_order_pill"),
                    color = BistroElevated,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, SaffronGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Table Order",
                            tint = SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${totalOrderItems}x • $${String.format("%.2f", totalOrderPrice)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = SaffronGold
                        )
                    }
                }
            } else {
                Surface(
                    color = BistroElevated,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, SpatialEmerald.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "AR Ready",
                            tint = SpatialEmerald,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "4D AR READY",
                            style = MaterialTheme.typography.labelSmall,
                            color = SpatialEmerald
                        )
                    }
                }
            }
        }
    }
}
