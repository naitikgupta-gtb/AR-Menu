package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class AppDestination(val title: String) {
    BISTRO_MENU("AR Menu"),
    MODEL_CREATOR("3D Upload"),
    LIVE_WEBAR("Live WebAR"),
    CODE_STUDIO("WebAR Code")
}

enum class DishCategory(val displayName: String) {
    ALL("All Dishes"),
    BURGERS("Burgers & Mains"),
    RAMEN("Artisanal Ramen"),
    PIZZA("Wood-Fired Pizza"),
    DESSERTS_DRINKS("Desserts & Drinks")
}

enum class ViewerMode(val label: String) {
    ORBITAL_3D("3D Orbital"),
    ROOM_AR_TABLE("View in Room"),
    MODEL_VIEWER_WEB("<model-viewer>")
}

enum class LightingPreset(
    val label: String,
    val exposure: Float,
    val topBgHex: Long,
    val bottomBgHex: Long,
    val rimHex: Long
) {
    WARM_BISTRO("Warm Bistro", 1.05f, 0xFF2D231B, 0xFF120E0C, 0xFFE5A93C),
    STUDIO_DAYLIGHT("Daylight", 1.22f, 0xFF282E36, 0xFF111418, 0xFF90CAF9),
    CANDLELIGHT("Candlelight", 0.90f, 0xFF351D11, 0xFF0E0806, 0xFFFF8A65)
}

data class IngredientLayer3D(
    val name: String,
    val radiusTopRatio: Float,
    val radiusBottomRatio: Float,
    val heightRatio: Float,
    val yCenterRatio: Float,
    val baseColorHex: Long,
    val topColorHex: Long,
    val isDome: Boolean = false
)

data class ArMenuDish(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: DishCategory,
    val price: Double,
    val calories: Int,
    val prepMinutes: Int,
    val diameterCm: Float,
    val heightCm: Float,
    val glbAsset: String,
    val usdzAsset: String,
    @DrawableRes val imageRes: Int,
    val description: String,
    val chefPairing: String,
    val allergens: List<String>,
    val cameraOrbit: String,
    val polyCount: String,
    val layers3D: List<IngredientLayer3D>,
    val customPosterPath: String? = null
) {
    val formattedPrice: String get() = "$${String.format("%.2f", price)}"
    val dimensionsLabel: String get() = "Ø ${diameterCm.toInt()}cm × H ${heightCm}cm"
}

data class WebArCodeArtifact(
    val id: String,
    val fileName: String,
    val badge: String,
    val description: String,
    val codeContent: String
)

object BistroMenuRepository {

