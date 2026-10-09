package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArMenuDish
import com.example.model.WebArCodeArtifact
import com.example.ui.components.EmbeddedWebArModelViewer
import com.example.ui.theme.BistroCardBorder
import com.example.ui.theme.BistroElevated
import com.example.ui.theme.BistroSurface
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SpatialEmerald

/**
 * Full-screen Live WebAR Sandbox running `assets/webar/index.html`, `styles.css`, `app.js`,
 * and `<model-viewer>` with our bundled binary `.glb` 3D dish models.
 */
@Composable
fun LiveWebArSandboxScreen(
    selectedDish: ArMenuDish,
    onLaunchNativeSceneViewer: (ArMenuDish) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Top Telemetry Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BistroSurface,
            border = BorderStroke(1.dp, BistroCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SELF-HOSTED WEBAR ENGINE • https://ar-bistro.local/",
                        style = MaterialTheme.typography.labelSmall,
                        color = SpatialEmerald
                    )
                    Text(
                        text = "HTML5 + CSS3 + Vanilla JS + <model-viewer> (.glb / .usdz)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = SaffronGold.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, SaffronGold)
                ) {
                    Text(
                        text = "5 .GLB Loaded",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Full-Screen Embedded WebAR Runtime
        EmbeddedWebArModelViewer(
            dishId = null,
            onLaunchNativeAr = { onLaunchNativeSceneViewer(selectedDish) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

/**
 * Developer Architecture & Code Studio allowing users to inspect and copy the complete
 * production-ready `index.html`, `styles.css`, `app.js`, and implementation guide.
 */
@Composable
fun WebArCodeStudioScreen(
    artifacts: List<WebArCodeArtifact>,
    selectedArtifactId: String,
    onSelectArtifact: (String) -> Unit,
    onLaunchLiveSandbox: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeArtifact = artifacts.find { it.id == selectedArtifactId } ?: artifacts.first()
    var copiedFileName by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header & Copy / Live Preview Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BistroSurface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WEBAR SOLUTIONS ARCHITECT STUDIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGold
                        )
                        Text(
                            text = "Self-Hosted <model-viewer> Source Bundle",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onLaunchLiveSandbox,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronGold,
                            contentColor = ObsidianBg
                        ),
                        modifier = Modifier.testTag("studio_run_webar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Run Live",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // File Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    artifacts.forEach { artifact ->
                        val selected = artifact.id == activeArtifact.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectArtifact(artifact.id) }
                                .testTag("code_tab_${artifact.id}"),
                            color = if (selected) SaffronGold else BistroElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (selected) SaffronGold else BistroCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = artifact.fileName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) ObsidianBg else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = artifact.badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) ObsidianBg.copy(alpha = 0.78f) else SaffronGold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active File Description & Copy Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activeArtifact.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(
                            ClipData.newPlainText(activeArtifact.fileName, activeArtifact.codeContent)
                        )
                        copiedFileName = activeArtifact.fileName
                    }
                    .testTag("copy_code_button"),
                color = BistroElevated,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (copiedFileName == activeArtifact.fileName) Icons.Default.Verified else Icons.Default.ContentCopy,
                        contentDescription = "Copy file content",
                        tint = if (copiedFileName == activeArtifact.fileName) SpatialEmerald else SaffronGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (copiedFileName == activeArtifact.fileName) "Copied!" else "Copy ${activeArtifact.fileName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Code Viewer Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color(0xFF0E0C0A),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BistroCardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = activeArtifact.codeContent,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
