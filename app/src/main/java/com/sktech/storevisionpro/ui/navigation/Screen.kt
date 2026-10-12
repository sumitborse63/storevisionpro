package com.sktech.storevisionpro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Auth & Onboarding
    data object Auth : Screen("auth")
    data object Language : Screen("language")

    // Main 4 Bottom Tabs
    data object SalesOperations : Screen("sales_operations")
    data object Analytics : Screen("analytics")
    data object AiInsights : Screen("ai_insights")
    data object Marketing : Screen("marketing")

    // Operational Sub-Screens
    data object SalesEntry : Screen("sales_entry")
    data object ProductManagement : Screen("product_management")
    data object CalendarView : Screen("calendar_view")
    data object BusinessHealth : Screen("business_health")
    data object BrandingKit : Screen("branding_kit")
    data object ExtraTools : Screen("extra_tools")
    data object SyncStatus : Screen("sync_status")
    data object TrendingProducts : Screen("trending_products")
    data object PriceComparison : Screen("price_comparison")
    data object ExpiryAlerts : Screen("expiry_alerts")
    data object HospitalityMenu : Screen("hospitality_menu")
    data object CustomerQrMenu : Screen("customer_qr_menu")
}

data class BottomNavTab(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val BottomNavTabs = listOf(
    BottomNavTab(
        route = Screen.SalesOperations.route,
        title = "Operations",
        icon = Icons.AutoMirrored.Filled.ReceiptLong
    ),
    BottomNavTab(
        route = Screen.Analytics.route,
        title = "Analytics",
        icon = Icons.Default.AutoGraph
    ),
    BottomNavTab(
        route = Screen.AiInsights.route,
        title = "AI Insights",
        icon = Icons.Default.Psychology
    ),
    BottomNavTab(
        route = Screen.Marketing.route,
        title = "Marketing",
        icon = Icons.Default.Campaign
    )
)
