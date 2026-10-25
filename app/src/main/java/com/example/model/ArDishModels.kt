package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class AppDestination(val title: String) {
    BISTRO_MENU("4D Menu"),
    TABLE_AR("Table AR"),
    TABLE_QR("Table QR"),
    MODEL_CREATOR("3D Upload"),
    LIVE_WEBAR("Live WebAR"),
    CODE_STUDIO("WebAR Code")
}

enum class DishCategory(val displayName: String, val iconEmoji: String) {
    ALL("All Dishes", "✨"),
    STARTERS("Starters", "🥗"),
    MAIN_COURSE("Main Course", "🍝"),
    BURGERS("Burgers", "🍔"),
    PIZZA("Pizza & Italian", "🍕"),
    RAMEN("Artisanal Ramen", "🍜"),
    SALADS("Salads & Bowls", "🥗"),
    DESSERTS_DRINKS("Desserts", "🍰")
}

enum class DietaryFilter(val label: String) {
    ALL("All"),
    VEG("🟢 Veg"),
    NON_VEG("🔴 Non-Veg")
}

enum class ViewerMode(val label: String) {
    ROOM_AR_TABLE("View on Table (AR)"),
    ORBITAL_3D("3D Orbital"),
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

data class ArNutritionHotspot(
    val name: String,
    val benefit1: String,
    val benefit2: String,
    val iconEmoji: String,
    val xOffsetRatio: Float,
    val yOffsetRatio: Float
)

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
    val isVeg: Boolean,
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
    val nutritionHotspots: List<ArNutritionHotspot>,
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
        // 1. Italian Supreme Wood-Fired Pizza (As featured in Video 1 & Video 2 with miniature animated chef mascot!)
        ArMenuDish(
            id = "italian-supreme-pizza",
            title = "Italian Supreme Pizza",
            subtitle = "Wood-Fired Crust • Pepperoni / Paneer • Fresh Basil",
            category = DishCategory.PIZZA,
            isVeg = false,
            price = 16.95,
            calories = 880,
            prepMinutes = 8,
            diameterCm = 32f,
            heightCm = 4.5f,
            glbAsset = "models/pizza.glb",
            usdzAsset = "models/pizza.usdz",
            imageRes = R.drawable.img_dish_pizza,
            description = "Traditional hand-tossed Neapolitan dough, Italian sausage, smoked pepperoni, bell peppers, Kalamata olives, fresh mozzarella, and sweet basil on a rustic wooden paddle.",
            chefPairing = "Features 3D Animated Chef Mascot on table projection.",
            allergens = listOf("Gluten", "Dairy"),
            cameraOrbit = "15deg 58deg 0.55m",
            polyCount = "4.2k tris • 38 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Rustic Wooden Pizza Paddle", 0.96f, 0.94f, 0.05f, 0.03f, 0xFF654321, 0xFF8B5A2B),
                IngredientLayer3D("Blistered Sourdough Crust", 0.88f, 0.86f, 0.09f, 0.10f, 0xFFC88746, 0xFFDEA05E),
                IngredientLayer3D("San Marzano Herb Marinara", 0.74f, 0.74f, 0.04f, 0.16f, 0xFFB8291B, 0xFFD43828),
                IngredientLayer3D("Fior di Latte Mozzarella & Olives", 0.68f, 0.68f, 0.04f, 0.20f, 0xFFEFE6D5, 0xFFFDF7EB),
                IngredientLayer3D("Gourmet Pepperoni & Basil", 0.45f, 0.45f, 0.05f, 0.25f, 0xFF8C1D18, 0xFFA82620, isDome = true)
            ),
            nutritionHotspots = listOf(
                ArNutritionHotspot("Mozzarella", "Rich Calcium", "High Protein", "🧀", -0.55f, -0.25f),
                ArNutritionHotspot("Black Olives", "Good Fats", "Antioxidants", "🫒", 0.52f, -0.30f),
                ArNutritionHotspot("Fresh Basil", "Vitamin K", "Aromatic Herb", "🌿", -0.45f, 0.35f),
                ArNutritionHotspot("San Marzano", "Lycopene", "Heart Healthy", "🍅", 0.50f, 0.30f)
            )
        ),

        // 2. Grilled Chicken & Avocado Salad (The exact dish from Video 4 with all 7 floating AR callouts!)
        ArMenuDish(
            id = "grilled-chicken-salad",
            title = "Grilled Chicken Salad",
            subtitle = "Avocado • Olives • Tomato • Cucumber • Herb Dressing",
            category = DishCategory.SALADS,
            isVeg = false,
            price = 25.00,
            calories = 380,
            prepMinutes = 7,
            diameterCm = 22f,
            heightCm = 9f,
            glbAsset = "models/ramen.glb",
            usdzAsset = "models/ramen.usdz",
            imageRes = R.drawable.img_hero_bistro,
            description = "Tender chargrilled chicken breast strips served on crisp mixed greens with ripe Hass avocado, vine tomatoes, crunchy cucumbers, Kalamata olives, and house vinaigrette.",
            chefPairing = "4D Smart Menu signature dish with full AR nutrition callouts.",
            allergens = listOf("Gluten-Free", "High Protein"),
            cameraOrbit = "0deg 65deg 0.42m",
            polyCount = "3.8k tris • 34 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("White Ceramic Salad Bowl", 0.88f, 0.56f, 0.28f, 0.18f, 0xFFE8E8E8, 0xFFFFFFFF),
                IngredientLayer3D("Crisp Romaine & Baby Spinach", 0.76f, 0.72f, 0.08f, 0.32f, 0xFF2E7D32, 0xFF4CAF50),
                IngredientLayer3D("Vine Tomatoes & Cucumbers", 0.68f, 0.65f, 0.08f, 0.40f, 0xFFD32F2F, 0xFFE53935),
                IngredientLayer3D("Chargrilled Herb Chicken", 0.48f, 0.48f, 0.12f, 0.48f, 0xFF8D6E63, 0xFFA1887F, isDome = true)
            ),
            nutritionHotspots = listOf(
                ArNutritionHotspot("Chicken", "High Protein", "Low Carbs", "🍗", -0.58f, -0.40f),
                ArNutritionHotspot("Avocado", "Omega 9", "Good for Heart", "🥑", -0.70f, 0.10f),
                ArNutritionHotspot("Tomato", "Vitamin C", "Antioxidants", "🍅", -0.52f, 0.52f),
                ArNutritionHotspot("Cucumber", "Hydration", "Low Calorie", "🥒", 0.58f, 0.45f),
                ArNutritionHotspot("Olives", "Good Fats", "Anti-inflammatory", "🫒", 0.60f, -0.45f),
                ArNutritionHotspot("Onion", "Digestion", "Natural Detox", "🧅", 0.70f, -0.15f),
                ArNutritionHotspot("Capsicum", "Vitamin C", "Immune Boost", "🫑", 0.65f, 0.15f)
            )
        ),

        // 3. Double Wagyu Truffle Smash (From Video 4 Burger Menu)
        ArMenuDish(
            id = "wagyu-truffle-burger",
            title = "Classic Oak Truffle Burger",
            subtitle = "Double A5 Wagyu • Cave Gruyère • Hokkaido Brioche",
            category = DishCategory.BURGERS,
            isVeg = false,
            price = 26.00,
            calories = 840,
            prepMinutes = 11,
            diameterCm = 14f,
            heightCm = 12f,
            glbAsset = "models/burger.glb",
            usdzAsset = "models/burger.usdz",
            imageRes = R.drawable.img_dish_burger,
            description = "Two seared A5 Miyazaki Wagyu smash patties, cave-ripened Gruyère cheese, black truffle emulsion, crispy shallots, and toasted milk brioche on dark volcanic slate.",
            chefPairing = "Pairs with Smoked Hinoki Old Fashioned or Napa Cabernet.",
            allergens = listOf("Gluten", "Dairy", "Egg", "Sesame"),
            cameraOrbit = "25deg 72deg 0.42m",
            polyCount = "4.2k tris • 38 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Volcanic Slate Plate", 0.92f, 0.84f, 0.06f, 0.03f, 0xFF1F1E21, 0xFF2B2A2E),
                IngredientLayer3D("Toasted Brioche Base", 0.54f, 0.48f, 0.11f, 0.12f, 0xFFC77A32, 0xFFD9934C),
                IngredientLayer3D("Wild Arugula & Shallots", 0.58f, 0.56f, 0.05f, 0.20f, 0xFF3E7B27, 0xFF569E3A),
                IngredientLayer3D("Wagyu Smash Patty I", 0.56f, 0.56f, 0.12f, 0.29f, 0xFF421F14, 0xFF592B1D),
                IngredientLayer3D("Melted Cave Gruyère", 0.59f, 0.57f, 0.04f, 0.37f, 0xFFF5B82E, 0xFFFFCC4D),
                IngredientLayer3D("Wagyu Smash Patty II", 0.55f, 0.55f, 0.11f, 0.45f, 0xFF3D1C12, 0xFF54281B),
                IngredientLayer3D("Glazed Brioche Crown", 0.55f, 0.55f, 0.22f, 0.66f, 0xFFC26B22, 0xFFE08838, isDome = true)
            ),
            nutritionHotspots = listOf(
                ArNutritionHotspot("Wagyu Beef", "High Iron", "Rich Protein", "🥩", -0.58f, -0.20f),
                ArNutritionHotspot("Gruyère", "Aged Calcium", "Zero Carbs", "🧀", 0.55f, -0.15f),
                ArNutritionHotspot("Truffle Aioli", "Antioxidants", "Gourmet Umami", "🍄", -0.52f, 0.30f),
                ArNutritionHotspot("Brioche Bun", "Quick Energy", "Hokkaido Milk", "🍞", 0.52f, 0.32f)
            )
        ),

        // 4. Burrata with Prosciutto & Truffle (From Video 4 Starters menu)
        ArMenuDish(
            id = "burrata-tartufo-starter",
            title = "Burrata with Prosciutto",
            subtitle = "Pugliese Burrata • Shaved Truffle • Heirloom Tomatoes",
            category = DishCategory.STARTERS,
            isVeg = true,
            price = 24.00,
            calories = 520,
            prepMinutes = 6,
            diameterCm = 18f,
            heightCm = 7f,
            glbAsset = "models/pizza.glb",
            usdzAsset = "models/pizza.usdz",
            imageRes = R.drawable.img_dish_pizza,
            description = "Whole hand-tied creamy Pugliese burrata cheese, 24-month aged San Daniele prosciutto, heirloom cherry tomatoes, cold-pressed olive oil, and balsamic glaze.",
            chefPairing = "Signature Italian starter with holographic ingredient tags.",
            allergens = listOf("Dairy", "Nut-Free"),
            cameraOrbit = "10deg 60deg 0.38m",
            polyCount = "3.4k tris • 31 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Matte Ceramic Starter Plate", 0.88f, 0.82f, 0.06f, 0.04f, 0xFF2B2826, 0xFF3D3734),
                IngredientLayer3D("Heirloom Tomato Carpaccio", 0.72f, 0.70f, 0.05f, 0.12f, 0xFFC62828, 0xFFD32F2F),
                IngredientLayer3D("Creamy Pugliese Burrata", 0.38f, 0.38f, 0.22f, 0.26f, 0xFFF5F5F0, 0xFFFFFFFF, isDome = true)
            ),
            nutritionHotspots = listOf(
                ArNutritionHotspot("Burrata", "High Calcium", "Rich Protein", "🧀", -0.55f, -0.22f),
                ArNutritionHotspot("Olive Oil", "Omega 3 & 9", "Heart Shield", "🫒", 0.55f, -0.25f),
                ArNutritionHotspot("Heirloom Tomato", "Vitamin C", "Antioxidants", "🍅", 0.52f, 0.30f)
            )
        ),

        // 5. Kurobuta Black Garlic Tonkotsu Ramen
        ArMenuDish(
            id = "black-garlic-ramen",
            title = "Kurobuta Tonkotsu Ramen",
            subtitle = "18-Hr Broth • Torch Chashu • Ajitsuke Tamago",
            category = DishCategory.MAIN_COURSE,
            isVeg = false,
            price = 23.50,
            calories = 790,
            prepMinutes = 9,
            diameterCm = 22f,
            heightCm = 9.5f,
            glbAsset = "models/ramen.glb",
            usdzAsset = "models/ramen.usdz",
            imageRes = R.drawable.img_dish_ramen,
            description = "Slow-simmered rich pork marrow broth, roasted black garlic mayu, torch-seared pork belly chashu, 6-minute jammy egg, bamboo menma, and artisanal alkaline noodles.",
            chefPairing = "Pairs with Junmai Daiginjo Sake or Cold-Brewed Yuzu Tea.",
            allergens = listOf("Soy", "Wheat", "Egg", "Sesame"),
            cameraOrbit = "0deg 65deg 0.45m",
            polyCount = "3.8k tris • 34 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Stoneware Footed Base", 0.46f, 0.42f, 0.08f, 0.04f, 0xFF181514, 0xFF24201E),
                IngredientLayer3D("Handcrafted Ceramic Bowl", 0.84f, 0.52f, 0.36f, 0.24f, 0xFF211C1A, 0xFF2E2724),
                IngredientLayer3D("Alkaline Rye Noodles", 0.72f, 0.68f, 0.06f, 0.36f, 0xFFD9B468, 0xFFE8C87D),
                IngredientLayer3D("Black Garlic Tonkotsu Broth", 0.78f, 0.76f, 0.05f, 0.41f, 0xFFB88C56, 0xFFCFA36B),
                IngredientLayer3D("Torch-Seared Chashu Belly", 0.38f, 0.38f, 0.06f, 0.46f, 0xFF6E3219, 0xFF8C4324),
                IngredientLayer3D("6-Min Ajitsuke Tamago", 0.22f, 0.22f, 0.11f, 0.50f, 0xFFF28C18, 0xFFFFA733, isDome = true)
            ),
            nutritionHotspots = listOf(
                ArNutritionHotspot("Bone Broth", "Natural Collagen", "Gut Health", "🍲", -0.55f, -0.35f),
                ArNutritionHotspot("Chashu", "High Protein", "Vitamin B1", "🥓", 0.55f, -0.30f),
                ArNutritionHotspot("Ajitsuke Egg", "Choline", "Brain Food", "🥚", -0.48f, 0.35f),
                ArNutritionHotspot("Black Garlic", "Allicin", "Immune Defense", "🧄", 0.52f, 0.35f)
            )
        ),

        // 6. Uji Ceremonial Matcha Mille Crêpe
        ArMenuDish(
            id = "uji-matcha-mille-crepe",
            title = "Ceremonial Matcha Mille Crêpe",
            subtitle = "20 Handmade Crêpes • Uji Matcha • 24k Gold",
            category = DishCategory.DESSERTS_DRINKS,
            isVeg = true,
            price = 16.50,
            calories = 460,
            prepMinutes = 5,
            diameterCm = 16f,
            heightCm = 7.5f,
            glbAsset = "models/matcha_cake.glb",
            usdzAsset = "models/matcha_cake.usdz",
            imageRes = R.drawable.img_hero_bistro,
            description = "Twenty delicate French crêpe layers layered with ceremonial Uji matcha diplomat cream, Hokkaido chantilly, and edible gold leaf.",
            chefPairing = "Signature dessert with antioxidant hotspot telemetry.",
            allergens = listOf("Dairy", "Egg", "Gluten"),
            cameraOrbit = "-20deg 70deg 0.38m",
            polyCount = "3.2k tris • 29 KB .glb",
            layers3D = listOf(
                IngredientLayer3D("Porcelain Dessert Plate", 0.82f, 0.76f, 0.05f, 0.03f, 0xFF232326, 0xFF303034),
                IngredientLayer3D("Lower 10 Crêpe & Cream Layers", 0.48f, 0.48f, 0.16f, 0.14f, 0xFF5E943A, 0xFF74B04B),
                IngredientLayer3D("Upper 10 Crêpe & Cream Layers", 0.48f, 0.48f, 0.16f, 0.30f, 0xFF669E40, 0xFF7EB856),
                IngredientLayer3D("Ceremonial Uji Matcha Dusting", 0.49f, 0.49f, 0.04f, 0.40f, 0xFF437324, 0xFF528A2E),
                IngredientLayer3D("Hokkaido Chantilly & Gold Leaf", 0.18f, 0.18f, 0.12f, 0.46f, 0xFFF7F2E6, 0xFFFFFBF2, isDome = true)
            ),
            nutritionHotspots = listOf(
                ArNutritionHotspot("Uji Matcha", "L-Theanine", "Calm Focus", "🍵", -0.52f, -0.25f),
                ArNutritionHotspot("EGCG", "Super Antioxidant", "Cell Renewal", "🍃", 0.52f, -0.20f),
                ArNutritionHotspot("Gold Leaf", "Edible Luxury", "Pure 24k", "✨", 0.48f, 0.32f)
            )
        )
    )
}
