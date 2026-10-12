# StoreVisionPro — SRS Analysis & Requirements Traceability Matrix

## 1. Executive Summary & Document Context

This document provides a comprehensive, reconciled analysis of the System Requirements Specification (SRS) for **StoreVisionPro**, synthesized from two primary source artifacts:
1. `StoreVisionPro_Unified_Requirements_v2.pdf` (Unified Requirements and Product Blueprint — 20 Pages)
2. `requirements_updated_merged_with_additions.md` (Updated Merged Requirements with Industry Additions — 611 Lines)

StoreVisionPro is a mobile-first (Android / Kotlin Jetpack Compose) and future-desktop retail operating system, analytics engine, and marketing automation suite tailored for modern small-to-medium retail and hospitality businesses (grocery, apparel/fashion, electronics/mobile, cosmetics, FMCG, and cafes/restaurants). 

The platform bridges operational point-of-sale (POS) workflows with intelligence engines: offline-first data synchronization, statistical business analytics, heuristic/AI-driven pricing and inventory forecasting, agentic conversational assistance, and automated generative marketing material creation.

---

## 2. Source Comparison & Reconciliation Analysis

### 2.1 Reconciled Differences and Structural Alignments

| Area / Feature | PDF Specification (`v2.pdf`) | Markdown Specification (`.md`) | Reconciled Architectural Decision |
| :--- | :--- | :--- | :--- |
| **Document Purpose** | Visual blueprint, screen wireframe hierarchy, 6-module functional grouping, roadmap, and UI specs. | Exhaustive user stories, formal RFC 2119 keywords (`SHALL`, `SHOULD`, `MAY`), detailed edge cases. | Unified into a single engineering baseline: RFC 2119 rigor backed by exact UI blueprints and 6 product modules. |
| **Module vs Tab Topology** | 6 functional modules mapped to 4 bottom tabs + 1 persistent floating AI Assistant + Settings/Extra Tools screen. | Outlined 16 original requirements + 8 industry additions (Req 17 to 24). | Navigation architecture standardizes on **4 primary bottom tabs** (`Sales & Entry`, `Analytics`, `AI Insights`, `Marketing`), **1 global floating AI Action Button (FAB)** (`Chatbot / Voice Log`), and an **Extra Tools & Settings drawer/screen**. |
| **Data Storage Schema** | 24 Firestore collections + 1 local `syncQueue` Room table explicitly typed. | Outlined base collections with suggested additions in Appendix B and Req 24. | Full 24-collection schema adopted natively into Cloud Firestore and mirrored into local SQLite/Room entities with two-way sync. |
| **AI Governance & Control** | Emphasizes "Owner in control", accept/edit/reject modals, no auto-writes. | Deeply formalizes Requirement 14, Requirement 24, Cross-cutting Section A & G. | Strict **Zero Unapproved Mutation** rule. All AI outputs remain in `PROPOSED` state until an explicit cryptographic/user session confirmation is persisted to `auditLogs`. |
| **Offline-First Resilience** | Room/SQLite local database with sync queue. Full POS offline. | Detail on conflict resolution, idempotency tokens, restart survival, sync status states. | **Offline-First Clean Architecture**: Local Room DB is the single source of truth for the UI; a bidirectional `SyncManager` background engine synchronizes changes to Firestore using atomic batch transactions and idempotency UUIDs. |
| **Market Intelligence Integrity** | Rules for trending items and competitor prices. | Requirement 18, 19, 24: like-for-like comparison, source citations, date/time stamps, strict anti-hallucination. | External trend and pricing data must maintain immutable provenance records (`sourceId`, `retrievalTimestamp`, `sourceUrl`). Pure generative hallucinations of competitor prices are hard-blocked. |
| **Hospitality Domain Scope** | Listed as Module 6 sub-feature and Future Scope. | Requirement 23: Complete QR-menu ordering, status lifecycle, tenant isolation. | Isolated hospitality plug-in module: available only when `shopType IN ['hotel', 'cafe', 'restaurant']`, rendering a lightweight customer-facing Web/Compose menu. |

---

## 3. Product Module Decomposition

