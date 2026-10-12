package com.sktech.storevisionpro.ui.marketing

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.marketing.PosterBitmapExporter
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMarketingScreen(
    uiState: StoreUiState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTemplate by remember { mutableStateOf("Today's Special Offer") }
    var selectedProductForPoster by remember { mutableStateOf(uiState.products.firstOrNull()?.name ?: "Amul Gold Milk 500ml") }
    var discountText by remember { mutableStateOf("Buy 2 Get 1 Free") }
    var instagramHandle by remember { mutableStateOf("@apex_supermart") }
    var showBrandKitDialog by remember { mutableStateOf(false) }
    var isVoiceRecording by remember { mutableStateOf(false) }
    var voiceScriptTextEn by remember {
        mutableStateOf("Fresh dairy arrived at Apex SuperMart! Buy 2 Amul Gold Milk packets today and grab Britannia Good Day at 50% discount. Limited stocks available at Main Market branch. Visit today!")
    }
    var voiceScriptTextHi by remember {
        mutableStateOf("अपेक्स सुपरमार्ट पर आज का धमाकेदार ऑफर! अमूल गोल्ड दूध के 2 पैकेट पर ब्रिटानिया गुड डे बिस्किट 50% छूट पर पाएं। सीमित स्टॉक, आज ही पधारें!")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column {
                    Text(
                        text = "AI Marketing & Creative Studio",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Persistent Shop Branding, Canvas Posters & Social Copy",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // Section 1: Shop Branding Kit Persistent Identity Card (Screen 17)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldMuted)
                                    .border(1.dp, ElectricEmerald, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = ElectricEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = uiState.currentShopName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OffWhitePearl
                                    )
                                )
                                Text(
                                    text = "Approved Logo & Calibrated Brand Palette",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                                )
                            }
                        }

                        Button(
                            onClick = { showBrandKitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ZincMetal),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Brand",
                                tint = ElectricEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Brand Kit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = OffWhitePearl
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Brand Palette:",
                            style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                        )
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(ElectricEmerald))
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(SlateDark))
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(OffWhitePearl))
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(PrecisionAzure))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Instagram: $instagramHandle",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = DimGhost
                            )
                        )
                    }
                }
            }
        }

        // Section 2: Poster Canvas Template Selector
        item {
            Text(
                text = "Select Poster Template (Offline Canvas)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OffWhitePearl
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Today's Special Offer", "Weekend Flash Sale", "Festival Wishes", "Clearance Discount", "Fresh Arrivals")) { tpl ->
                    val selected = selectedTemplate == tpl
                    Surface(
                        color = if (selected) EmeraldMuted else ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selected) ElectricEmerald else SlateDivider
                        ),
                        modifier = Modifier.clickable { selectedTemplate = tpl }
                    ) {
                        Text(
                            text = tpl,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) ElectricEmerald else OffWhitePearl
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Section 3: Live Customization Controls (Product & Badge Text)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Customize Poster Content",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )

                    // Product Selector Chips
                    Text(
                        text = "Featured Product:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(uiState.products) { prod ->
                            val isSelected = selectedProductForPoster == prod.name
                            Surface(
                                color = if (isSelected) ZincMetal else SlateDark,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ElectricEmerald else SlateDivider
                                ),
                                modifier = Modifier.clickable { selectedProductForPoster = prod.name }
                            ) {
                                Text(
                                    text = prod.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) ElectricEmerald else OffWhitePearl,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Promotion Ribbon Input
                    OutlinedTextField(
                        value = discountText,
                        onValueChange = { discountText = it },
                        label = { Text("Promotion Badge Text", color = MutedSilver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = SlateDivider,
                            focusedTextColor = OffWhitePearl,
                            unfocusedTextColor = OffWhitePearl
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Section 4: Live Poster Card Simulation (Screen 18)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricEmerald),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Simulated Brand Header
                    Text(
                        text = uiState.currentShopName.uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = OffWhitePearl
                        )
                    )
                    Text(
                        text = "★  ${selectedTemplate.uppercase()}  ★",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrecisionAzure,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated Promotion Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricEmerald)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = discountText.uppercase(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = SlateDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedProductForPoster,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )

                    Text(
                        text = "Valid Until Stocks Last • In-Store Exclusive",
                        style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Share Actions with real Canvas Bitmap Exporter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val posterFile = PosterBitmapExporter.generatePosterBitmap(
                                    context = context,
                                    shopName = uiState.currentShopName,
                                    promotionBadge = discountText,
                                    productName = selectedProductForPoster,
                                    templateName = selectedTemplate,
                                    instagramHandle = instagramHandle
                                )
                                if (posterFile != null) {
                                    Toast.makeText(context, "Exporting Poster to WhatsApp...", Toast.LENGTH_SHORT).show()
                                    PosterBitmapExporter.sharePosterImage(context, posterFile, "com.whatsapp")
                                } else {
                                    Toast.makeText(context, "Failed to render poster", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = SlateDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Share WhatsApp",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val posterFile = PosterBitmapExporter.generatePosterBitmap(
                                    context = context,
                                    shopName = uiState.currentShopName,
                                    promotionBadge = discountText,
                                    productName = selectedProductForPoster,
                                    templateName = selectedTemplate,
                                    instagramHandle = instagramHandle
                                )
                                if (posterFile != null) {
                                    Toast.makeText(context, "Exporting Poster to Instagram...", Toast.LENGTH_SHORT).show()
                                    PosterBitmapExporter.sharePosterImage(context, posterFile, "com.instagram.android")
                                } else {
                                    Toast.makeText(context, "Failed to render poster", Toast.LENGTH_SHORT).show()
                                }
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = OffWhitePearl,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Instagram Story", style = MaterialTheme.typography.labelSmall.copy(color = OffWhitePearl))
                        }
                    }
                }
            }
        }

        // Section 5: Voice-to-Ad Script Generator & Multilingual Copywriter
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Voice-to-Ad Script Generator",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OffWhitePearl
                                )
                            )
                            Text(
                                text = "Dictate promo ideas or generate bilingual audio scripts",
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                            )
                        }

                        IconButton(
                            onClick = {
                                isVoiceRecording = !isVoiceRecording
                                if (isVoiceRecording) {
                                    Toast.makeText(context, "Simulating audio dictation...", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isVoiceRecording) SignalCrimson else ZincMetal)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Dictate",
                                tint = if (isVoiceRecording) OffWhitePearl else ElectricEmerald
                            )
                        }
                    }

                    if (isVoiceRecording) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateDark)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(12, 24, 38, 16, 42, 28, 50, 20, 34, 18, 44, 26, 14).forEach { heightDp ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(heightDp.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(ElectricEmerald)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // English Script Card
                    Surface(
                        color = ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "English Broadcast Copy",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElectricEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "30s Audio Script",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DimGhost,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = voiceScriptTextEn,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = OffWhitePearl,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hindi Script Card
                    Surface(
                        color = ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Hindi Broadcast Copy (हिंदी ऑडियो स्क्रिप्ट)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PrecisionAzure,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "30s Audio Script",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DimGhost,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = voiceScriptTextHi,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = OffWhitePearl,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Shop Logo & Brand Kit Customization Dialog (Screen 17)
    if (showBrandKitDialog) {
        AlertDialog(
            onDismissRequest = { showBrandKitDialog = false },
            title = {
                Text(
                    text = "Brand Identity & Logo Kit",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhitePearl
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Configure persistent branding elements embedded across customer invoices, QR menus, and promotional posters.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedSilver)
                    )

                    OutlinedTextField(
                        value = instagramHandle,
                        onValueChange = { instagramHandle = it },
                        label = { Text("Instagram Handle", color = MutedSilver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricEmerald,
                            unfocusedBorderColor = SlateDivider,
                            focusedTextColor = OffWhitePearl,
                            unfocusedTextColor = OffWhitePearl
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Primary Accent Color: Electric Emerald (#10B981)",
                        style = MaterialTheme.typography.labelSmall.copy(color = ElectricEmerald)
                    )
                    Text(
                        text = "Deterministic Edge AI Font: Inter + Monospace Tabular",
                        style = MaterialTheme.typography.labelSmall.copy(color = DimGhost)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBrandKitDialog = false
                        Toast.makeText(context, "Brand Kit Updated & Locked", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald)
                ) {
                    Text("Save & Apply", color = SlateDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBrandKitDialog = false }) {
                    Text("Cancel", color = MutedSilver)
                }
            },
            containerColor = ZincCharcoal,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF09090B,
    name = "AI Marketing Creative Studio Preview"
)
@Composable
fun AiMarketingScreenPreview() {
    StoreVisionProTheme(darkTheme = true) {
        AiMarketingScreen(
            uiState = StoreUiState(),
            onNavigateBack = {}
        )
    }
}
