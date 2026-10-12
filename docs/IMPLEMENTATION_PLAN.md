# StoreVisionPro — Production-Line Implementation Plan

## 1. Production Pipeline & Wave Parallelization Architecture

This implementation plan organizes the development of StoreVisionPro into sequential, dependency-aware **Engineering Waves**. Each wave delivers production-tested, fully functional vertical slices that satisfy the requirements detailed in the SRS and System Architecture documents.

```
[ WAVE 1: Core Engine Foundation ]
  Phase 0: Build Architecture & Dependency Matrix
  Phase 1: Shell Navigation, Theme Tokens & Multi-Auth Security
        │
        ▼
[ WAVE 2: Offline-First Persistence & POS Operations ]
  Phase 2: Room SQLite SSOT & WorkManager Sync Engine
  Phase 3: Module 1 - POS Sales, CameraX Barcode, Batches & Inventory
        │
        ▼
[ WAVE 3: Analytics & Intelligence Baseline ]
  Phase 4: Module 2 - Analytics Engine, Heatmaps & PDF Exporter
  Phase 5: Module 3 - V1 Rule-Based Heuristic Insights & Health Check
        │
        ▼
[ WAVE 4: Conversational Agent & Generative Marketing ]
  Phase 6: Module 4 - AI Chatbot & Voice Log with HITL Confirmation
  Phase 7: Module 5 - Branding Kit & Canvas Poster Marketing Engine
        │
        ▼
[ WAVE 5: Enterprise Tools, Hospitality & Release Hardening ]
  Phase 8: Module 6 - GST Invoicing, Loyalty, UPI Cashbook & QR Menu
  Phase 9: End-to-End Stress Testing, Security Audit & Store Release
```

---

## 2. Phased Implementation Roadmap

---

### Phase 0: Build Architecture & Dependency Matrix
- **Goal:** Configure Gradle dependencies, version catalog (`libs.versions.toml`), and compilation flags required for Room, Hilt, Firebase, CameraX, and Chart rendering.
- **Duration:** 1–2 Days.
- **File Manifest:**
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `build.gradle.kts`
- **Tasks:**
  1. Add KSP (Kotlin Symbol Processing) and Hilt plugins to `build.gradle.kts`.
  2. In `gradle/libs.versions.toml`, declare versions and coordinates for:
     - Room (`androidx.room:room-runtime`, `androidx.room:room-ktx`, `androidx.room:room-compiler`).
     - Hilt (`com.google.dagger:hilt-android`, `com.google.dagger:hilt-compiler`, `androidx.hilt:hilt-navigation-compose`).
     - Firebase BoM (`com.google.firebase:firebase-bom:33.7.0`, `firebase-auth-ktx`, `firebase-firestore-ktx`, `firebase-storage-ktx`).
     - CameraX (`androidx.camera:camera-camera2`, `androidx.camera:camera-lifecycle`, `androidx.camera:camera-view`).
     - ML Kit Barcode Scanning (`com.google.mlkit:barcode-scanning:17.3.0`).
     - MPAndroidChart (`com.github.PhilJay:MPAndroidChart:v3.1.0`).
     - WorkManager (`androidx.work:work-runtime-ktx:2.10.0`).
     - Biometric (`androidx.biometric:biometric:1.2.0-alpha05`).
     - Retrofit + OkHttp (`com.squareup.retrofit2:retrofit`, `converter-gson`, `logging-interceptor`).
  3. Configure ProGuard keep rules for Room entities and Firebase models.
- **Verification Gate:** Run `./gradlew assembleDebug` to confirm error-free Gradle dependency resolution.

---

