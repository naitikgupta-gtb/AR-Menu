package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArMenuDish
import com.example.ui.theme.BistroCardBorder
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.BistroSurface
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald

/**
 * Screen displaying the physical restaurant Acrylic Table Standee QR code
 * as seen in Videos 1, 2, 3 & 4 ("See It Before You Believe It - 4D Smart Menu").
 */
@Composable
fun TableQrStandeeScreen(
    selectedDish: ArMenuDish,
    onLaunchDishAr: (ArMenuDish) -> Unit,
    onOpenLiveWebAr: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTableNumber by remember { mutableIntStateOf(7) }
    var copiedToClipboard by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val qrUrl = remember(selectedTableNumber) {
        "https://smartmenu.ar/golden-oak/table-$selectedTableNumber"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("table_qr_standee_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Headline Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = SaffronGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "RESTAURANT TABLE STANDEE GENERATOR",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "See It Before You Believe It.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "The 4D Smart Menu physical acrylic standee for dining tables",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Table Number Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            colors = CardDefaults.cardColors(containerColor = BistroSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BistroCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Active Table Number",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold
                    )
                    Text(
                        text = "Table #$selectedTableNumber (VIP Booth)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3, 7, 12, 18).forEach { tableNum ->
                        val isSelected = selectedTableNumber == tableNum
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedTableNumber = tableNum },
                            color = if (isSelected) SaffronGold else BistroElevated,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) SaffronGold else BistroCardBorder)
                        ) {
                            Text(
                                text = "T$tableNum",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) ObsidianBg else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // The Physical Acrylic Standee Preview (Card matching Video 4 & Video 1)
        Card(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .testTag("acrylic_standee_card"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianBg),
            border = BorderStroke(2.dp, SaffronGold),
            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1712),
                                ObsidianBg,
                                Color(0xFF15100C)
                            )
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gold Brand Crest Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "THE GOLDEN OAK",
                        style = MaterialTheme.typography.titleMedium,
                        color = SaffronGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = "4D SMART MENU EXPERIENCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // QR Code Container Box with Gold Corners
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(Color.White, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeVectorVisual(
                        tableNumber = selectedTableNumber,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Center Logo inside QR
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(ObsidianBg, RoundedCornerShape(12.dp))
                            .border(1.5.dp, SaffronGold, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "4D",
                            style = MaterialTheme.typography.titleMedium,
                            color = SaffronGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Table number badge
                Surface(
                    color = BistroElevated,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, BistroCardBorder)
                ) {
                    Text(
                        text = "TABLE #$selectedTableNumber • DINE-IN AR",
                        style = MaterialTheme.typography.labelMedium,
                        color = SaffronGold,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Scan with your phone camera to see dishes in 3D AR right on this table before ordering",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Key Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StandeeMiniBadge(icon = "📱", title = "Zero App", subtitle = "WebAR")
                    StandeeMiniBadge(icon = "🥗", title = "Holographic", subtitle = "Nutrition")
                    StandeeMiniBadge(icon = "👨‍🍳", title = "3D Chef", subtitle = "Mascot")
                }
            }
        }

        // Action Buttons for restaurant testing & QR simulation
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onLaunchDishAr(selectedDish) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("simulate_table_qr_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SaffronGold,
                    contentColor = ObsidianBg
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simulate Guest Camera Scan (Open 4D Dish)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenLiveWebAr,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open WebAR Link",
                        style = MaterialTheme.typography.labelLarge,
                        color = SaffronGold
                    )
                }

                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(qrUrl))
                        copiedToClipboard = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BistroCardBorder)
                ) {
                    Icon(
                        imageVector = if (copiedToClipboard) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = if (copiedToClipboard) SpatialEmerald else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (copiedToClipboard) "Copied URL!" else "Copy Table URL",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (copiedToClipboard) SpatialEmerald else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Restaurant Standee Commercial Value Card (Matching Video 3)
        Card(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BistroSurface),
            border = BorderStroke(1.dp, BistroCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SpatialEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Why Restaurants Use 4D Smart Menus",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                StandeeFeatureBullet(
                    emoji = "📈",
                    title = "+35% Average Order Value",
                    desc = "Guests order higher-margin dishes after visualizing real portion sizes in 3D."
                )
                StandeeFeatureBullet(
                    emoji = "⚡",
                    title = "0 Seconds Customer Onboarding",
                    desc = "Zero app install required. Native iOS Quick Look & Android Scene Viewer launch instantly."
                )
                StandeeFeatureBullet(
                    emoji = "🥑",
                    title = "High Trust Nutrition Holograms",
                    desc = "Floating ingredient cards display macro benefits (Omega 9, Protein, Vitamins) for health-conscious diners."
                )
            }
        }
    }
}

@Composable
private fun StandeeMiniBadge(
    icon: String,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun StandeeFeatureBullet(
    emoji: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = SaffronGold,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Procedural stylized QR code pattern with finder patterns matching dining table standees.
 */
@Composable
private fun QrCodeVectorVisual(
    tableNumber: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cellCount = 21
        val cellSize = size.width / cellCount
        val darkColor = Color(0xFF14100D)

        // Draw Finder Patterns (Top-Left, Top-Right, Bottom-Left)
        fun drawFinder(cx: Float, cy: Float) {
            // Outer 7x7 square
            drawRoundRect(
                color = darkColor,
                topLeft = Offset(cx, cy),
                size = Size(7 * cellSize, 7 * cellSize),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            // Inner white 5x5 square
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cx + cellSize, cy + cellSize),
                size = Size(5 * cellSize, 5 * cellSize),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Center solid 3x3 square
            drawRoundRect(
                color = darkColor,
                topLeft = Offset(cx + 2 * cellSize, cy + 2 * cellSize),
                size = Size(3 * cellSize, 3 * cellSize),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
        }

        drawFinder(0f, 0f) // Top-Left
        drawFinder((cellCount - 7) * cellSize, 0f) // Top-Right
        drawFinder(0f, (cellCount - 7) * cellSize) // Bottom-Left

        // Timing patterns
        for (i in 8 until cellCount - 8 step 2) {
            drawRect(
                color = darkColor,
                topLeft = Offset(i * cellSize, 6 * cellSize),
                size = Size(cellSize, cellSize)
            )
            drawRect(
                color = darkColor,
                topLeft = Offset(6 * cellSize, i * cellSize),
                size = Size(cellSize, cellSize)
            )
        }

        // Procedural deterministic data cells
        for (r in 0 until cellCount) {
            for (c in 0 until cellCount) {
                val isFinderArea = (r < 8 && c < 8) || (r < 8 && c >= cellCount - 8) || (r >= cellCount - 8 && c < 8)
                val isCenterLogo = r in 8..12 && c in 8..12
                if (!isFinderArea && !isCenterLogo) {
                    val hash = (r * 31 + c * 17 + tableNumber * 13) % 7
                    if (hash in listOf(1, 3, 5)) {
                        drawRoundRect(
                            color = darkColor,
                            topLeft = Offset(c * cellSize + 1f, r * cellSize + 1f),
                            size = Size(cellSize - 2f, cellSize - 2f),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