The complete functional footprint is organized into six core modules:

```
+----------------------------------------------------------------------------------------------------+
|                                      STOREVISIONPRO PLATFORM                                       |
+----------------------------------------------------------------------------------------------------+
|                                                                                                    |
|  [ Module 1: Shop Operations ]          [ Module 2: Analytics Dashboard ]                          |
|  - POS Sales Recording & Multi-Item     - Peak & Quiet Time footfall analysis                      |
|  - Barcode / QR Scanning & Lookup       - Top-10 / Least Sold & Dead Stock metrics                 |
|  - AI-Assisted Product Ingestion        - Income, Profit & Expense aggregations                    |
|  - Real-time Inventory & Batches        - Hourly, Day-wise & Weekly Heatmaps                       |
|  - Expiry & Best-Before Monitoring      - Seasonal, Weather & Custom Event impacts                 |
|  - Expense Tracking & Cashbook Ledger   - Repeat Customer & Cohort analytics                       |
|  - Supplier Catalog & Payables          - One-Click PDF Report Export                              |
|                                                                                                    |
|  [ Module 3: AI Business Insights ]     [ Module 4: AI Chatbot & Voice Assistant ]                 |
|  - V1 Offline Heuristic Rule Engine     - Real-time Inventory & Financial Q&A                      |
|  - V2 Cloud Dynamic Pricing Guidance    - Natural Language / Voice Stock Updates                   |
|  - Stock-Out & Reorder Forecasting      - Voice Log Expense & Transaction Capture                  |
|  - Automated Bundle & Combo Generator   - Conversational Business Health Diagnostic                |
|  - Industry-Aware Market Trends         - Structured Confirmation Modals                           |
|  - Sourced Competitor Price Comparison  - Immutable User Action Auditing                           |
|  - Business Health Check Diagnostic                                                                |
|                                                                                                    |
|  [ Module 5: AI Marketing Creator ]     [ Module 6: Business Management Tools ]                    |
|  - Guided Branding Kit Onboarding       - GST-Compliant PDF Invoicing Engine                       |
|  - Persistent Logo & Palette Repository - Customer Loyalty QR & Reward Ledger                     |
|  - Offline Template Poster Canvas       - Cashbook Ledger & UPI Reconciliation                     |
|  - Festival-Themed Creative Engine      - Multi-Shop Tenant Switcher & Consolidated Rollups        |
|  - AI Copywriting (Captions, Hashtags)  - QR-Menu Setup & Real-Time Order Handling                 |
|  - Voice-to-Ad Concept Pipeline         - RBAC Permission Enforcement (Owner/Manager/Staff)        |
|                                                                                                    |
+----------------------------------------------------------------------------------------------------+
|                                      CROSS-CUTTING FOUNDATION                                      |
|  - Offline-First Sync Engine (Room + Firestore)    - Multi-Factor Auth (Google, Phone OTP, Biometric)  |
|  - Strict Human-in-the-Loop AI Governance Engine   - Multilingual Localization Engine (i18n)       |
|  - Tamper-Evident Audit Logging Subsystem          - Evidence-Based Source Transparency Gate       |
+----------------------------------------------------------------------------------------------------+
```

---

## 4. Requirements Traceability Matrix (RTM)

The following matrix maps every requirement from the Unified Requirements specification to its functional module, architectural tier, target data entities, UI screen, and implementation wave.