### Phase 1: Shell Navigation, Theme Tokens & Multi-Auth Security
- **Goal:** Establish the app container with 4-tab bottom navigation, floating AI Assistant, `DESIGN.md` theme tokens, and Firebase Multi-Auth.
- **Duration:** 3–5 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.ui.theme.Theme.kt`
  - `com.sktech.storevisionpro.ui.theme.Color.kt`
  - `com.sktech.storevisionpro.ui.theme.Type.kt`
  - `com.sktech.storevisionpro.ui.navigation.NavGraph.kt`
  - `com.sktech.storevisionpro.ui.navigation.Screen.kt`
  - `com.sktech.storevisionpro.ui.components.StoreVisionScaffold.kt`
  - `com.sktech.storevisionpro.ui.auth.AuthScreen.kt`
  - `com.sktech.storevisionpro.ui.auth.AuthViewModel.kt`
  - `com.sktech.storevisionpro.security.BiometricAuthManager.kt`
  - `com.sktech.storevisionpro.i18n.LanguageManager.kt`
- **Tasks:**
  1. **Color & Typography Tokens:** Implement `Slate Dark` (`#09090B`), `Zinc Charcoal` (`#18181B`), `Electric Emerald` (`#10B981`), `Warning Amber` (`#F59E0B`), `Signal Crimson` (`#EF4444`) in Compose theme. Set up `Cabinet Grotesk` display and `JetBrains Mono` tabular styles.
  2. **Navigation Graph:** Construct Compose `NavHost` connecting Screen 1 (Auth), Screen 2 (Language), Screen 3 (Dashboard), Screen 4 (Products), Screen 5 (Sales Entry), Screen 7 (Analytics), Screen 8 (AI Insights), Screen 11 (Marketing), Screen 12 (Extra Tools).
  3. **Multi-Factor Auth:** Implement Firebase Email/Password, Google Sign-In with Credential Manager, and Phone OTP verification. Bind AndroidX `BiometricPrompt` for local biometric unlock.
  4. **Multilingual Architecture:** Configure Android resource strings (`values-hi`, `values-mr`, etc.) and create `LanguageManager` to persist user language selection across restarts.
- **Verification Gate:** Validate successful login, biometric unlock prompt, language switching, and bottom-tab navigation across all 4 sections on an emulator.

---

### Phase 2: Offline-First Data Layer & Sync Engine
- **Goal:** Implement the local Room database, DAOs, and the WorkManager background sync engine for bidirectional Firestore synchronization.
- **Duration:** 5–7 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.data.local.StoreVisionDatabase.kt`
  - `com.sktech.storevisionpro.data.local.entity.*` (18 Entities)
  - `com.sktech.storevisionpro.data.local.dao.*` (ProductDao, SaleDao, CashbookDao, SyncQueueDao)
  - `com.sktech.storevisionpro.data.sync.SyncWorker.kt`
  - `com.sktech.storevisionpro.data.sync.SyncManager.kt`
  - `com.sktech.storevisionpro.data.sync.ConflictResolver.kt`
  - `com.sktech.storevisionpro.data.remote.FirestoreRepository.kt`
- **Tasks:**
  1. **Room Entities & DAOs:** Code the full set of entities and DAOs specified in `DATA_SCHEMA_AND_CONTRACTS.md`.
  2. **Database Migration Policy:** Configure Room database builder with fallback-to-destructive-migration during debug, and strict auto-migrations for production.
  3. **SyncQueue Processor:** Implement `SyncWorker` extending `CoroutineWorker`. Read pending rows from `sync_queue`, batch write to Cloud Firestore via `WriteBatch`, and remove completed records upon server acknowledgment.
  4. **Network Monitoring:** Implement `ConnectivityObserver` utilizing Android `ConnectivityManager.NetworkCallback` to trigger sync instantly when internet returns.
- **Verification Gate:** Insert 50 products and 10 sales while device Wi-Fi is disabled; enable Wi-Fi and verify automatic batch replication in Cloud Firestore console without duplicates.

---

### Phase 3: Module 1 - Shop Operations, Barcode & POS Engine
- **Goal:** Deliver full Point-of-Sale (POS) checkout, CameraX barcode scanning, real-time inventory management, expiry/batch tracking, and supplier records.
- **Duration:** 7–10 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.ui.pos.SalesEntryScreen.kt`
  - `com.sktech.storevisionpro.ui.pos.POSViewModel.kt`
  - `com.sktech.storevisionpro.ui.pos.BarcodeScannerDialog.kt`
  - `com.sktech.storevisionpro.ui.products.ProductListScreen.kt`
  - `com.sktech.storevisionpro.ui.products.ProductDetailDialog.kt`
  - `com.sktech.storevisionpro.ui.calendar.CalendarSalesScreen.kt`
  - `com.sktech.storevisionpro.domain.usecase.ProcessSaleTransactionUseCase.kt`
  - `com.sktech.storevisionpro.camera.CameraXBarcodeAnalyzer.kt`
  - `com.sktech.storevisionpro.audit.AuditLogger.kt`
