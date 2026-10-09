package com.example.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.model.AppDestination
import com.example.model.ArMenuDish
import com.example.model.BistroMenuRepository
import com.example.model.DishCategory
import com.example.model.LightingPreset
import com.example.model.ViewerMode
import com.example.model.WebArCodeArtifact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ArMenuUiState(
    val currentDestination: AppDestination = AppDestination.BISTRO_MENU,
    val selectedCategory: DishCategory = DishCategory.ALL,
    val searchQuery: String = "",
    val dishes: List<ArMenuDish> = BistroMenuRepository.dishes,
    val selectedDish: ArMenuDish = BistroMenuRepository.dishes.first(),
    val isArModalOpen: Boolean = false,
    val viewerMode: ViewerMode = ViewerMode.ORBITAL_3D,
    val autoRotate: Boolean = true,
    val explodeLayers: Boolean = false,
    val trueScale1To1: Boolean = true,
    val lightingPreset: LightingPreset = LightingPreset.WARM_BISTRO,
    val tableOrderQuantities: Map<String, Int> = emptyMap(),
    val preloadedDishIds: Set<String> = setOf(BistroMenuRepository.dishes.first().id),
    val statusBanner: String? = null,
    val selectedCodeArtifactId: String = "index.html"
) {
    val filteredDishes: List<ArMenuDish>
        get() = dishes.filter { dish ->
            val matchesCategory = selectedCategory == DishCategory.ALL || dish.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                dish.title.contains(searchQuery, ignoreCase = true) ||
                dish.subtitle.contains(searchQuery, ignoreCase = true) ||
                dish.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

    val totalOrderItems: Int
        get() = tableOrderQuantities.values.sum()

    val totalOrderPrice: Double
        get() = tableOrderQuantities.entries.sumOf { (id, qty) ->
            val dish = dishes.find { it.id == id }
            (dish?.price ?: 0.0) * qty
        }
}

class ArMenuViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArMenuUiState())
    val uiState: StateFlow<ArMenuUiState> = _uiState.asStateFlow()

    fun selectDestination(destination: AppDestination) {
        _uiState.update { it.copy(currentDestination = destination, statusBanner = null) }
    }

    fun selectCategory(category: DishCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openDishArViewer(dish: ArMenuDish, initialMode: ViewerMode = ViewerMode.ORBITAL_3D) {
        _uiState.update { state ->
            state.copy(
                selectedDish = dish,
                isArModalOpen = true,
                viewerMode = initialMode,
                explodeLayers = false,
                preloadedDishIds = state.preloadedDishIds + dish.id,
                statusBanner = null
            )
        }
    }

    fun closeDishArViewer() {
        _uiState.update { it.copy(isArModalOpen = false, statusBanner = null) }
    }

    fun setViewerMode(mode: ViewerMode) {
        _uiState.update { it.copy(viewerMode = mode, statusBanner = null) }
    }

    fun toggleAutoRotate() {
        _uiState.update { it.copy(autoRotate = !it.autoRotate) }
    }

    fun toggleExplodeLayers() {
        _uiState.update { it.copy(explodeLayers = !it.explodeLayers) }
    }

    fun toggleTrueScale() {
        _uiState.update { it.copy(trueScale1To1 = !it.trueScale1To1) }
    }

    fun cycleLightingPreset() {
        _uiState.update { state ->
            val presets = LightingPreset.entries
            val nextIndex = (presets.indexOf(state.lightingPreset) + 1) % presets.size
            state.copy(lightingPreset = presets[nextIndex])
        }
    }

    fun addToTableOrder(dish: ArMenuDish) {
        _uiState.update { state ->
            val currentQty = state.tableOrderQuantities[dish.id] ?: 0
            val updated = state.tableOrderQuantities.toMutableMap().apply {
                put(dish.id, currentQty + 1)
            }
            state.copy(
                tableOrderQuantities = updated,
                statusBanner = "Added ${dish.title} to Table Order (${currentQty + 1}x)"
            )
        }
    }

    fun clearTableOrder() {
        _uiState.update {
            it.copy(
                tableOrderQuantities = emptyMap(),
                statusBanner = "Table order cleared"
            )
        }
    }

    fun selectCodeArtifact(artifactId: String) {
        _uiState.update { it.copy(selectedCodeArtifactId = artifactId) }
    }

    fun dismissBanner() {
        _uiState.update { it.copy(statusBanner = null) }
    }

    /**
     * Attempts to launch Google's native Android ARCore Scene Viewer Intent for .glb models.
     * Falls back defensively to the in-app Room AR Table projection mode if Google Play Services
     * for AR (ARCore) is unavailable in the current environment/emulator.
     */
    fun launchNativeSceneViewerOrFallback(context: Context, dish: ArMenuDish) {
        try {
            val sceneViewerUrl = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                .appendQueryParameter(
                    "file",
                    "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF-Binary/Avocado.glb"
                )
                .appendQueryParameter("mode", "ar_preferred")
                .appendQueryParameter("title", "${dish.title} (${dish.dimensionsLabel})")
                .appendQueryParameter("resizable", (!uiState.value.trueScale1To1).toString())
                .build()

            val sceneViewerIntent = Intent(Intent.ACTION_VIEW).apply {
                data = sceneViewerUrl
                setPackage("com.google.android.googlequicksearchbox")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(sceneViewerIntent)
        } catch (_: ActivityNotFoundException) {
            _uiState.update {
                it.copy(
                    viewerMode = ViewerMode.ROOM_AR_TABLE,
                    statusBanner = "ARCore Scene Viewer not installed on device — Switched to Interactive Room Table AR Fallback (1:1 Scale)."
                )
            }
        } catch (_: Exception) {
            _uiState.update {
                it.copy(
                    viewerMode = ViewerMode.ROOM_AR_TABLE,
                    statusBanner = "Switched to Interactive Room Table AR Fallback (1:1 Scale)."
                )
            }
        }
    }

    fun loadCodeArtifacts(context: Context): List<WebArCodeArtifact> {
        fun readAssetText(path: String): String {
            return try {
                context.assets.open(path).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                "// Error reading $path: ${e.message}"
            }
        }

        val htmlCode = readAssetText("webar/index.html")
        val cssCode = readAssetText("webar/styles.css")
        val jsCode = readAssetText("webar/app.js")

        val workflowGuide = """
            # Self-Hosted WebAR Restaurant Menu — Architecture & Implementation Workflow
            
            ## 1. Directory & Asset Pairing Structure
            webar/
            ├── index.html          # Semantic HTML5 + <model-viewer> + Fallback <canvas>
            ├── styles.css          # Mobile-first responsive CSS + bottom-sheet AR modal
            ├── app.js              # Vanilla JS controller, preloader & defensive fallback
            ├── models/
            │   ├── burger.glb      # Android Scene Viewer / WebXR binary glTF 2.0
            │   ├── burger.usdz     # iOS Safari AR Quick Look Universal Scene Description
            │   ├── ramen.glb       # Matching pair for Ramen
            │   ├── ramen.usdz
            │   ├── pizza.glb       # Matching pair for Wood-Fired Pizza
            │   └── pizza.usdz
            └── images/
                ├── hero_bistro.jpg
                ├── burger.jpg
                ├── ramen.jpg
                └── pizza.jpg

            ## 2. Key <model-viewer> Configuration Attributes
            - src="models/burger.glb"       -> Primary 3D/WebXR/Scene Viewer binary glTF 2.0
            - ios-src="models/burger.usdz"  -> Native iOS AR Quick Look asset
            - ar                            -> Enables augmented reality launch
            - ar-modes="webxr scene-viewer quick-look" -> Prioritizes in-browser WebXR, then Android Scene Viewer, then iOS Quick Look
            - ar-scale="fixed"              -> Locks dish to 1:1 true culinary dimensions on the table
            - camera-controls               -> Enables touch orbit, pan, and pinch-to-zoom
            - touch-action="pan-y"          -> Preserves vertical scrolling when swiping outside orbit

            ## 3. Defensive Programming & Fallback Strategy
            1. Script Load Guard: `<script type="module" ... onerror="window.__MODEL_VIEWER_SCRIPT_ERROR__ = true">`
            2. WebXR / Device Detection: Inspects `navigator.xr.isSessionSupported('immersive-ar')` alongside iOS Quick Look (`a.relList.supports('ar')`) and Android User-Agent capabilities.
            3. Automatic 3D Orbital Turntable: If WebAR hardware is missing, `.glb` binaries are parsed directly via `DataView` and rendered in an interactive 60fps 3D orbital canvas with layer X-ray and lighting controls.
        """.trimIndent()

        return listOf(
            WebArCodeArtifact(
                id = "index.html",
                fileName = "index.html",
                badge = "HTML5 + <model-viewer>",
                description = "Semantic HTML5 menu structure, resource preloading hints, and configured <model-viewer> dialog with custom AR slots.",
                codeContent = htmlCode
            ),
            WebArCodeArtifact(
                id = "styles.css",
                fileName = "styles.css",
                badge = "Responsive Mobile CSS",
                description = "Mobile-first dark culinary UI system, sticky category bar, responsive dish grid, and bottom-sheet 3D/AR viewport styling.",
                codeContent = cssCode
            ),
            WebArCodeArtifact(
                id = "app.js",
                fileName = "app.js",
                badge = "Vanilla JS + Fallback Engine",
                description = "Menu catalog mapping (.glb/.usdz pairs), pointerenter asset preloader, WebAR capability detector, and defensive 3D .glb orbital fallback renderer.",
                codeContent = jsCode
            ),
            WebArCodeArtifact(
                id = "ARCHITECTURE.md",
                fileName = "ARCHITECTURE.md",
                badge = "Step-by-Step Guide",
                description = "Complete file structure, variable mappings, MIME types (model/gltf-binary, model/vnd.usdz+zip), and defensive programming workflow.",
                codeContent = workflowGuide
            )
        )
    }
}
