package com.sktech.storevisionpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sktech.storevisionpro.ui.auth.AuthScreen
import com.sktech.storevisionpro.ui.chat.AiChatbotSheet
import com.sktech.storevisionpro.ui.components.StoreVisionScaffold
import com.sktech.storevisionpro.ui.dashboard.HomeDashboardScreen
import com.sktech.storevisionpro.ui.insights.AiInsightsScreen
import com.sktech.storevisionpro.ui.insights.BusinessHealthCheckScreen
import com.sktech.storevisionpro.ui.language.LanguageSelectorScreen
import com.sktech.storevisionpro.ui.marketing.AiMarketingScreen
import com.sktech.storevisionpro.ui.navigation.Screen
import com.sktech.storevisionpro.ui.pos.SalesEntryScreen
import com.sktech.storevisionpro.ui.products.ProductManagementScreen
import com.sktech.storevisionpro.ui.state.StoreViewModel
import com.sktech.storevisionpro.ui.theme.StoreVisionProTheme
import com.sktech.storevisionpro.ui.tools.ExtraToolsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StoreViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            StoreVisionProTheme(darkTheme = uiState.isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.SalesOperations.route

                // Full app scaffold with top bar, bottom bar, and floating AI assistant
                StoreVisionScaffold(
                    uiState = uiState,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.SalesOperations.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenChat = { viewModel.setChatSheetOpen(true) },
                    onOpenLanguage = { navController.navigate(Screen.Language.route) },
                    onOpenTools = { navController.navigate(Screen.ExtraTools.route) },
                    onToggleTheme = { viewModel.toggleTheme() }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SalesOperations.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Tab 1: Operations (Home Dashboard)
                        composable(Screen.SalesOperations.route) {
                            HomeDashboardScreen(
                                uiState = uiState,
                                onNavigateToSalesEntry = { navController.navigate(Screen.SalesEntry.route) },
                                onNavigateToProducts = { navController.navigate(Screen.ProductManagement.route) },
                                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                                onNavigateToInsights = { navController.navigate(Screen.AiInsights.route) },
                                onOpenVoiceAssistant = { viewModel.setChatSheetOpen(true) },
                                onApplyClearanceBundle = { nearId, pairId, disc ->
                                    viewModel.applyClearanceBundle(nearId, pairId, disc)
                                }
                            )
                        }

                        // Tab 2: Analytics Dashboard
                        composable(Screen.Analytics.route) {
                            com.sktech.storevisionpro.ui.analytics.AnalyticsDashboardScreen(
                                uiState = uiState,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Tab 3: AI Business Insights
                        composable(Screen.AiInsights.route) {
                            AiInsightsScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateToHealthCheck = { navController.navigate(Screen.BusinessHealth.route) }
                            )
                        }

                        // Tab 4: AI Marketing Creative Studio
                        composable(Screen.Marketing.route) {
                            AiMarketingScreen(
                                uiState = uiState,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Sub-Screen: POS Checkout
                        composable(Screen.SalesEntry.route) {
                            SalesEntryScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Sub-Screen: Product Catalog & Inventory
                        composable(Screen.ProductManagement.route) {
                            ProductManagementScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Sub-Screen: Business Health Check
                        composable(Screen.BusinessHealth.route) {
                            BusinessHealthCheckScreen(
                                uiState = uiState,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Sub-Screen: Language Selector
                        composable(Screen.Language.route) {
                            LanguageSelectorScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Sub-Screen: Extra Tools & Admin Settings
                        composable(Screen.ExtraTools.route) {
                            ExtraToolsScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // Sub-Screen: Auth
                        composable(Screen.Auth.route) {
                            AuthScreen(
                                onLoginSuccess = { navController.navigate(Screen.SalesOperations.route) }
                            )
                        }
                    }
                }

                // Global Persistent AI Assistant Bottom Sheet
                if (uiState.isChatSheetOpen) {
                    AiChatbotSheet(
                        messages = uiState.chatMessages,
                        onSendMessage = { text -> viewModel.sendUserChatMessage(text) },
                        onConfirmAction = { confirmation -> viewModel.confirmVoiceLogAction(confirmation) },
                        onRejectAction = { confirmation -> viewModel.rejectVoiceLogAction(confirmation) },
                        onDismiss = { viewModel.setChatSheetOpen(false) }
                    )
                }
            }
        }
    }
}