- **Tasks:**
  1. **Barcode Scanning:** Wire CameraX preview with ML Kit `BarcodeScanning.getClient()`. Support fast continuous scanning with audio beep/haptic feedback.
  2. **Product Management:** Build Screen 4 with category filters, search bar, stock level color indicators, and batch/expiry modal.
  3. **Sales Checkout Engine:** Build Screen 5 with editable date/time, customer counter stepper, "Visited, No Purchase" toggle, item table, and subtotal calculation.
  4. **FEFO Inventory Decrement:** In `ProcessSaleTransactionUseCase`, deduct item quantities from the earliest expiring active batch (`ProductBatchEntity`), record `SaleEntity` + `SaleItemEntity`, write to `CashbookEntryEntity`, and commit inside a Room `@Transaction`.
  5. **Calendar View:** Build Screen 6 with month grid rendering daily revenue totals and transaction inspection lists.
- **Verification Gate:** Execute a complete 5-item sale via barcode scanner; verify synchronous inventory decrement, cashbook entry creation, and invoice sequence generation.

---

### Phase 4: Module 2 - Analytics Dashboard, Heatmaps & PDF Engine
- **Goal:** Build the business intelligence suite: animated charts, multi-dimensional footfall heatmaps, dead-stock detection, and native PDF report export.
- **Duration:** 6–8 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.ui.analytics.AnalyticsDashboardScreen.kt`
  - `com.sktech.storevisionpro.ui.analytics.AnalyticsViewModel.kt`
  - `com.sktech.storevisionpro.ui.analytics.components.FootfallHeatmapCanvas.kt`
  - `com.sktech.storevisionpro.ui.analytics.components.IncomeTrendChart.kt`
  - `com.sktech.storevisionpro.ui.analytics.components.CategoryPieChart.kt`
  - `com.sktech.storevisionpro.domain.analytics.FinancialMetricsCalculator.kt`
  - `com.sktech.storevisionpro.domain.analytics.DeadStockDetector.kt`
  - `com.sktech.storevisionpro.pdf.PdfReportGenerator.kt`
- **Tasks:**
  1. **Aggregation Pipeline:** Create SQL queries aggregating income, expenses, and customer count by hour, day of week, month, and year.
  2. **Custom Compose Heatmap:** Build `FootfallHeatmapCanvas` rendering a 24-hour x 7-day footfall density matrix with calibrated emerald intensity shades.
  3. **Leaderboard & Dead Stock:** Rank Top-10 Best-Sellers and identify products with 0 sales in the last $D$ days (default: 14 days).
  4. **Native PDF Exporter:** Implement `PdfReportGenerator` using `android.graphics.pdf.PdfDocument`. Render store logo, header, KPI summary table, itemized performance, and signature line.
- **Verification Gate:** Generate and open a 30-day PDF report on device storage; verify visual fidelity, mathematical accuracy against database records, and print readiness.

---

### Phase 5: Module 3 - AI Business Insights (Offline Heuristics & Cloud Gateway)
- **Goal:** Deliver the two-tier intelligence engine: V1 offline rule-based heuristics and V2 cloud dynamic pricing, reorder predictions, and Business Health Check.
- **Duration:** 8–10 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.domain.insights.OfflineRuleEngine.kt`
  - `com.sktech.storevisionpro.domain.insights.BusinessHealthCheckEngine.kt`
  - `com.sktech.storevisionpro.data.remote.AiGatewayService.kt`
  - `com.sktech.storevisionpro.ui.insights.AiInsightsScreen.kt`
  - `com.sktech.storevisionpro.ui.insights.BusinessHealthCheckScreen.kt`
  - `com.sktech.storevisionpro.ui.insights.components.SuggestionCard.kt`
  - `com.sktech.storevisionpro.ui.insights.components.HealthGauge.kt`
  - `com.sktech.storevisionpro.governance.ApprovalGateInterceptor.kt`
