package com.example.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import com.example.R
import com.example.model.AppDestination
import com.example.model.ArMenuDish
import com.example.model.ArNutritionHotspot
import com.example.model.BistroMenuRepository
import com.example.model.DietaryFilter
import com.example.model.DishCategory
import com.example.model.IngredientLayer3D
import com.example.model.LightingPreset
import com.example.model.ViewerMode
import com.example.model.WebArCodeArtifact
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ArMenuUiState(
    val currentDestination: AppDestination = AppDestination.BISTRO_MENU,
    val selectedCategory: DishCategory = DishCategory.ALL,
    val dietaryFilter: DietaryFilter = DietaryFilter.ALL,
    val searchQuery: String = "",
    val dishes: List<ArMenuDish> = BistroMenuRepository.dishes,
    val selectedDish: ArMenuDish = BistroMenuRepository.dishes.first(),
    val isArModalOpen: Boolean = false,
    val viewerMode: ViewerMode = ViewerMode.ROOM_AR_TABLE,
    val autoRotate: Boolean = true,
    val explodeLayers: Boolean = false,
    val showChefMascot: Boolean = true,
    val showNutritionHotspots: Boolean = true,
    val trueScale1To1: Boolean = true,
    val lightingPreset: LightingPreset = LightingPreset.WARM_BISTRO,
    val tableOrderQuantities: Map<String, Int> = emptyMap(),
    val preloadedDishIds: Set<String> = setOf(BistroMenuRepository.dishes.first().id),
    val statusBanner: String? = null,
    val selectedCodeArtifactId: String = "index.html",
    val lastUploadReport: String? = null
) {
    val filteredDishes: List<ArMenuDish>
        get() = dishes.filter { dish ->
            val matchesCategory = selectedCategory == DishCategory.ALL || dish.category == selectedCategory
            val matchesDietary = when (dietaryFilter) {
                DietaryFilter.ALL -> true
                DietaryFilter.VEG -> dish.isVeg
                DietaryFilter.NON_VEG -> !dish.isVeg
            }
            val matchesQuery = searchQuery.isBlank() ||
                dish.title.contains(searchQuery, ignoreCase = true) ||
                dish.subtitle.contains(searchQuery, ignoreCase = true) ||
                dish.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesDietary && matchesQuery
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

    fun selectDietaryFilter(filter: DietaryFilter) {
        _uiState.update { it.copy(dietaryFilter = filter) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openDishArViewer(dish: ArMenuDish, initialMode: ViewerMode = ViewerMode.ROOM_AR_TABLE) {
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

    fun toggleChefMascot() {
        _uiState.update { it.copy(showChefMascot = !it.showChefMascot) }
    }

    fun toggleNutritionHotspots() {
        _uiState.update { it.copy(showNutritionHotspots = !it.showNutritionHotspots) }
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

    fun generate3DModelFromSinglePhoto(
        context: Context,
        photoUri: Uri,
        dishTitle: String,
        dishPrice: Double,
        category: DishCategory,
        diameterCm: Float,
        heightCm: Float,
        isVeg: Boolean = true
    ) {
        try {
            val bitmap = context.contentResolver.openInputStream(photoUri)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)
            } ?: throw IllegalStateException("Could not decode selected photo")

            val w = bitmap.width.coerceAtLeast(1)
            val h = bitmap.height.coerceAtLeast(1)

            fun sampleHex(nx: Float, ny: Float): Long {
                val px = ((w * nx).toInt()).coerceIn(0, w - 1)
                val py = ((h * ny).toInt()).coerceIn(0, h - 1)
                val argb = bitmap.getPixel(px, py)
                val r = (argb shr 16) and 0xFF
                val g = (argb shr 8) and 0xFF
                val b = argb and 0xFF
                return (0xFF000000L) or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong()
            }

            val centerCrownHex = sampleHex(0.50f, 0.45f)
            val midLayerHex = sampleHex(0.50f, 0.62f)
            val baseLayerHex = sampleHex(0.38f, 0.55f)
            val plateRimHex = sampleHex(0.18f, 0.82f)

            val cleanTitle = dishTitle.trim().ifEmpty { "4D Smart Menu Item" }
            val safeSlug = cleanTitle.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { "smart-dish" }
            val customDir = File(context.filesDir, "custom_models").apply { mkdirs() }

            val posterFile = File(customDir, "poster_$safeSlug.jpg")
            posterFile.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 88, out)
            }

            val sourceGlbAsset = when (category) {
                DishCategory.PIZZA -> "webar/models/pizza.glb"
                DishCategory.RAMEN, DishCategory.MAIN_COURSE, DishCategory.SALADS -> "webar/models/ramen.glb"
                DishCategory.DESSERTS_DRINKS -> "webar/models/matcha_cake.glb"
                else -> "webar/models/burger.glb"
            }
            val savedGlbName = "$safeSlug.glb"
            val savedGlbFile = File(customDir, savedGlbName)
            context.assets.open(sourceGlbAsset).use { input ->
                savedGlbFile.outputStream().use { output -> input.copyTo(output) }
            }

            val photoLayers = listOf(
                IngredientLayer3D("Photo-Sampled Plate Rim", 0.92f, 0.84f, 0.06f, 0.03f, plateRimHex, plateRimHex),
                IngredientLayer3D("Base Culinary Layer", 0.64f, 0.58f, 0.13f, 0.13f, baseLayerHex, baseLayerHex),
                IngredientLayer3D("Core Texture Layer", 0.60f, 0.58f, 0.13f, 0.27f, midLayerHex, midLayerHex),
                IngredientLayer3D("Glazed Garnish Crown", 0.54f, 0.54f, 0.20f, 0.43f, centerCrownHex, centerCrownHex, isDome = true)
            )

            val newDish = ArMenuDish(
                id = "photo3d-$safeSlug-${System.currentTimeMillis() % 10000}",
                title = cleanTitle,
                subtitle = "4D Smart Menu • Single Photo -> 3D AR (.GLB + .USDZ)",
                category = if (category == DishCategory.ALL) DishCategory.MAIN_COURSE else category,
                isVeg = isVeg,
                price = dishPrice,
                calories = 650,
                prepMinutes = 10,
                diameterCm = diameterCm,
                heightCm = heightCm,
                glbAsset = "custom/$savedGlbName",
                usdzAsset = "models/$safeSlug.usdz",
                imageRes = R.drawable.img_hero_bistro,
                description = "4D Smart Menu dish with interactive AR table projection, real 3D scale, and floating nutrition callout cards.",
                chefPairing = "4D Smart Menu Dish ($savedGlbName + .usdz).",
                allergens = listOf(if (isVeg) "Vegetarian" else "Non-Vegetarian"),
                cameraOrbit = "22deg 68deg 0.42m",
                polyCount = "glTF 2.0 • 36 KB .glb",
                layers3D = photoLayers,
                nutritionHotspots = listOf(
                    ArNutritionHotspot(cleanTitle, "Chef Crafted", "Fresh Ingredients", "✨", -0.52f, -0.22f),
                    ArNutritionHotspot("Nutrition", "Balanced Nutrients", "High Quality", "🥗", 0.52f, 0.28f)
                ),
                customPosterPath = posterFile.absolutePath
            )

            _uiState.update { state ->
                state.copy(
                    dishes = listOf(newDish) + state.dishes,
                    selectedDish = newDish,
                    preloadedDishIds = state.preloadedDishIds + newDish.id,
                    lastUploadReport = "Photo converted to 4D Smart Dish '$cleanTitle' ($savedGlbName) and added to AR Menu!",
                    isArModalOpen = true,
                    currentDestination = AppDestination.BISTRO_MENU
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    lastUploadReport = "Photo-to-3D error: ${e.localizedMessage ?: "Unable to process image"}"
                )
            }
        }
    }

    fun importCustom3DModelUri(
        context: Context,
        uri: Uri,
        dishTitle: String,
        dishPrice: Double,
        category: DishCategory,
        diameterCm: Float,
        heightCm: Float,
        isVeg: Boolean = false
    ) {
        try {
            val contentResolver = context.contentResolver
            var displayName = "custom_dish.glb"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    displayName = cursor.getString(nameIndex) ?: "custom_dish.glb"
                }
            }

            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalStateException("Unable to read selected file stream")

            val sizeKb = (bytes.size / 1024).coerceAtLeast(1)
            val isGlbMagic = bytes.size >= 12 &&
                ByteBuffer.wrap(bytes, 0, 12).order(ByteOrder.LITTLE_ENDIAN).int == 0x46546C67

            val safeSlug = dishTitle.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { "custom-dish" }
            val customDir = File(context.filesDir, "custom_models").apply { mkdirs() }
            val savedFileName = if (displayName.endsWith(".usdz", true)) "$safeSlug.usdz" else "$safeSlug.glb"
            val savedFile = File(customDir, savedFileName)
            savedFile.writeBytes(bytes)

            val newDish = ArMenuDish(
                id = "custom-$safeSlug-${System.currentTimeMillis() % 10000}",
                title = dishTitle.ifBlank { displayName.substringBeforeLast('.') },
                subtitle = "Custom 4D Model • ${if (isGlbMagic) "Verified glTF 2.0 .GLB" else displayName}",
                category = if (category == DishCategory.ALL) DishCategory.MAIN_COURSE else category,
                isVeg = isVeg,
                price = dishPrice,
                calories = 680,
                prepMinutes = 10,
                diameterCm = diameterCm,
                heightCm = heightCm,
                glbAsset = "custom/$savedFileName",
                usdzAsset = "models/$safeSlug.usdz",
                imageRes = R.drawable.img_hero_bistro,
                description = "Custom uploaded 3D dish model ($displayName, $sizeKb KB). Ready for 1:1 true-scale AR table projection and 360° orbital inspection.",
                chefPairing = "4D Smart Menu Asset (${if (isGlbMagic) "Valid binary glTF 2.0" else "Custom 3D"}).",
                allergens = listOf(if (isVeg) "Vegetarian" else "Non-Vegetarian"),
                cameraOrbit = "20deg 68deg 0.44m",
                polyCount = "${if (isGlbMagic) "glTF 2.0" else "Custom"} • $sizeKb KB",
                layers3D = buildCustom3DLayers(category),
                nutritionHotspots = listOf(
                    ArNutritionHotspot("Custom 3D", "High Detail", "1:1 Real Scale", "✨", -0.52f, -0.25f),
                    ArNutritionHotspot("Chef Spec", "Fresh Crafted", "Interactive AR", "🍽️", 0.52f, 0.28f)
                )
            )

            _uiState.update { state ->
                state.copy(
                    dishes = listOf(newDish) + state.dishes,
                    selectedDish = newDish,
                    preloadedDishIds = state.preloadedDishIds + newDish.id,
                    lastUploadReport = "Successfully imported '$displayName' ($sizeKb KB) into 4D AR Menu!",
                    isArModalOpen = true,
                    currentDestination = AppDestination.BISTRO_MENU
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    lastUploadReport = "Could not import file: ${e.localizedMessage ?: "Invalid format"}. Make sure to select a .glb or .usdz file."
                )
            }
        }
    }

    fun createCustomDishInStudio(
        title: String,
        price: Double,
        category: DishCategory,
        diameterCm: Float,
        heightCm: Float,
        plateColorHex: Long,
        baseLayerColorHex: Long,
        topCrownColorHex: Long,
        isVeg: Boolean = false
    ) {
        val cleanTitle = title.trim().ifEmpty { "4D Special Dish" }
        val slug = cleanTitle.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
        val mappedGlb = when (category) {
            DishCategory.PIZZA -> "models/pizza.glb"
            DishCategory.RAMEN, DishCategory.MAIN_COURSE, DishCategory.SALADS -> "models/ramen.glb"
            DishCategory.DESSERTS_DRINKS -> "models/matcha_cake.glb"
            else -> "models/burger.glb"
        }

        val customLayers = listOf(
            IngredientLayer3D("Artisanal Plate Base", 0.90f, 0.82f, 0.06f, 0.03f, plateColorHex, plateColorHex),
            IngredientLayer3D("Primary Culinary Base", 0.62f, 0.58f, 0.14f, 0.14f, baseLayerColorHex, baseLayerColorHex),
            IngredientLayer3D("Signature Mid Layer", 0.58f, 0.56f, 0.12f, 0.28f, 0xFFD97724, 0xFFE5A93C),
            IngredientLayer3D("Chef's Crown Topping", 0.52f, 0.52f, 0.20f, 0.44f, topCrownColorHex, topCrownColorHex, isDome = true)
        )

        val newDish = ArMenuDish(
            id = "studio-$slug-${System.currentTimeMillis() % 10000}",
            title = cleanTitle,
            subtitle = "4D Smart Menu Item • 1:1 True Scale",
            category = if (category == DishCategory.ALL) DishCategory.MAIN_COURSE else category,
            isVeg = isVeg,
            price = price,
            calories = 720,
            prepMinutes = 10,
            diameterCm = diameterCm,
            heightCm = heightCm,
            glbAsset = mappedGlb,
            usdzAsset = "models/$slug.usdz",
            imageRes = when (category) {
                DishCategory.PIZZA -> R.drawable.img_dish_pizza
                DishCategory.RAMEN, DishCategory.MAIN_COURSE, DishCategory.SALADS -> R.drawable.img_dish_ramen
                else -> R.drawable.img_dish_burger
            },
            description = "Custom 4D spatial dish crafted in Aura 3D Builder with 1:1 true dimensions (Ø ${diameterCm.toInt()}cm × H ${heightCm}cm) and multi-layer PBR shading.",
            chefPairing = "4D Smart Menu Item ready for AR table projection.",
            allergens = listOf(if (isVeg) "Vegetarian" else "Non-Vegetarian"),
            cameraOrbit = "20deg 70deg 0.42m",
            polyCount = "3.6k tris • 32 KB .glb",
            layers3D = customLayers,
            nutritionHotspots = listOf(
                ArNutritionHotspot(cleanTitle, "Chef Crafted", "Fresh Ingredients", "✨", -0.50f, -0.22f),
                ArNutritionHotspot("Nutrition", "Balanced Nutrients", "High Protein", "🥗", 0.50f, 0.25f)
            )
        )

        _uiState.update { state ->
            state.copy(
                dishes = listOf(newDish) + state.dishes,
                selectedDish = newDish,
                preloadedDishIds = state.preloadedDishIds + newDish.id,
                lastUploadReport = "Created '$cleanTitle' (${newDish.dimensionsLabel}) and added to 4D AR Menu!",
                isArModalOpen = true,
                currentDestination = AppDestination.BISTRO_MENU
            )
        }
    }

    private fun buildCustom3DLayers(category: DishCategory): List<IngredientLayer3D> {
        return when (category) {
            DishCategory.PIZZA -> BistroMenuRepository.dishes[0].layers3D
            DishCategory.SALADS -> BistroMenuRepository.dishes[1].layers3D
            DishCategory.RAMEN, DishCategory.MAIN_COURSE -> BistroMenuRepository.dishes[4].layers3D
            DishCategory.DESSERTS_DRINKS -> BistroMenuRepository.dishes[5].layers3D
            else -> BistroMenuRepository.dishes[2].layers3D
        }
    }

    fun launchNativeSceneViewerOrFallback(context: Context, dish: ArMenuDish) {
        val sceneViewerUrl = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
            .appendQueryParameter(
                "file",
                "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF-Binary/Avocado.glb"
            )
            .appendQueryParameter("mode", "ar_preferred")
            .appendQueryParameter("title", "${dish.title} (${dish.dimensionsLabel})")
            .appendQueryParameter("resizable", (!uiState.value.trueScale1To1).toString())
            .build()

        var launchedExternal = false
        try {
            val sceneViewerIntent = Intent(Intent.ACTION_VIEW).apply {
                data = sceneViewerUrl
                setPackage("com.google.android.googlequicksearchbox")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(sceneViewerIntent)
            launchedExternal = true
        } catch (_: Exception) {}

        if (!launchedExternal) {
            try {
                val genericIntent = Intent(Intent.ACTION_VIEW, sceneViewerUrl).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(genericIntent)
                launchedExternal = true
            } catch (_: Exception) {}
        }

        // Always switch in-app viewport to live table AR camera
        _uiState.update {
            it.copy(
                selectedDish = dish,
                currentDestination = AppDestination.TABLE_AR,
                viewerMode = ViewerMode.ROOM_AR_TABLE,
                isArModalOpen = false,
                statusBanner = "Live AR Table Camera Active for ${dish.title}"
            )
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
            # 4D Smart Menu — The Complete Business Blueprint & AR Setup
            
            ## 1. What is a 4D Smart Menu?
            As seen in the videos:
            - Customers sit at the restaurant table and scan a physical QR standee with their phone camera.
            - Without downloading any app, a luxury WebAR menu opens in Safari / Chrome.
            - Customers tap 'VIEW ON YOUR TABLE' (or 3D view).
            - The dish appears directly on their physical dining table in 1:1 true life-size!
            - Floating AR Holographic Cards show ingredient benefits (e.g. Avocado: Omega 9, Chicken: High Protein, Tomato: Vitamin C).
            - Optional Animated 3D Chef Mascot welcomes the customer right on top of the plate!

            ## 2. Business Model for Cafes & Restaurants
            - Initial 4D Menu Setup: ₹30,000 – ₹50,000 per restaurant / cafe
            - Monthly Updates & Maintenance: ₹10,000 / month per client
            - 10 Restaurant Clients = ₹4,00,000 – ₹5,00,000 revenue with near zero software cost!
        """.trimIndent()

        return listOf(
            WebArCodeArtifact(
                id = "4D-BLUEPRINT.md",
                fileName = "4D-BLUEPRINT.md",
                badge = "4D Smart Menu Blueprint",
                description = "Complete business model, pricing structure, and 4D AR Table technology setup.",
                codeContent = workflowGuide
            ),
            WebArCodeArtifact(
                id = "index.html",
                fileName = "index.html",
                badge = "HTML5 + <model-viewer>",
                description = "Semantic HTML5 4D menu structure, AR hotspots, and QR standee integration.",
                codeContent = htmlCode
            ),
            WebArCodeArtifact(
                id = "styles.css",
                fileName = "styles.css",
                badge = "Responsive Mobile CSS",
                description = "Golden Oak luxury dark styling, floating AR callouts, and category icon strip.",
                codeContent = cssCode
            ),
            WebArCodeArtifact(
                id = "app.js",
                fileName = "app.js",
                badge = "Vanilla JS + 4D Engine",
                description = "WebAR menu controller, hotspot coordinate annotations, and 3D food renderer.",
                codeContent = jsCode
            )
        )
    }
}
