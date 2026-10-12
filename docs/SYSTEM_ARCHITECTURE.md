# StoreVisionPro — System Architecture Design Document (SADD)

## 1. Architectural Philosophy & System Topology

StoreVisionPro is architectured as an **Offline-First, Edge-Centric Retail Operating System** with an asynchronous Cloud Synchronization and AI Orchestration Gateway. 

The core architectural tenet is **Deterministic Edge Autonomy**: A retail shop or restaurant must never stop operating because of spotty Wi-Fi, ISP downtime, or cloud server latency. All transactions, inventory adjustments, barcode lookups, daily cash reconciliations, and initial business rules run natively on the device using a local embedded database. The cloud backend acts as an analytical synthesizer, multi-device synchronizer, and gateway to large language/vision models.

```
+--------------------------------------------------------------------------------------------------------+
|                                        EDGE CLIENT: ANDROID APP                                        |
|  +--------------------------------------------------------------------------------------------------+  |
|  |                            Presentation Layer (Jetpack Compose + MVI)                            |  |
|  |  [POS Sales]   [Analytics UI]   [AI Insights UI]   [Marketing Studio]   [Floating AI Assistant]  |  |
|  +--------------------------------------------------------------------------------------------------+  |
|                                                  |                                                     |
|  +--------------------------------------------------------------------------------------------------+  |
|  |                              Domain Layer (Clean UseCases & Rules)                                |  |
|  |  - SalesCheckoutUseCase          - ExpiryTrackingUseCase         - V1 RuleBasedInsightsEngine   |  |
|  |  - InventorySyncUseCase          - BusinessHealthCheckUseCase    - HumanInTheLoopApprovalGate    |  |
|  +--------------------------------------------------------------------------------------------------+  |
|                                                  |                                                     |
|  +--------------------------------------------------------------------------------------------------+  |
|  |                       Data Layer: Single Source of Truth (SSOT) Architecture                      |  |
|  |   +---------------------------------------------+   +-----------------------------------------+  |  |
|  |   |           Room Database (SQLite)            |   |         SyncQueue Engine                |  |  |
|  |   |  - 18 Entities (Products, Sales, Cashbook)  |<--|  - WorkManager Sync Worker              |  |  |
|  |   |  - Full Reactive Streams (Kotlin Flow)      |   |  - Idempotent Change Batches            |  |  |
|  |   +---------------------------------------------+   +-----------------------------------------+  |  |
|  +--------------------------------------------------------------------------------------------------+  |
+---------------------------------------------------|----------------------------------------------------+
                                                    | Encrypted TLS / WebSocket / REST
                                                    v
+--------------------------------------------------------------------------------------------------------+
|                                  CLOUD & BACKEND ORCHESTRATION TIER                                    |
|  +---------------------------------------------+   +-------------------------------------------------+ |
|  |           Firebase Cloud Platform           |   |            AI Orchestration Gateway             | |
|  |  - Firebase Authentication (Google, Phone)  |   |           (Node.js / Python FastAPI)            | |
|  |  - Cloud Firestore (24 Collections)         |   |  - LLM Orchestration (Gemini Pro / Flash)      | |
|  |  - Cloud Storage (Logos, Product Images)    |   |  - Speech-to-Text & Voice-to-Ad Pipeline        | |
|  |  - Cloud Functions (Aggregations, Rollups)  |   |  - Market Intelligence & Trend Scraper          | |
|  |  - Security Rules (Multi-Tenant Isolation)  |   |  - Price Comparison Aggregator                  | |
|  +---------------------------------------------+   +-------------------------------------------------+ |
+--------------------------------------------------------------------------------------------------------+
```

---

## 2. Android Client Architecture (Clean Architecture + MVI)

The mobile client is built on **Clean Architecture** principles structured into three distinct tiers:

### 2.1 Presentation Layer (Jetpack Compose + MVI)
The UI is strictly declarative using Jetpack Compose, Material 3, and Unidirectional Data Flow (MVI):
- **UI State**: Immutable data classes representing the full snapshot of the screen at any instant (e.g., `SalesEntryUiState`, `AnalyticsUiState`).
- **UI Intent / Event**: User interactions dispatched as sealed interfaces to the ViewModel (e.g., `SalesIntent.AddProductToBill(barcode)`, `SalesIntent.ConfirmCheckout`).
- **ViewModel**: State holders extending `androidx.lifecycle.ViewModel`. ViewModels consume UseCases, expose `StateFlow<UiState>`, and emit one-off side effects via `SharedFlow<UiSideEffect>` (e.g., navigation triggers, toast alerts, thermal printer jobs).