    val dishes: List<ArMenuDish> = listOf(
        ArMenuDish(
            id = "wagyu-truffle-burger",
            title = "Double Wagyu Truffle Smash",
            subtitle = "A5 Miyazaki Wagyu • Cave Gruyère • Brioche",
            category = DishCategory.BURGERS,
            price = 26.00,
            calories = 840,
            prepMinutes = 11,
            diameterCm = 14f,
            heightCm = 12f,
            glbAsset = "models/burger.glb",
            usdzAsset = "models/burger.usdz",
            imageRes = R.drawable.img_dish_burger,
            description = "Two seared A5 Wagyu smash patties, aged cave-ripened Gruyère, black truffle aioli, crispy shallots, and toasted Hokkaido milk brioche served on matte volcanic slate.",
            chefPairing = "Pairs with Smoked Hinoki Old Fashioned or Napa Cabernet.",
            allergens = listOf("Gluten", "Dairy", "Egg", "Sesame"),
            cameraOrbit = "25deg 72deg 0.42m",
            polyCount = "4.2k tris • 38 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Volcanic Slate Plate", 0.92f, 0.84f, 0.06f, 0.03f, 0xFF1F1E21, 0xFF2B2A2E),
                IngredientLayer3D("Toasted Brioche Base", 0.54f, 0.48f, 0.11f, 0.12f, 0xFFC77A32, 0xFFD9934C),
                IngredientLayer3D("Baby Wild Arugula", 0.58f, 0.56f, 0.05f, 0.20f, 0xFF3E7B27, 0xFF569E3A),
                IngredientLayer3D("Seared Wagyu Patty I", 0.56f, 0.56f, 0.12f, 0.29f, 0xFF421F14, 0xFF592B1D),
                IngredientLayer3D("Melted Cave Gruyère", 0.59f, 0.57f, 0.04f, 0.37f, 0xFFF5B82E, 0xFFFFCC4D),
                IngredientLayer3D("Seared Wagyu Patty II", 0.55f, 0.55f, 0.11f, 0.45f, 0xFF3D1C12, 0xFF54281B),
                IngredientLayer3D("Crispy Shallots & Truffle", 0.50f, 0.50f, 0.05f, 0.53f, 0xFF8C5828, 0xFFA86E38),
                IngredientLayer3D("Glazed Brioche Crown", 0.55f, 0.55f, 0.22f, 0.66f, 0xFFC26B22, 0xFFE08838, isDome = true)
            )
        ),
        ArMenuDish(
            id = "black-garlic-ramen",
            title = "Kurobuta Black Garlic Tonkotsu",
            subtitle = "18-Hr Pork Broth • Torch Chashu • Ajitsuke Egg",
            category = DishCategory.RAMEN,
            price = 23.50,
            calories = 790,
            prepMinutes = 9,
            diameterCm = 22f,
            heightCm = 9.5f,
            glbAsset = "models/ramen.glb",
            usdzAsset = "models/ramen.usdz",
            imageRes = R.drawable.img_dish_ramen,
            description = "18-hour simmered pork bone broth swirled with roasted black garlic mayu, torch-seared Kurobuta pork belly chashu, 6-minute jammy egg, bamboo menma, and crisp nori.",
            chefPairing = "Pairs with Junmai Daiginjo Sake or Cold-Brewed Yuzu Green Tea.",
            allergens = listOf("Soy", "Wheat", "Egg", "Sesame"),
            cameraOrbit = "0deg 65deg 0.45m",
            polyCount = "3.8k tris • 34 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Handcrafted Footed Base", 0.46f, 0.42f, 0.08f, 0.04f, 0xFF181514, 0xFF24201E),
                IngredientLayer3D("Stoneware Ramen Bowl", 0.84f, 0.52f, 0.36f, 0.24f, 0xFF211C1A, 0xFF2E2724),
                IngredientLayer3D("Alkaline Rye Noodles", 0.72f, 0.68f, 0.06f, 0.36f, 0xFFD9B468, 0xFFE8C87D),
                IngredientLayer3D("Tonkotsu & Black Garlic Mayu", 0.78f, 0.76f, 0.05f, 0.41f, 0xFFB88C56, 0xFFCFA36B),
                IngredientLayer3D("Torch-Seared Chashu Belly", 0.38f, 0.38f, 0.06f, 0.46f, 0xFF6E3219, 0xFF8C4324),
                IngredientLayer3D("6-Minute Jammy Tamago", 0.22f, 0.22f, 0.11f, 0.50f, 0xFFF28C18, 0xFFFFA733, isDome = true)
            )
        ),
        ArMenuDish(
            id = "burrata-truffle-pizza",
            title = "Wood-Fired Burrata & Tartufo",
            subtitle = "72-Hr Sourdough • Pugliese Burrata • Black Truffle",
            category = DishCategory.PIZZA,
            price = 28.00,
            calories = 910,
            prepMinutes = 8,
            diameterCm = 32f,
            heightCm = 4.5f,
            glbAsset = "models/pizza.glb",
            usdzAsset = "models/pizza.usdz",
            imageRes = R.drawable.img_dish_pizza,
            description = "Blistered 72-hour cold-fermented Neapolitan sourdough crust, crushed San Marzano DOP tomatoes, whole creamy Pugliese burrata, shaved Umbrian black truffle, and sweet basil.",
            chefPairing = "Pairs with Nebbiolo d'Alba or Aperol Blood Orange Spritz.",
            allergens = listOf("Gluten", "Milk"),
            cameraOrbit = "15deg 58deg 0.55m",
            polyCount = "3.4k tris • 31 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Basalt Pizza Stone", 0.96f, 0.94f, 0.05f, 0.03f, 0xFF222020, 0xFF2E2B2B),
                IngredientLayer3D("Blistered Sourdough Cornicione", 0.88f, 0.86f, 0.09f, 0.10f, 0xFFC88746, 0xFFDEA05E),
                IngredientLayer3D("San Marzano Tomato Reduction", 0.74f, 0.74f, 0.04f, 0.16f, 0xFFB8291B, 0xFFD43828),
                IngredientLayer3D("Fior di Latte & Truffle Shavings", 0.66f, 0.66f, 0.04f, 0.20f, 0xFFEFE6D5, 0xFFFDF7EB),
                IngredientLayer3D("Whole Pugliese Burrata Crown", 0.28f, 0.28f, 0.16f, 0.27f, 0xFFF7F4EB, 0xFFFFFFFF, isDome = true)
            )
        ),
        ArMenuDish(
            id = "uji-matcha-mille-crepe",
            title = "Uji Ceremonial Matcha Mille Crêpe",
            subtitle = "20 Handmade Crêpes • Uji Matcha • Gold Leaf",
            category = DishCategory.DESSERTS_DRINKS,
            price = 16.50,
            calories = 460,
            prepMinutes = 5,
            diameterCm = 16f,
            heightCm = 7.5f,
            glbAsset = "models/matcha_cake.glb",
            usdzAsset = "models/matcha_cake.usdz",
            imageRes = R.drawable.img_hero_bistro,
            description = "Twenty paper-thin French crêpe layers brushed with ceremonial-grade Uji matcha diplomate cream, finished with Hokkaido chantilly and 24k edible gold leaf.",
            chefPairing = "Pairs with Hand-Whisked Usucha Matcha or Espresso Tonic.",
            allergens = listOf("Dairy", "Egg", "Gluten"),
            cameraOrbit = "-20deg 70deg 0.38m",
            polyCount = "3.2k tris • 29 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Porcelain Dessert Plate", 0.82f, 0.76f, 0.05f, 0.03f, 0xFF232326, 0xFF303034),
                IngredientLayer3D("Lower 10 Crêpe & Cream Layers", 0.48f, 0.48f, 0.16f, 0.14f, 0xFF5E943A, 0xFF74B04B),
                IngredientLayer3D("Upper 10 Crêpe & Cream Layers", 0.48f, 0.48f, 0.16f, 0.30f, 0xFF669E40, 0xFF7EB856),
                IngredientLayer3D("Ceremonial Uji Matcha Dusting", 0.49f, 0.49f, 0.04f, 0.40f, 0xFF437324, 0xFF528A2E),
                IngredientLayer3D("Hokkaido Chantilly & Gold Leaf", 0.18f, 0.18f, 0.12f, 0.46f, 0xFFF7F2E6, 0xFFFFFBF2, isDome = true)
            )
        ),
        ArMenuDish(
            id = "smoked-hinoki-old-fashioned",
            title = "Smoked Hinoki & Yuzu Old Fashioned",
            subtitle = "Japanese Single Malt • Hand-Carved Ice Sphere",
            category = DishCategory.DESSERTS_DRINKS,
            price = 19.00,
            calories = 165,
            prepMinutes = 4,
            diameterCm = 9.5f,
            heightCm = 10f,
            glbAsset = "models/old_fashioned.glb",
            usdzAsset = "models/old_fashioned.usdz",
            imageRes = R.drawable.img_hero_bistro,
            description = "Aged Japanese single malt whisky stirred with Okinawa black sugar and aromatic yuzu bitters over a hand-carved crystal ice sphere smoked tableside with Hinoki wood.",
            chefPairing = "Ideal aperitif or digestif alongside the Double Wagyu Smash.",
            allergens = listOf("Gluten-Free", "Vegan"),
            cameraOrbit = "10deg 74deg 0.32m",
            polyCount = "2.9k tris • 27 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Charred Hinoki Wood Coaster", 0.56f, 0.56f, 0.06f, 0.03f, 0xFF382415, 0xFF4D321E),
                IngredientLayer3D("Heavy Crystal Rocks Base", 0.42f, 0.38f, 0.10f, 0.11f, 0xFF8FB8CC, 0xFFB8DCEB),
                IngredientLayer3D("Aged Japanese Whisky Pour", 0.40f, 0.39f, 0.24f, 0.28f, 0xFFD4822A, 0xFFE89B42),
                IngredientLayer3D("Hand-Carved Crystal Ice Sphere", 0.26f, 0.26f, 0.20f, 0.36f, 0xFFDDF2FF, 0xFFF2FAFF, isDome = true)
            )
        )
    )
}
