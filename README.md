# StoreVisionPro — Unified Retail OS & AI Intelligence Platform

StoreVisionPro is an offline-first, mobile-centric retail operating system and AI-powered marketing suite designed for small-to-medium retail and hospitality businesses (grocery, apparel, mobile/electronics, cosmetics, FMCG, and cafes/restaurants).

---

## 📚 Complete Engineering & Architecture Documentation

All architectural specifications, design contracts, data schemas, and production plans are documented in the [`docs/`](file:///c:/Users/Sumit%20Borase/AndroidStudioProjects/storevisionpro/docs/) directory:

1. **[SRS Analysis & Requirements Traceability Matrix (`docs/SRS_ANALYSIS_AND_TRACEABILITY.md`)](file:///c:/Users/Sumit%20Borase/AndroidStudioProjects/storevisionpro/docs/SRS_ANALYSIS_AND_TRACEABILITY.md)**
   - Reconciles `StoreVisionPro_Unified_Requirements_v2.pdf` and `requirements_updated_merged_with_additions.md`.
   - Comprehensive decomposition of all 24 requirements, 6 modules, and cross-cutting governance rules.
   - Traceability Matrix mapping every requirement to architectural components and delivery waves.

2. **[System Architecture Design Document (`docs/SYSTEM_ARCHITECTURE.md`)](file:///c:/Users/Sumit%20Borase/AndroidStudioProjects/storevisionpro/docs/SYSTEM_ARCHITECTURE.md)**
   - High-level topology: Clean Architecture + MVI on Android, Cloud Firestore, and AI Orchestration Gateway.
   - Deep dive into all 6 subsystems: POS, Analytics, AI Business Insights, Conversational Assistant, Marketing Studio, and Business Tools.
   - Offline-first synchronization engine (`syncQueue` state machine, conflict resolution).
   - Strict Human-in-the-Loop (HITL) AI governance gate and Role-Based Access Control (RBAC).

3. **[Stitch Semantic Design System Specification (`docs/DESIGN.md`)](file:///c:/Users/Sumit%20Borase/AndroidStudioProjects/storevisionpro/docs/DESIGN.md)**
   - Crafted using `stitch-design-taste` guidelines.
   - Cockpit Dense visual atmosphere, calibrated Slate/Zinc palette with Electric Emerald accent.
   - Typographic system (`Cabinet Grotesk` display + `Geist` body + `JetBrains Mono` tabular).
   - Component behaviors, anti-patterns, and comprehensive blueprints for all **20 application screens**.

4. **[Data Schema & API Contracts (`docs/DATA_SCHEMA_AND_CONTRACTS.md`)](file:///c:/Users/Sumit%20Borase/AndroidStudioProjects/storevisionpro/docs/DATA_SCHEMA_AND_CONTRACTS.md)**
   - Complete schema for all 24 Cloud Firestore collections with validation rules.
   - Complete Kotlin Room entities, foreign keys, and reactive DAO signatures.
   - OpenAPI 3.1 contracts for Chatbot, Voice Log parsing, Health Check, and Verified Market Pricing.

5. **[Production-Line Implementation Plan (`docs/IMPLEMENTATION_PLAN.md`)](file:///c:/Users/Sumit%20Borase/AndroidStudioProjects/storevisionpro/docs/IMPLEMENTATION_PLAN.md)**
   - Structured across 5 Waves and 10 detailed engineering phases (Phase 0 to Phase 9).
   - Step-by-step task breakdown, file manifests, exit criteria, and verification test gates.

---

## 🛠 Technology Stack Baseline

- **Mobile Client:** Android Native (Kotlin 2.2+, Jetpack Compose, Material 3)
- **Architecture Pattern:** Clean Architecture + MVI (Model-View-Intent) with Coroutines & StateFlow
- **Local Persistence (SSOT):** AndroidX Room (SQLite) with transactional FEFO/FIFO batch tracking
- **Background Synchronization:** Android Jetpack WorkManager with retry backoff & idempotency tokens
- **Cloud Backend:** Firebase (Authentication, Cloud Firestore, Cloud Storage, Cloud Functions)
- **Computer Vision & Hardware:** CameraX + Google ML Kit Barcode Scanning
- **Visual Analytics:** MPAndroidChart & Compose Canvas Heatmaps
- **Document Generation:** Android Native `PdfDocument` (GST invoices & financial summaries)
- **AI Orchestration:** Hybrid Architecture (Tier 1 Local Kotlin Heuristic Rules + Tier 2 Cloud LLM Gateway via Gemini / Whisper)

---

## 🏢 Six Product Modules

- **Module 1: Shop Operations (POS, Products, Inventory, Batches, Expenses, Suppliers)**
- **Module 2: Analytics Dashboard (Income Trends, Footfall Heatmaps, Leaderboards, Dead Stock)**
- **Module 3: AI Business Insights (Dynamic Pricing, Stock-Out Forecasting, Bundles, Health Check)**
- **Module 4: AI Chatbot & Voice Assistant (Conversational Q&A, Voice Log with Confirmation)**
- **Module 5: AI Marketing Creator (Branding Kit, Poster Canvas, Captions, Voice-to-Ad)**
- **Module 6: Business Management Tools (GST Invoices, Loyalty, Cashbook & UPI, Multi-Shop, Hospitality QR Menu)**