### 2.2 Domain Layer
The domain layer is completely decoupled from Android framework dependencies:
- **UseCases**: Single-responsibility interactors encapsulating specific business logic (e.g., `ProcessSaleTransactionUseCase`, `EvaluateNearExpiryStockUseCase`, `GenerateRuleBasedInsightsUseCase`).
- **Domain Models**: Pure Kotlin models free of database annotations or JSON serialization tags.
- **Repository Contracts**: Interfaces defining data access operations implemented by the data layer.

### 2.3 Data Layer (Local Room SSOT + Remote Gateway)
The local SQLite database via **Room** serves as the Single Source of Truth:
- The UI layer *always* observes Room database queries through reactive Kotlin `Flow<T>`.
- Write operations write synchronously to the local Room tables and concurrently enqueue a change record in the local `syncQueue` table.
- A background `SyncWorker` managed by Android Jetpack `WorkManager` reads pending entries from `syncQueue`, dispatches mutations to Cloud Firestore, and flags entries as `SYNCED` upon remote confirmation.

```
[ User Interaction ] 
         │ 
         ▼
[ Intent / Event ] ──► [ ViewModel ] ──► [ UseCase ] ──► [ Repository ]
                             ▲                                 │
                             │ (StateFlow)                     ▼
                      [ Room Database ] <──────────── [ Local DAO Write ]
                             │                                 │
                             ▼                                 ▼
                     (Live UI Update)                  [ syncQueue Enqueue ]
                                                               │
                                                               ▼
                                                     [ WorkManager SyncWorker ]
                                                               │
                                                               ▼
                                                    [ Cloud Firestore Write ]
```

---

## 3. Subsystem Detailed Design

### 3.1 Subsystem 1: Shop Operations & Point-of-Sale (POS) Engine
- **Atomic Sales Transaction**: Multi-item bills are executed inside a single Room `@Transaction`. Inventory decrement, sales item creation, loyalty point calculation, and cashbook journal entry commit together or roll back completely.
- **High-Velocity Barcode Scanning**: Powered by Google ML Kit Barcode Scanning integrated directly into CameraX Preview via an `ImageAnalysis.Analyzer`. Supports EAN-13, EAN-8, UPC-A, Code 128, and QR codes with zero UI lag (< 50ms detection).
- **Batch & Expiry Management (FEFO/FIFO)**: Every product can optionally link to multiple `ProductBatchEntity` records with distinct `batchNo`, `receiptDate`, and `expiryDate`. Sales automatically draw stock using First-Expired-First-Out (FEFO) logic to minimize waste.
- **Supplier & Payables Tracking**: Keeps detailed ledgers of purchase orders, supplier dues, expected delivery schedules, and historical fulfillment accuracy.

### 3.2 Subsystem 2: Analytics & Business Intelligence Engine
- **In-Memory Aggregation Pipeline**: High-throughput time-series analysis calculates income, cost-of-goods-sold (COGS), gross margins, and net profits across Daily, Weekly, Monthly, and Yearly scopes.
- **Multi-Dimensional Heatmaps**: Custom Compose Canvas renders:
  1. Hourly customer footfall (24x7 matrix).
  2. Day-wise footfall distribution.
  3. Weekly sales volume heatmap.
- **Dead-Stock & Velocity Classifier**: Evaluates product velocity against configurable dormancy thresholds ($D_{threshold}$, default 14 days without sales) and marks items as slow-moving or dead stock.
- **PDF Report Engine**: Uses Android's native `android.graphics.pdf.PdfDocument` to generate print-ready, GST-compliant financial summaries and sales reports with zero external cloud dependencies.

### 3.3 Subsystem 3: AI Business Insights Engine
A hybrid two-tier intelligence pipeline ensures high availability and cost efficiency:
- **Tier 1 (Local Edge Heuristics — V1)**: Fully functional offline rule engine written in pure Kotlin. Evaluates:
  - Fast-selling threshold alerts ($Velocity > 2\times Average$).
  - Stock-out warning predictions ($RemainingStock / DailySalesVelocity < LeadTimeDays$).
  - Best-timing offer triggers (e.g., Happy-Hour discounts during identified historical quiet hours).
  - Dead-stock bundle suggestions (pairing high-margin high-velocity item with dormant item).