- **Tasks:**
  1. **V1 Offline Rule Engine:** Implement local Kotlin heuristic evaluators:
     - Detect items with sales velocity $> 2\times$ average and trigger restock reminders.
     - Scan batches approaching expiry ($< 7$ days) and suggest Buy-1-Get-1 bundle offers.
     - Identify quiet hours from historical footfall and suggest Happy-Hour discounts.
  2. **Human-in-the-Loop Approval Gate:** Build `SuggestionCard` with explicit `[ Accept ]`, `[ Edit ]`, and `[ Reject ]` actions. Block all direct database modifications until approved.
  3. **Business Health Check:** Implement radial health score gauge and multi-factor diagnostic report (margin, customer retention, dead stock capital).
  4. **Provenance & Source Transparency:** Display source labels (`Rule-Based Heuristic` vs `Verified Market Feed`) and block fabricated competitor prices.
- **Verification Gate:** Trigger a simulated near-expiry rule; accept the suggestion via UI and verify that only the user-confirmed discount is applied to the catalog with an audit record created.

---

### Phase 6: Module 4 & 5 - Conversational Assistant, Voice Log & Marketing Creator
- **Goal:** Build the voice log transaction parser, chatbot assistant with strict confirmation cards, guided branding kit onboarding, and offline Canvas poster creator.
- **Duration:** 10–14 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.ui.chat.AiChatbotSheet.kt`
  - `com.sktech.storevisionpro.ui.chat.ChatViewModel.kt`
  - `com.sktech.storevisionpro.ui.marketing.MarketingStudioScreen.kt`
  - `com.sktech.storevisionpro.ui.marketing.BrandingKitOnboardingScreen.kt`
  - `com.sktech.storevisionpro.ui.marketing.PosterCanvasPreviewScreen.kt`
  - `com.sktech.storevisionpro.voice.VoiceLogRecorder.kt`
  - `com.sktech.storevisionpro.marketing.PosterCanvasRenderer.kt`
  - `com.sktech.storevisionpro.marketing.PaletteExtractor.kt`
- **Tasks:**
  1. **Voice Log Recording & Parsing:** Capture audio via Android SpeechRecognizer or stream to Whisper API; parse intent into structured JSON (`product`, `qty`, `cost`, `supplier`).
  2. **Chatbot Confirmation Card:** Embed interactive approval cards in the chat stream. Prevent auto-saving of voice-logged stock receipts until the owner taps "Confirm & Log".
  3. **Branding Kit Onboarding:** Build Screen 17 wizard (upload existing logo or guided preferences questionnaire; extract palette tokens; store in `brandingProfiles`).
  4. **Canvas Poster Renderer:** Implement `PosterCanvasRenderer` compositing background templates (Today's Offer, Weekend Sale, Diwali wishes), product photo, discount banner, shop logo, and WhatsApp/Instagram contact details directly on an Android `Bitmap`.
  5. **Social Sharing:** Integrate Android Intent (`ACTION_SEND`) to share generated posters and captions directly to WhatsApp and Instagram.
- **Verification Gate:** Speak "Received 20 packets of sugar for 800 rupees"; verify parsed confirmation card in chat, confirm it, and verify inventory and cashbook update.

---

### Phase 7: Module 6 - Business Management Tools & Hospitality Extension
- **Goal:** Implement GST invoicing, customer loyalty QR, cashbook & UPI reconciliation, multi-shop switcher, and hospitality QR menu ordering.
- **Duration:** 8–10 Days.
- **File Manifest:**
  - `com.sktech.storevisionpro.ui.tools.ExtraToolsScreen.kt`
  - `com.sktech.storevisionpro.ui.invoicing.GstInvoiceGenerator.kt`
  - `com.sktech.storevisionpro.ui.loyalty.LoyaltyManagerScreen.kt`
  - `com.sktech.storevisionpro.ui.cashbook.CashbookLedgerScreen.kt`
  - `com.sktech.storevisionpro.ui.multishop.ShopSwitcherDialog.kt`
  - `com.sktech.storevisionpro.ui.hospitality.HospitalityAdminScreen.kt`
  - `com.sktech.storevisionpro.ui.hospitality.CustomerQrMenuScreen.kt`
- **Tasks:**
  1. **GST Invoicing:** Calculate CGST, SGST, IGST breakdown; generate PDF bill; save record in `invoices` collection.
  2. **Loyalty Program:** Generate unique customer QR codes; award points on sales; provide coupon discount redemption during sales checkout.
  3. **Cashbook & UPI Reconciliation:** Track Cash-In, Cash-Out, and UPI settlements. Auto-generate day-end closing balance sheet.
  4. **Multi-Shop Switcher:** Allow multi-store owners to switch active `shopId` with immediate reactive filtering of all dashboard stats, stock, and staff.
  5. **Hospitality QR-Menu:** If `shopType` is cafe/restaurant, enable digital menu manager, table QR generator, and real-time order board (Received -> Preparing -> Ready).
- **Verification Gate:** Switch between Shop A and Shop B; verify complete data isolation. Generate a GST invoice and verify tax calculations.

---

### Phase 8: Production Verification, Security Audit & Launch Hardening
- **Goal:** Execute comprehensive unit, integration, and UI instrumentation tests; perform memory profiling; verify offline sync under network degradation; prepare release APK/AAB.
- **Duration:** 5–7 Days.
- **Tasks:**
  1. **Unit Testing:** Achieve $> 85\%$ test coverage across all UseCases (`ProcessSaleTransactionUseCaseTest`, `OfflineRuleEngineTest`, `SyncWorkerTest`).
  2. **Offline Resilience Stress Test:** Execute 500 sales transactions while cycling network connectivity on/off; verify zero lost transactions, no memory leaks, and perfect Firestore consistency.
  3. **Security Audit:** Verify Firestore Security Rules block cross-tenant queries, enforce RBAC role permissions, and ensure tamper-evident audit logs cannot be modified by staff.
  4. **ProGuard / R8 Optimization:** Enable minification and resource shrinking; verify release build launches in $< 1.5$ seconds on target test devices.
- **Verification Gate:** Complete green test pass on CI/CD pipeline (`./gradlew test connectedAndroidTest`) and generate signed release bundle.

---

## 3. Dependency Wave & Execution Matrix

| Wave | Phases | Core Deliverables | Pre-requisites | Exit Criteria |
| :--- | :--- | :--- | :--- | :--- |
| **Wave 1** | Phase 0, 1 | Gradle setup, Design System Theme, Multi-Auth, Navigation Shell | Clean repo | Authenticated user lands on Dashboard |
| **Wave 2** | Phase 2, 3 | Room DB, SyncEngine, POS Checkout, Barcode Scanner, Expiry/Batches | Wave 1 | Complete offline sale saves and syncs to Firestore |
| **Wave 3** | Phase 4, 5 | Analytics Dashboard, Heatmaps, PDF Export, V1 Rule Engine, Health Check | Wave 2 | Accurate KPI graphs, heatmaps, and rule-based tips |
| **Wave 4** | Phase 6, 7 | Voice Log, AI Chatbot, HITL Confirmation, Branding Kit, Poster Canvas | Wave 3 | Spoken stock update confirmed; poster generated |
| **Wave 5** | Phase 8, 9 | GST Invoicing, Loyalty, UPI Cashbook, QR Menu, E2E Testing, Release AAB | Wave 4 | Full test suite passes; release bundle built |
