package com.sktech.storevisionpro.ui.language

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.domain.model.AppLanguage
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.state.StoreViewModel
import com.sktech.storevisionpro.ui.theme.*

@Composable
fun LanguageSelectorScreen(
    uiState: StoreUiState,
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedLang by remember { mutableStateOf(uiState.currentLanguage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OffWhitePearl
                )
            }
            Column {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhitePearl
                    )
                )
                Text(
                    text = "भाषा निवडा • भाषा चुनें • Choose UI Language",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MutedSilver)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(AppLanguage.values()) { lang ->
                val isSelected = selectedLang == lang
                Surface(
                    color = if (isSelected) EmeraldMuted else ZincCharcoal,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) ElectricEmerald else SlateDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLang = lang }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ElectricEmerald else OffWhitePearl,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = lang.englishName,
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.setLanguage(selectedLang)
                onNavigateBack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Apply & Save Language Preference",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OffWhitePearl
                )
            )
        }
    }
}
