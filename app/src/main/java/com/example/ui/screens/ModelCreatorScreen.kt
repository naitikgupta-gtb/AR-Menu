package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.DishCategory
import com.example.ui.theme.BistroCardBorder
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.BistroSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald

@Composable
fun ModelCreatorScreen(
    lastUploadReport: String?,
    onImportCustomGlbUri: (Uri, String, Double, DishCategory, Float, Float) -> Unit,
    onGenerate3DFromPhotoUri: (Uri, String, Double, DishCategory, Float, Float) -> Unit,
    onCreateStudioDish: (String, Double, DishCategory, Float, Float, Long, Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var dishTitle by remember { mutableStateOf("Royal Dum Biryani Handi") }
    var dishPriceText by remember { mutableStateOf("24.00") }
    var selectedCategory by remember { mutableStateOf(DishCategory.BURGERS) }
    var diameterCm by remember { mutableFloatStateOf(20f) }
    var heightCm by remember { mutableFloatStateOf(11f) }
    var copiedPromptIndex by remember { mutableIntStateOf(-1) }

    val plateColors = listOf(
        "Volcanic Slate" to 0xFF222022L,
        "Warm Terracotta" to 0xFF6E3524L,
        "Kyoto Ceramic" to 0xFF1B2830L,
        "White Marble" to 0xFFDAD6CEL
    )
    val crownColors = listOf(
        "Saffron Glaze" to 0xFFE5A93CL,
        "Matcha Uji" to 0xFF5E943AL,
        "Crimson Berry" to 0xFFB8291BL,
        "Ivory Burrata" to 0xFFF7F4EBL
    )

    var selectedPlateIdx by remember { mutableIntStateOf(0) }
    var selectedCrownIdx by remember { mutableIntStateOf(0) }

    // Zero-permission Android Photo Picker for Option 2 (Single Photo -> 3D Dish)
    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val price = dishPriceText.toDoubleOrNull() ?: 24.0
            onGenerate3DFromPhotoUri(
                uri,
                dishTitle,
                price,
                selectedCategory,
                diameterCm,
                heightCm
            )
        }
    }

    // Zero-permission File Picker for .GLB / .USDZ upload from Meshy.ai / Tripo3D
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val price = dishPriceText.toDoubleOrNull() ?: 24.0
            onImportCustomGlbUri(
                uri,
                dishTitle,
                price,
                selectedCategory,
                diameterCm,
                heightCm
            )
        }
    }

    val aiTextPrompts = remember {
        listOf(
            "Biryani / Curry Bowl" to "A single photorealistic ceramic handi bowl of saffron chicken dum biryani topped with fried onions, mint leaves, and boiled egg, isolated on plain background, game-ready PBR 3D food asset, centered",
            "Gourmet Burger" to "A photorealistic double smash burger on a matte black ceramic plate with melted cheddar cheese, lettuce, and glossy brioche bun, isolated object, clean geometry, 3D food scan style",
            "Wood-Fired Pizza" to "Whole Neapolitan margherita pizza with fresh burrata cheese and basil leaves on a round dark wooden board, top-down 45-degree view, isolated 3D food model, high-resolution PBR texture"
        )
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .testTag("model_creator_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (lastUploadReport != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SpatialEmerald.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SpatialEmerald)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SpatialEmerald
                            )
                            Text(
                                text = lastUploadReport,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 1. Single Photo to 3D + Upload Meshy/Tripo .GLB Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = BistroSurface),
                    border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = SaffronGold
                            )
                            Column {
                                Text(
                                    text = "OPTION 2 • SINGLE PHOTO OR AI .GLB UPLOAD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaffronGold
                                )
                                Text(
                                    text = "1 Photo se 3D Dish Banaye ya .GLB Upload Karein",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = dishTitle,
                                onValueChange = { dishTitle = it },
                                label = { Text("Dish Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1.6f)
                                    .testTag("custom_dish_title_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SaffronGold,
                                    unfocusedBorderColor = BistroCardBorder
                                )
                            )

                            OutlinedTextField(
                                value = dishPriceText,
                                onValueChange = { dishPriceText = it },
                                label = { Text("Price ($)") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .testTag("custom_dish_price_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SaffronGold,
                                    unfocusedBorderColor = BistroCardBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DishCategory.entries.filter { it != DishCategory.ALL }.forEach { cat ->
                                val selected = selectedCategory == cat
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { selectedCategory = cat },
                                    color = if (selected) SaffronGold else BistroElevated,
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, if (selected) SaffronGold else BistroCardBorder)
                                ) {
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) ObsidianBg else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                tint = SpatialEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "1:1 AR Table Scale: Ø ${diameterCm.toInt()}cm × H ${heightCm.toInt()}cm",
                                style = MaterialTheme.typography.labelLarge,
                                color = SpatialEmerald
                            )
                        }

                        Slider(
                            value = diameterCm,
                            onValueChange = { diameterCm = it },
                            valueRange = 10f..36f,
                            colors = SliderDefaults.colors(
                                thumbColor = SaffronGold,
                                activeTrackColor = SaffronGold
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Primary Button: Pick 1 Dish Photo -> Instant 3D .GLB Dish
                        Button(
                            onClick = {
                                singlePhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("pick_single_photo_3d_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronGold,
                                contentColor = ObsidianBg
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pick 1 Dish Photo -> Generate 3D Dish (.GLB)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Secondary Row: Upload .GLB/.USDZ from Meshy/Tripo OR Build Procedural 3D
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { filePickerLauncher.launch("*/*") },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(48.dp)
                                    .testTag("upload_glb_file_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, SaffronGold)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = SaffronGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Upload .GLB / .USDZ",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SaffronGold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val price = dishPriceText.toDoubleOrNull() ?: 24.0
                                    onCreateStudioDish(
                                        dishTitle,
                                        price,
                                        selectedCategory,
                                        diameterCm,
                                        heightCm,
                                        plateColors[selectedPlateIdx].second,
                                        0xFF8C4A27L,
                                        crownColors[selectedCrownIdx].second
                                    )
                                },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(48.dp)
                                    .testTag("generate_3d_dish_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, BistroCardBorder)
                            ) {
                                Text(
                                    text = "Instant 3D Template",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 2. Ready-to-Copy AI Prompts for Meshy.ai / Tripo3D (Text-to-3D)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BistroSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BistroCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "READY-TO-COPY AI PROMPTS (MESHY.AI / TRIPO3D)",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Text-to-3D ke liye 1-Tap Copy Prompts",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        aiTextPrompts.forEachIndexed { idx, (label, promptText) ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText(label, promptText))
                                        copiedPromptIndex = idx
                                    },
                                color = BistroElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (copiedPromptIndex == idx) SpatialEmerald else BistroCardBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = SaffronGold
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (copiedPromptIndex == idx) Icons.Default.Verified else Icons.Default.ContentCopy,
                                                contentDescription = "Copy prompt",
                                                tint = if (copiedPromptIndex == idx) SpatialEmerald else SaffronGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (copiedPromptIndex == idx) "Copied!" else "Copy Prompt",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (copiedPromptIndex == idx) SpatialEmerald else SaffronGold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = promptText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Detailed Step-by-Step Option 2 Guide Cards
            item {
                WorkflowGuideCard(
                    stepBadge = "STEP 1 • SAHI PHOTO KAISE LEIN (PHOTO PREPARATION)",
                    title = "1 Clean Dish Photo Ready Karein",
                    icon = Icons.Default.AddPhotoAlternate,
                    bullets = listOf(
                        "Angle: Dish ki photo 45° angle (thoda upar aur samne) se click karein taaki पूरी plate aur dish ki height dono saaf dikhe.",
                        "Lighting: Bright indoor light ya natural window light use karein. Hard black shadows na hon.",
                        "Background Remove (Optional): Agar table par chammach/glass rakhe hain, toh unhe hata dein taaki AI sirf dish + plate ka 3D model banaye."
                    )
                )
            }

            item {
                WorkflowGuideCard(
                    stepBadge = "STEP 2 • MESHY.AI YA TRIPO3D.AI ME GENERATE KAREIN",
                    title = "30–60 Seconds me AI se 3D Model Banayein",
                    icon = Icons.Default.Layers,
                    bullets = listOf(
                        "1. Browser me `meshy.ai` ya `tripo3d.ai` open karein aur Free Account se sign-in karein.",
                        "2. Dashboard par 'Image to 3D' (ya 'Text to 3D') select karein.",
                        "3. Apni dish ki photo upload karein (ya upar diya gaya prompt paste karein) aur 'Generate' dabayein.",
                        "4. 30–60 seconds me 4 previews aayenge -> Sabse realistic preview par 'Refine / Texture' click karein."
                    )
                )
            }

            item {
                WorkflowGuideCard(
                    stepBadge = "STEP 3 • .GLB & .USDZ DOWNLOAD AUR APP ME UPLOAD",
                    title = "Download Settings & Instant AR Menu Integration",
                    icon = Icons.Default.ViewInAr,
                    bullets = listOf(
                        "1. Download Button par click karein -> Target Polycount ko 'Low/Medium (10k–15k)' par set karein taaki mobile par 1 second me खुल jaye.",
                        "2. Do formats download karein: '.glb' (Android & WebAR ke liye) aur '.usdz' (iPhone AR Quick Look ke liye).",
                        "3. Is app me wapas aakar upar 'Upload .GLB / .USDZ' button dabayein aur apni downloaded `.glb` file select karein — aapki dish turant 3D aur Room AR me live dikhne lagegi!"
                    )
                )
            }
        }
    }
}

@Composable
private fun WorkflowGuideCard(
    stepBadge: String,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bullets: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BistroSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BistroCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SaffronGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stepBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = SaffronGold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            bullets.forEach { bullet ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SaffronGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
