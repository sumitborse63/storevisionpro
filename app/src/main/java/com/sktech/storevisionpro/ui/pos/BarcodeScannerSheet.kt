package com.sktech.storevisionpro.ui.pos

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerSheet(
    onBarcodeScanned: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var manualBarcode by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ZincCharcoal,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = SlateDivider)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Camera Barcode Scanner",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MutedSilver
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Targeting Viewport Reticle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0F12))
                    .border(2.dp, SlateDivider, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Outer corners targeting frame
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .border(1.5.dp, ElectricEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                )

                // Animated Laser Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .offset { androidx.compose.ui.unit.IntOffset(0, (laserOffsetY - 90f).dp.roundToPx()) }
                        .height(2.dp)
                        .background(ElectricEmerald)
                )

                Text(
                    text = "Align barcode within frame",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedSilver,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Hardware Simulation Chips (Supports emulators and rapid testing)
            Text(
                text = "Rapid Test Scans (One-Tap Hardware Simulation):",
                style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val simulationBarcodes = listOf(
                "Amul Milk" to "8901262010053",
                "Good Day" to "8901063012117",
                "Tata Salt" to "8901058852331",
                "Fortune Oil" to "8906007281023",
                "Atta 5kg" to "8901030381028"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(simulationBarcodes) { (name, code) ->
                    Surface(
                        color = ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                        modifier = Modifier.clickable {
                            onBarcodeScanned(code)
                            onDismiss()
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OffWhitePearl
                                )
                            )
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = ElectricEmerald,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Manual Barcode Entry Fallback
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = manualBarcode,
                    onValueChange = { manualBarcode = it },
                    placeholder = { Text("Or enter barcode digits...", color = DimGhost) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OffWhitePearl,
                        unfocusedTextColor = OffWhitePearl,
                        focusedBorderColor = ElectricEmerald,
                        unfocusedBorderColor = SlateDivider
                    ),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (manualBarcode.isNotBlank()) {
                            onBarcodeScanned(manualBarcode.trim())
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Lookup", color = OffWhitePearl, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