- **Tier 2 (Cloud AI Engine — V2)**: When connected, calls the backend AI Gateway:
  - Dynamic Pricing Advisor: Computes elasticity and suggests price shifts based on local demand surges.
  - Business Health Check Diagnostic: Deep multi-variate analysis cross-referencing profit margins, customer retention, basket size, and expense ratios to generate prioritized operational recommendations.
  - Sourced Market Intelligence: Ingests external trend reports and verified competitor prices with source URLs, dates, and like-for-like product SKU matching.

### 3.4 Subsystem 4: Conversational AI & Agentic Voice Engine
- **Speech Capture & Transcription**: Captures high-fidelity audio via Android `AudioRecord` / MediaRecorder and streams to backend Whisper STT, with local Android `SpeechRecognizer` fallback.
- **Agentic Intent Extraction**: LLM prompt engineering converts unstructured speech (e.g., *"Added 40 packets of Maggi noodles purchased for 480 rupees"*) into structured JSON containing `intent`, `entityType`, `productName`, `quantity`, `price`, `supplier`, `category`.
- **Strict Human-In-The-Loop Confirmation Protocol**: The parsed JSON is rendered in an interactive confirmation card with **Accept**, **Edit**, and **Reject** buttons. Under no circumstance does the chatbot directly write to the product, sales, or cashbook tables without explicit touch or voice confirmation.
- **Audit Logging**: Every confirmed voice or chat transaction is signed with the operator's `userId`, `timestamp`, and `transcript` inside `voiceLogs` and `auditLogs`.

### 3.5 Subsystem 5: Generative Marketing & Branding Engine
- **Guided Branding Kit Onboarding**: State machine guiding the owner through logo registration:
  - If existing logo: Image picker, crop tool, background transparency check, palette extractor (using AndroidX `Palette` API to extract dominant and vibrant brand colors).
  - If new logo requested: Multi-turn questionnaire (style, font approach, visual symbols, colors) -> Structured Brief confirmation -> Backend Diffusion / Vector generation -> Multi-variant preview -> Owner approval.
- **Offline Template Poster Engine**: 100% on-device rendering using Android `Canvas` and `Paint`. Dynamically composites product photos, typography, discount badges, shop branding, and contact details with zero cloud costs.
- **AI Copywriting & Voice-to-Ad**: Connects to AI Gateway to generate platform-specific copy: WhatsApp broadcast text, Instagram story captions, trending hashtags, and short 15-second promotional video scripts.

### 3.6 Subsystem 6: Business Management Tools & Hospitality Extension
- **GST Invoicing Subsystem**: Computes CGST, SGST, IGST, and HSN codes, generating formatted digital invoices shareable via Android Sharesheet (WhatsApp, Email, Print).
- **Cashbook & UPI Reconciliation**: Tri-column daily ledger (Cash-In, Cash-Out, UPI). Integrates with Android Notification Listener / SMS receiver (with user opt-in) to detect incoming UPI settlements from Google Pay, PhonePe, Paytm, and BharatPe, auto-generating a Day-End Closing Balance Sheet.
- **Hospitality QR-Menu Module**: Isolated domain triggered when `shopType` is cafe/restaurant/hotel. Renders a public QR menu web app (or lightweight client) allowing diners to place orders directly into the shop's live KDS (Kitchen Display System) / order queue.

---

## 4. Offline-First Synchronization Architecture

The synchronization engine guarantees eventual consistency across devices while ensuring instantaneous local performance.

### 4.1 Sync State Machine

```
              [ Client Mutation ]
                      │
                      ▼
               [ Save to Room ]
                      │
                      ▼
            [ Insert to syncQueue ]
             (Status: PENDING)
                      │
                      ▼
              [ Network Check ]
             ╱                 ╲
     [ Offline ]              [ Online ]
          │                        │
          ▼                        ▼
     (Sleep / Idle)         (Status: SYNCING)
                                   │
                                   ▼
                       [ Remote Firestore Batch ]
                              ╱          ╲
                      [ Success ]      [ Error / Conflict ]
                           │                   │
                           ▼                   ▼
                   (Status: SYNCED)     (Status: CONFLICT / FAILED)
                           │                   │
                           ▼                   ▼
                  [ Delete from Queue ]  [ Exponential Backoff ]
                                         [ or Manual Resolution UI ]
```

### 4.2 SyncQueue Table Structure
Each queued change record tracks:
- `changeId`: Unique UUID generated on the client.
- `collection`: Target Firestore collection name (e.g., `sales`, `products`, `expenses`).
- `documentId`: ID of the document being modified.
- `operation`: `CREATE`, `UPDATE`, `DELETE`.
- `payloadJson`: Complete serialized entity JSON payload.
- `createdAt`: Client millisecond timestamp.
- `retryCount`: Number of synchronization attempts made.
- `syncStatus`: `PENDING`, `SYNCING`, `SYNCED`, `FAILED`, `CONFLICT`.
- `errorMessage`: Diagnostic information if rejected.