| Req ID | Requirement Description | Module | Architectural Component | Firestore / Room Entities | UI Blueprint Screen | Implementation Wave |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1.1** | Add, edit, delete product with name, category, price, stock, photo | Module 1 | `ProductRepository`, `InventoryManager` | `products`, `ProductEntity` | Screen 4: Product Management | Wave 2 (Phase 2) |
| **1.2** | Barcode / QR scanning for auto-fill and POS lookup | Module 1 | `BarcodeScannerService` (CameraX / MLKit) | `products` | Screen 4, Screen 5 | Wave 2 (Phase 2) |
| **1.3** | AI-assisted product ingestion via image recognition (suggestion only) | Module 1 | `VisionProductClassifier`, `AiGatewayClient` | `aiSuggestions`, `products` | Screen 4: Product Management | Wave 5 (Phase 6) |
| **1.4** | Initial stock rule: local entry only, never from internet scraping | Module 1 | `ProductValidationUseCase` | `products` | Screen 4: Product Management | Wave 2 (Phase 2) |
| **1.5** | Sales entry: search, quantity stepper, stock preview, bill summary | Module 1 | `SalesCheckoutUseCase`, `POSViewModel` | `sales`, `saleItems` | Screen 5: Sales Entry | Wave 2 (Phase 2) |
| **1.6** | Multi-item sale persistence with atomic link to main sale record | Module 1 | `SaleTransactionDao` (Room Transaction) | `sales`, `saleItems`, `syncQueue` | Screen 5: Sales Entry | Wave 2 (Phase 2) |
| **1.7** | Calendar view for day-wise and month-wise sales navigation | Module 1 | `CalendarAnalyticsUseCase` | `sales` | Screen 6: Calendar View | Wave 2 (Phase 2) |
| **1.8** | Customer count default to 1; 'visited, no purchase' footfall counter | Module 1 | `FootfallTracker` | `sales`, `footfallVisits` | Screen 5: Sales Entry | Wave 2 (Phase 2) |
| **1.9** | Real-time inventory decrement, low-stock threshold, pre-peak alerts | Module 1 | `InventoryTrackerService`, `AlertManager` | `products`, `alerts` | Screen 3, Screen 4 | Wave 2 (Phase 2) |
| **1.10** | Expense tracking (rent, electricity, salary, stock) with Voice Log | Module 1 | `ExpenseRepository`, `VoiceLogParser` | `expenses`, `voiceLogs` | Screen 3, Screen 12 | Wave 2 (Phase 2) |
| **1.11** | Supplier management, purchase costs, delivery schedule, payables | Module 1 | `SupplierRepository`, `PaymentTracker` | `suppliers`, `supplierPayments` | Screen 12: Extra Tools | Wave 2 (Phase 2) |
| **1.12** | Role-based access control (Owner, Manager, Entry-only Staff) + Audit | Module 1 | `SessionSecurityManager`, `AuditLogger` | `users`, `auditLogs` | Screen 12: Extra Tools | Wave 1 & 2 (Ph 1, 2) |
| **1.13** | Expiry and best-before batch tracking (FIFO/FEFO inventory) | Module 1 | `BatchInventoryTracker` | `productBatches`, `products` | Screen 4, Screen 16 | Wave 2 (Phase 2) |
| **1.14** | Near-expiry warning window alerts and stock protection suggestions | Module 1 | `ExpiryAlertWorker`, `RuleEngine` | `productBatches`, `aiSuggestions` | Screen 16: Expiry Alerts | Wave 2 & 4 (Ph 2, 4) |
| **2.1** | Peak and quiet time traffic analysis by hour, day, and season | Module 2 | `TrafficAnalyticsEngine` | `sales`, `footfallVisits` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.2** | Data sufficiency indicators preventing statistical hallucination | Module 2 | `DataSufficiencyValidator` | Local Analytics Cache | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.3** | Top-10 most-sold, least-sold, changing demand, and dead stock | Module 2 | `ProductPerformanceAggregator` | `sales`, `saleItems`, `products` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.4** | Daily, weekly, monthly, and yearly income summaries | Module 2 | `FinancialMetricsAggregator` | `sales` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.5** | Net profit/loss calculation (Revenue minus expenses) | Module 2 | `ProfitLossCalculator` | `sales`, `expenses` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.6** | Multi-dimensional heatmaps (Hourly customer, Day footfall, Weekly sales) | Module 2 | `HeatmapRenderer` (Custom Compose Canvas) | `sales` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.7** | Animated charts: Line (income), Bar (products, hourly), Pie (categories) | Module 2 | `MPAndroidChartWrapper` / Compose Charts | `sales`, `products` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.8** | Seasonal trends and custom event correlations (weather, festivals) | Module 2 | `SeasonalCorrelationEngine` | `sales`, `events` | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.9** | Repeat customer tracking, RFM analysis, high-value customer identification | Module 2 | `CustomerCohortEngine` | `customers`, `sales` | Screen 7, Screen 12 | Wave 3 (Phase 3) |
| **2.10** | Slow-moving stock alerts with suggested clearance discounts | Module 2 | `SlowMovingStockDetector` | `products`, `aiSuggestions` | Screen 8: AI Insights | Wave 3 & 4 (Ph 3, 4) |
| **2.11** | One-click PDF report export for daily, weekly, monthly records | Module 2 | `PdfReportGenerator` (Android PdfDocument) | Local Cache | Screen 7: Analytics Dashboard | Wave 3 (Phase 3) |
| **2.12** | Live financial metrics feed into Business Health Check engine | Module 2 | `BusinessHealthDataBridge` | In-memory State / Aggregates | Screen 9: Health Check | Wave 3 & 4 (Ph 3, 4) |
| **3.1** | V1 Rule-based offline insights engine (IF-condition heuristic rules) | Module 3 | `OfflineRuleEngine` (Local Kotlin Heuristics) | Local Room DB | Screen 8: AI Insights | Wave 4 (Phase 4) |
| **3.2** | Dynamic pricing adjustment recommendations based on velocity | Module 3 | `DynamicPricingAdvisor` | `aiSuggestions`, `products` | Screen 8: AI Insights | Wave 4 & 5 (Ph 4, 6) |
| **3.3** | Inventory forecasting: stock-out predictions and margin optimization | Module 3 | `StockForecastingService` | `products`, `sales` | Screen 8: AI Insights | Wave 4 & 5 (Ph 4, 6) |
| **3.4** | Seasonal and festival demand prediction per retail category | Module 3 | `SeasonalDemandPredictor` | `aiSuggestions` | Screen 8: AI Insights | Wave 4 & 5 (Ph 4, 6) |
| **3.5** | Cross-selling combo / bundle suggestions (e.g. Bread + Jam) | Module 3 | `AssociationRuleMiner` (Market Basket) | `aiSuggestions` | Screen 8: AI Insights | Wave 4 & 5 (Ph 4, 6) |
| **3.6** | Auto offer generator: Buy 2 Get 1, flat discount, happy hour | Module 3 | `OfferGeneratorUseCase` | `aiSuggestions` | Screen 8: AI Insights | Wave 4 (Phase 4) |
| **3.7** | Geolocation-aware competitor price benchmarking (with opt-in) | Module 3 | `CompetitorPriceAggregator` | `priceObservations` | Screen 15: Price Comparison | Wave 5 (Phase 6) |
| **3.8** | Retail category social media trend extraction | Module 3 | `SocialTrendDiscoveryService` | `trendRecords` | Screen 14: Trending Products | Wave 5 (Phase 6) |
| **3.9** | AI Business Health Check: diagnostic audit across revenue, margin, stock | Module 3 | `BusinessHealthCheckEngine` | `aiSuggestions`, `auditLogs` | Screen 9: Health Check | Wave 4 & 5 (Ph 4, 6) |
| **3.10** | Offer & scheme feasibility logic (margin, budget, customer benefit) | Module 3 | `OfferFeasibilityEvaluator` | `aiSuggestions` | Screen 9: Health Check | Wave 4 & 5 (Ph 4, 6) |
| **3.11** | Transparent missing data disclosures on recommendations | Module 3 | `ConfidenceEvaluator` | Local UI State | Screen 8, Screen 9 | Wave 4 (Phase 4) |
| **3.12** | Suggestion-only enforcement: absolute block on automatic mutations | Module 3 | `ApprovalGateInterceptor` | `aiSuggestions` | All AI Screens | Wave 4 (Phase 4) |
| **3.13** | Shop-type aware personalization across grocery, fashion, mobile, FMCG | Module 3 | `IndustryContextResolver` | `shops` | All Modules | Wave 1 & 4 (Ph 1, 4) |
| **3.16** | Market trends ingestion with verifiable provenance and timestamps | Module 3 | `MarketIntelligenceRepository` | `trendRecords`, `marketSources` | Screen 14: Trending Products | Wave 5 (Phase 6) |
| **3.19** | Like-for-like verified market price comparison table / chart | Module 3 | `PriceComparisonEngine` | `priceObservations`, `marketSources` | Screen 15: Price Comparison | Wave 5 (Phase 6) |
| **3.23** | Waste-reducing near-expiry bundle proposals with margin impact | Module 3 | `NearExpiryOptimizationEngine` | `productBatches`, `aiSuggestions` | Screen 16: Expiry Alerts | Wave 4 (Phase 4) |
| **4.1** | Chatbot stock inquiries ('how many Oreo biscuits left?') | Module 4 | `AssistantQueryEngine`, `StockQueryHandler` | `products` | Screen 10: AI Chatbot / Voice | Wave 5 (Phase 6) |
| **4.2** | Chatbot financial metric inquiries (sales, expenses, profit) | Module 4 | `FinancialQueryHandler` | `sales`, `expenses` | Screen 10: AI Chatbot / Voice | Wave 5 (Phase 6) |
| **4.3** | Chatbot strategy inquiry running Business Health Check in background | Module 4 | `StrategyQueryHandler` | `aiSuggestions` | Screen 10: AI Chatbot / Voice | Wave 5 (Phase 6) |
| **4.4** | Natural language / voice stock updates with confirmation modal | Module 4 | `AgenticUpdateParser`, `ConfirmationWorkflow` | `products`, `voiceLogs` | Screen 10: AI Chatbot / Voice | Wave 5 (Phase 6) |
| **4.5** | Voice Log: spoken entry converted into structured transaction record | Module 4 | `VoiceLogManager`, `SpeechToTextService` | `voiceLogs`, `expenses`, `sales` | Screen 10, Screen 12 | Wave 5 (Phase 6) |
| **4.6** | Assistant security scoping to authenticated user tenant and permissions | Module 4 | `AssistantSecurityEnforcer` | Session Token | Screen 10: AI Chatbot / Voice | Wave 5 (Phase 6) |
| **5.1** | Built-in offline poster templates (Canvas 2D rendering, no-AI base) | Module 5 | `PosterCanvasRenderer` | Local Templates, `campaigns` | Screen 11: Marketing Tools | Wave 4 (Phase 5) |
| **5.2** | Copywriting generator: WhatsApp status, Instagram captions, hashtags | Module 5 | `MarketingCopywriterService` | `campaigns` | Screen 11: Marketing Tools | Wave 5 (Phase 6) |
| **5.3** | Festival wishing posters with logo, shop name, and social IDs | Module 5 | `FestivalPosterBuilder` | `campaigns`, `brandingProfiles` | Screen 11: Marketing Tools | Wave 4 & 5 (Ph 5, 6) |
| **5.5** | Voice-to-Ad pipeline (Reel script, WhatsApp story, 15s video concept) | Module 5 | `VoiceToAdOrchestrator` | `campaigns` | Screen 11: Marketing Tools | Wave 5 (Phase 6) |
| **5.6** | Shop Branding Kit: logo creation, brand palette, banner generation | Module 5 | `BrandingKitManager` | `brandingProfiles`, `logoVersions` | Screen 17: Branding Kit | Wave 5 (Phase 6) |
| **5.7** | AI product photography enhancement (background, lighting, sharpness) | Module 5 | `ImageEnhancementService` | `products`, Cloud Storage | Screen 11: Marketing Tools | Wave 6 (Phase 7) |
| **5.9** | Complete owner review and publishing control before social sharing | Module 5 | `CampaignPublishGatekeeper` | `campaigns`, `auditLogs` | Screen 18: Campaign Builder | Wave 4 & 5 (Ph 5, 6) |
| **5.12** | Logo onboarding: reuse existing logo or trigger guided generation | Module 5 | `LogoOnboardingStateMachine` | `brandingProfiles`, `logoVersions` | Screen 17: Branding Kit | Wave 5 (Phase 6) |
| **5.16** | Context-aware campaign generation from approved trends/alerts/health | Module 5 | `InsightToCampaignBridge` | `campaigns`, `aiSuggestions` | Screen 18: Campaign Builder | Wave 5 (Phase 6) |
| **6.1** | GST-compliant PDF invoice generation, sharing, and history tracking | Module 6 | `InvoiceGeneratorService` | `invoices`, `sales` | Screen 12: Extra Tools | Wave 6 (Phase 7) |
| **6.2** | Customer loyalty system: QR code identification, points, coupons | Module 6 | `LoyaltyEngine` | `customers` | Screen 12: Extra Tools | Wave 6 (Phase 7) |
| **6.3** | Cashbook ledger (cash-in, cash-out, UPI) & day-end closing report | Module 6 | `CashbookManager` | `cashbook` | Screen 12: Extra Tools | Wave 6 (Phase 7) |
| **6.4** | Multi-shop tenant management, branch switching, aggregated rollups | Module 6 | `MultiShopContextManager` | `shops`, `users` | Screen 12: Extra Tools | Wave 6 (Phase 7) |
| **6.6** | Hospitality QR-menu setup (item catalog, pricing, availability) | Module 6 | `MenuCatalogManager` | `menus / orders` | Screen 19: Hospitality Admin | Wave 6 (Phase 7) |
| **6.7** | Customer QR-menu web/app ordering workflow and cart verification | Module 6 | `CustomerOrderingService` | `menus / orders` | Screen 20: Customer QR Menu | Wave 6 (Phase 7) |
| **6.8** | Kitchen order handling pipeline (received, accepted, preparing, ready) | Module 6 | `OrderProcessingQueue` | `menus / orders` | Screen 19: Hospitality Admin | Wave 6 (Phase 7) |
| **X1** | Cross-cutting: Human-in-the-loop AI governance (zero auto-mutations) | Platform | `GovernanceInterceptor` | `aiSuggestions`, `auditLogs` | All Screens | All Waves |
| **X2** | Cross-cutting: Offline-first Room persistence and Firestore sync | Platform | `SyncEngine`, `RoomDatabase` | `syncQueue`, All Room Entities | Screen 13: Sync Status | Wave 1 & 2 (Ph 1, 2) |
| **X3** | Cross-cutting: Multi-auth (Email, Google, Phone OTP, Biometric) | Platform | `AuthenticationManager` | Firebase Auth, BiometricPrompt | Screen 1: Login / Register | Wave 1 (Phase 1) |
| **X4** | Cross-cutting: Multilingual support with persistent language preference | Platform | `LocalizationManager` | `res/values-*/strings.xml`, `users` | Screen 2: Language Selector| Wave 1 (Phase 1) |
| **X6** | Cross-cutting: Security, RBAC session scoping, and audit logging | Platform | `AuditLoggingInterceptor` | `auditLogs` | Screen 12: Extra Tools | Wave 1 & 2 (Ph 1, 2) |
| **X7** | Cross-cutting: Evidence integrity, citations, and anti-hallucination | Platform | `ProvenanceValidator` | `marketSources`, `trendRecords`| Screen 14, Screen 15 | Wave 5 (Phase 6) |

---

## 5. Architectural Quality Attributes & Non-Functional Constraints

1. **Local Latency (< 100ms)**: POS item scanning, bill calculations, and transaction persistence must complete synchronously on the local Room database without blocking on network I/O.
2. **Deterministic Offline Survival**: All core operational capabilities (inventory lookup, barcode scanning, sales checkout, cashbook logging, PDF invoice generation, and V1 rule-based insights) must operate at 100% functionality without internet connectivity.
3. **Audit Trail Completeness**: 100% of data mutations triggered by AI proposals, manual overrides, role assignments, or price adjustments must be permanently recorded in `auditLogs` with `userId`, `timestamp`, `beforeValue`, and `afterValue`.
4. **Data Isolation in Multi-Tenant Accounts**: All database queries must enforce an explicit `shopId` filter at both the local Room DAO level and Cloud Firestore Security Rules level, preventing cross-tenant data contamination.
5. **Cold-Start Startup Time (< 1.5s)**: App launch must render the Main Dashboard with cached local data within 1500 milliseconds on a standard Android mid-range device (minSdk 24).