### 4.3 Conflict Resolution Rules
1. **Sales & Cashbook (Append-Only Transactions)**: Transactions are immutable. Conflicts are impossible because each sale generates a globally unique UUID.
2. **Inventory Stock Level**: Handled via server-side differential increments (`FieldValue.increment(-soldQty)`) in Cloud Firestore, preventing stock overwrites when multiple devices record sales offline concurrently.
3. **Product Catalog & Metadata**: Last-Write-Wins (LWW) based on verified millisecond timestamps. If conflicting concurrent edits occur within a 5-second window, the system flags the product in Screen 13 (`Sync Status`) for manual owner resolution.

---

## 5. AI Governance, Guardrails & Security Architecture

### 5.1 The Human-in-the-Loop (HITL) Gate
To prevent autonomous model hallucinations from damaging retail operations, the system enforces strict architectural gates:

```
[ AI Engine / Gateway ] ──► Emits [ PROPOSED Suggestion ]
                                    │
                                    ▼
                         Is Mutation Privileged?
                         (Price, Discount, Order, Delete)
                                ╱       ╲
                             [YES]     [NO] (Read-only query)
                               │         │
                               ▼         ▼
                 [ Present HITL UI Dialog ]   [ Direct Return to UI ]
                   - Accept Button
                   - Edit In-Place Field
                   - Reject Button
                               │
            ┌──────────────────┴──────────────────┐
            ▼                                     ▼
      [ User Confirms ]                     [ User Rejects ]
            │                                     │
            ▼                                     ▼
    [ Write to Room & Firestore ]         [ Mark REJECTED ]
    [ Append to auditLogs ]               [ Append to auditLogs ]
```

### 5.2 Role-Based Access Control (RBAC)
User permissions are strictly enforced both client-side in Compose Navigation and server-side in Firestore Security Rules:
- **Shop Owner (`ROLE_OWNER`)**: Full administrative access, shop configuration, staff role assignment, financial margin reports, AI approval gates, and multi-shop switching.
- **Store Manager (`ROLE_MANAGER`)**: Product catalog editing, stock receipt, supplier management, expense recording, POS sales, and poster generation. Blocked from viewing net profit balances and altering staff permissions.
- **Staff / Cashier (`ROLE_STAFF`)**: Entry-only access. Capable of scanning products, entering sales, issuing invoices, and recording cash-in/out. Blocked from modifying product prices, deleting records, or viewing analytics.

---

## 6. Cloud Backend & AI Gateway Architecture

The cloud infrastructure comprises Firebase managed services paired with a lightweight AI Orchestration Gateway built with Node.js/TypeScript or Python FastAPI:

```
+-----------------------------------------------------------------------------------+
|                         AI ORCHESTRATION GATEWAY RUNTIME                          |
|                                                                                   |
|  +-----------------------+  +-----------------------+  +-----------------------+  |
|  |     STT Service       |  |     LLM Assistant     |  |    Vision Analyzer    |  |
|  |  - Whisper API        |  |  - Gemini 2.0 Flash   |  |  - Product OCR / Net  |  |
|  |  - Audio Preprocessor |  |  - Strict JSON Schema |  |  - Background Removal |  |
|  +-----------------------+  +-----------------------+  +-----------------------+  |
|                                                                                   |
|  +-----------------------+  +-----------------------+  +-----------------------+  |
|  |  Market Intelligence  |  |    Marketing Copy     |  |   Health Diagnostic   |  |
|  |  - Catalog Feeds      |  |  - Social Captions    |  |  - Multivariate Math  |  |
|  |  - Source Provenance  |  |  - Reel Scripts       |  |  - Suggestion Engine  |  |
|  +-----------------------+  +-----------------------+  +-----------------------+  |
+-----------------------------------------------------------------------------------+
```

- **Strict Schema Enforcement**: All generative LLM calls utilize structured outputs (JSON schema) with zero conversational filler.
- **Source Transparency Engine**: Market price comparisons query verified distributor catalogs and public price aggregators, caching results with `sourceName`, `sourceUrl`, and `observationTimestamp`. If data is older than 72 hours, it is flagged as stale. If no verified source is found, the system explicitly returns `INSUFFICIENT_DATA` rather than generating estimated prices.
