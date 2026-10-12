# Design System: StoreVisionPro

## 1. Visual Theme & Atmosphere
StoreVisionPro employs a **Cockpit Dense, High-Utility Industrial Retail** aesthetic. Built for busy store cashiers, shop proprietors, and restaurant operators, the interface avoids decorative fluff, bloated whitespace, and slow animations in favor of precision, rapid data scanning, and high information density.

- **Density Score:** 8/10 (Cockpit Dense). Screens maximize visible tabular data, inventory alerts, and financial metrics without requiring excessive scrolling.
- **Variance Score:** 6/10 (Offset Asymmetric). Dashboards balance structured data grids with distinct hero metric spotlights.
- **Motion Intensity:** 6/10 (Fluid Spring Physics). Micro-interactions provide instant tactile feedback without delaying transaction execution.

The visual atmosphere evokes a calibrated hardware instrument: clean charcoal and zinc backgrounds, high-contrast crisp typography, restrained border-defined containment, and an energetic single accent that draws the eye directly to critical actions (scan, confirm, checkout).

---

## 2. Color Palette & Functional Roles
All colors strictly adhere to a neutral zinc foundation paired with a single vibrant emerald functional accent. Saturation is calibrated under 80%. Pure black (`#000000`) and the AI purple/neon glow aesthetic are strictly forbidden.

| Role | Token Name | Hex Code | Functional Application |
| :--- | :--- | :--- | :--- |
| **Canvas Background** | Slate Dark | `#09090B` | Root app window, primary dark surface |
| **Card Surface** | Zinc Charcoal | `#18181B` | POS cards, list containers, modals, bottom sheets |
| **Elevated Surface** | Zinc Metal | `#27272A` | Active input fields, elevated chips, dropdown menus |
| **Primary Text** | Off-White Pearl | `#F4F4F5` | Display headers, primary labels, totals, barcodes |
| **Secondary Text** | Muted Silver | `#A1A1AA` | Subtitles, timestamps, units, metadata descriptions |
| **Subtle Border** | Slate Divider | `#3F3F46` | 1px card borders, table dividers, column rules |
| **Primary Accent** | Electric Emerald | `#10B981` | Checkout CTA, confirm buttons, positive indicators |
| **Accent Glow Surface** | Emerald Muted | `#064E3B` | Selected tab backgrounds, active filter chip surfaces |
| **Warning / Caution** | Warning Amber | `#F59E0B` | Low-stock alerts, near-expiry warnings, pending sync |
| **Danger / Critical** | Signal Crimson | `#EF4444` | Dead stock, stock-out alerts, reject actions, errors |
| **Info / Provenance** | Precision Azure | `#38BDF8` | External market intelligence, source citation links |

---

## 3. Typographic Architecture
To preserve clarity under bright retail lighting and across variable screen sizes, StoreVisionPro employs modern Grotesque and tabular Monospace pairings:

- **Display & Headline Font:** `Cabinet Grotesk` (fallback: `Geist Sans`, system sans-serif). Tight letter tracking (`-0.02em`), bold weights, assertive and modern.
- **Body & Navigation Font:** `Geist Sans` (fallback: `Inter` strictly banned, use system Roboto/Helvetica). Generous line height (1.4), max 65 characters per line for readable guidance.
- **Tabular Data & Numbers Font:** `JetBrains Mono` (fallback: `Geist Mono`). **Mandatory for all financial currencies (₹), stock counts, barcode numbers, time codes, and SKU codes.** Prevents layout jitter during live counter animations.

### Scale Hierarchy
- **Header Large (Hero KPI):** 32sp / Bold / `Cabinet Grotesk`
- **Header Medium (Section Title):** 20sp / SemiBold / `Cabinet Grotesk`
- **Header Small (Card Title):** 16sp / Medium / `Cabinet Grotesk`
- **Body Regular:** 14sp / Normal / `Geist Sans`
- **Body Caption:** 12sp / Normal / `Geist Sans` (Muted Silver)
- **Monospace Value (Price / Qty):** 16sp / Medium / `JetBrains Mono`
- **Monospace Small (Barcode / SKU):** 12sp / Normal / `JetBrains Mono`

---

## 4. Component Stylings & Behaviors

### 4.1 Buttons
- **Primary Action (Checkout, Add Sale, Confirm):** Solid Electric Emerald (`#10B981`) fill, Off-White bold text. Tactile push feedback: transforms `-1px` on the Y-axis when pressed. Zero outer glowing or neon shadows.
- **Secondary / Ghost Action (Cancel, Filter, Back):** Zinc Metal (`#27272A`) surface with 1px Slate Divider (`#3F3F46`) border.
- **Destructive Action (Delete, Reject AI Proposal):** Transparent background with 1px Signal Crimson border and text (`#EF4444`).

### 4.2 Cards & Containers
- Border-defined rather than shadow-defined. All cards feature a `1px` stroke using Slate Divider (`#3F3F46`) and an 8dp corner radius.
- Shadow elevation is restrained (maximum 2dp blurred ambient shadow tinted to `#000000` at 20% opacity).
- High-density list items (such as POS bill rows or inventory lines) discard full card wrappings and use single horizontal 1px border-top dividers.

### 4.3 Form Inputs & Quantity Steppers
- Label positioned rigidly above the input field with 4dp spacing.
- Input container height: 48dp minimum for rapid thumb tapping.
- Quantity Stepper: Split button design with `[-]` button (left), Monospace quantity counter (center), and `[+]` button (right). Minimum touch target: 44dp x 44dp.

### 4.4 AI Suggestion Badges & Confirmation Cards
- All suggestions carry an explicit **Provenance Tag**:
  - `[Rule-Based Heuristic]` (Gray/Slate)
  - `[Verified Market Feed - Source: RetailNet, 12 Oct]` (Precision Azure)
  - `[AI Model Proposal]` (Emerald Muted)
- Confirmation Action Bar: Every suggestion card contains three distinct horizontal actions:
  - `[ Accept ]` (Emerald background)
  - `[ Edit Details ]` (Zinc Metal background)
  - `[ Reject ]` (Transparent with Red outline)

### 4.5 Loading, Empty, and Error States
- **Skeletal Loaders:** Exact dimensional placeholders rendered in pulsating `#27272A` with a subtle shimmer gradient. Never use circular spinning wheels for full-page loads.
- **Empty States:** Composed schematic illustrations showing step-by-step guidance on how to populate data (e.g., "Scan your first barcode or tap Add Product to initialize inventory").
- **Sync Status Pill:** Persistent top-bar pill indicator: Green dot for `Synced`, Amber dot with count for `Syncing (4 pending)`, Red warning icon for `Offline (Local Safe)`.

---

## 5. Navigation & Layout Principles

### 5.1 Shell Structure
1. **Top Application Bar (48dp):** Shop Name selector dropdown, Current Store Location, Sync Pill Indicator, Language Toggle, and Staff Avatar.
2. **Main Content Viewport:** Scrollable or locked-viewport dual-pane layout depending on device orientation and screen width.
3. **Floating AI Assistant (FAB):** Persistent 56dp circular floating button docked at the bottom-right corner with a waveform icon. Tapping opens the interactive conversational bottom sheet or voice listening modal.
4. **Bottom Navigation Bar (56dp):** 4 primary tabs:
   - Tab 1: **Sales & Entry** (POS Icon)
   - Tab 2: **Analytics** (Graph Icon)
   - Tab 3: **AI Insights** (Compass / Target Icon)
   - Tab 4: **Marketing** (Palette / Megaphone Icon)
   - *Extra Tools & Settings* accessible via Top-Bar Drawer or dedicated Action in Sales & Entry.

### 5.2 Responsive & Dual-Pane Strategy
- **Phones (< 600dp width):** Single-column stacked layout. Bottom navigation pinned. Modal bottom sheets for quick actions.
- **Tablets & POS Terminals (>= 600dp width):** Split screen POS layout: Left pane displays product catalog, category tabs, and barcode camera preview; Right pane displays the live checkout bill, quantity steppers, discount calculator, and Payment CTA.

---

## 6. Blueprint for All 20 Application Screens

### Screen 1: Login / Register
- **Header:** StoreVisionPro wordmark with sub-headline: "Retail Intelligence & Operations".
- **Form Elements:** Clean phone number / email field, OTP verification input box with 6 discrete cells, shop name text field, shop type dropdown (Grocery, Apparel, Electronics, Cosmetics, Cafe/Restaurant, General Store).
- **Auth CTAs:** "Send OTP", "Continue with Google" button, Biometric Unlock button ("Unlock with Fingerprint/Face").
- **Language Selector Link:** Discreet globe icon at the top corner.

### Screen 2: Language Selector
- **Layout:** High-density 2-column grid of language cards.
- **Items:** English, Hindi (हिन्दी), Marathi (मराठी), Gujarati (ગુજરાતી), Tamil (தமிழ்), Telugu (తెలుగు), Bengali (বাংলা), Kannada (ಕನ್ನಡ).
- **Behavior:** Immediate preview of interface labels upon selection, with a "Confirm Language" primary button.

### Screen 3: Home Dashboard
- **Top Row (4-Stat Hero Bento):**
  - Card 1: Today's Revenue (`₹14,250.00` in large Monospace) with percentage comparison to yesterday.
  - Card 2: Footfall / Customer Count (`84 Customers`).
  - Card 3: Top Selling Item (`Amul Butter 500g - 28 units`).
  - Card 4: Net Profit Estimate (`₹3,410.00`).
- **Quick Action Bar:** Direct large buttons for `[ + New Sale ]`, `[ + Add Product ]`, `[ Expense Log ]`, `[ Scan Barcode ]`.
- **Alert Banner:** Dynamic card highlighting actionable alerts (e.g., "3 products near expiry" or "Pre-weekend stock warning").

### Screen 4: Product Management
- **Top Filter:** Search bar by SKU/name, Category horizontal pills, Barcode quick-scan icon.
- **Product List Items:** 3-row compact items: Left product thumbnail, center name and category, right price and remaining stock pill (Color-coded: Green > threshold, Amber <= threshold, Red = 0).
- **Floating Ingestion Action:** Barcode camera scan or AI Camera Photo Ingestion ("Take photo of product shelf/packaging").
- **Batch Details Drawer:** Tapping a product slides up batch records with expiry dates, purchase costs, and supplier info.

### Screen 5: Sales Entry (POS Checkout)
- **Top Bar:** Date-time picker (auto-filled with current timestamp, editable by authorized staff), Customer Count counter stepper (defaults to 1), "Visited, No Purchase" toggle button.
- **Product Selector:** Instant predictive search bar and barcode scan button.
- **Live Bill Table:** High-density item rows: Name, Unit Price, Stepper (`[-] 2 [+]`), Line Total (`₹120.00`), Remove button.
- **Bill Summary Box (Bottom Pinned):** Subtotal, Tax / GST toggle, Discount field, **Grand Total** (`₹480.00`), and full-width Emerald CTA `[ Complete Sale & Print Bill ]`.

### Screen 6: Calendar View
- **View:** Month calendar grid with day cells showing mini color-coded revenue dots.
- **Selection Panel:** Tapping any date opens a day summary: Total Revenue, Total Bills, Footfall, and an expandable list of individual sales transactions with timestamps.

### Screen 7: Analytics Dashboard
- **Scope Toggle:** Horizontal segment bar (`Daily | Weekly | Monthly | Yearly`).
- **Income Line Chart:** Smooth animated revenue vs expense trend with pinch-to-zoom.
- **Hourly Customer Heatmap:** 24-hour horizontal grid showing peak customer density blocks.
- **Product Leaderboard:** Split view: Top 10 Bestsellers vs Least-Sold Products.
- **Dead Stock Alert Section:** List of items with zero sales in > 14 days.
- **Export CTA:** "Export PDF Report" button generating a print-ready document.

### Screen 8: AI Insights
- **Heuristic Cards Feed:**
  - Card: "Dynamic Pricing Tip: Milk demand increased 25% on weekends. Consider adjusting price."
  - Card: "Reorder Forecast: Sugar 1kg stock will deplete in 3 days. Recommended order: 50kg."
  - Card: "Bundle Opportunity: Customers buying Bread frequently buy Butter. Create combo for ₹5 discount."
- **Controls:** Every card features `[ Apply ]`, `[ Modify ]`, `[ Dismiss ]`.

### Screen 9: Business Health Check
- **Score Meter:** Radial health gauge (0-100) summarizing shop performance.
- **Diagnostic Breakdown:**
  - Profit Margin Health (Current 18.2% vs Industry Standard 22%).
  - Customer Retention Rate (34% repeat visitors).
  - Dead Stock Capital Lockup (`₹18,400 locked in non-moving stock`).
- **Strategic Plan Suggestions:** Step-by-step action items with estimated financial impacts.

### Screen 10: AI Chatbot & Voice Assistant
- **Interface:** Clean chat bubble stream with persistent bottom voice waveform / mic button.
- **Assistant Responses:** Direct tabular replies to natural language queries ("Stock of Oreo: 18 packets in shelf, 40 in backroom").
- **Confirmation Cards:** Interactive embedded cards for voice-logged updates:
  - "Detected Stock Receipt: 40 packets Oreo from Supplier ABC for ₹1,200. Confirm entry?" with `[ Confirm & Log ]` and `[ Edit ]`.

### Screen 11: AI Marketing Tools
- **Hub Layout:**
  - Section 1: Template Poster Creator (Today's Special, Weekend Discount, Festival wishes).
  - Section 2: AI Social Media Copywriter (WhatsApp broadcast messages, Instagram captions with hashtags).
  - Section 3: Voice-to-Ad Concept Builder.
  - Section 4: AI Product Photography Enhancer.

### Screen 12: Extra Tools
- **Menu Grid:**
  - GST Invoice Settings & PDF History
  - Customer Loyalty QR & Reward Ledger
  - Cashbook & UPI Reconciliation Engine
  - Multi-Shop Branch Switcher
  - Staff Roles & Permissions Manager
  - Supplier Catalog & Payables Ledger
  - Hospitality QR Menu Setup (Conditional)

### Screen 13: Sync Status
- **Metric Header:** Total Synced Records, Pending Changes count, Last Sync timestamp.
- **Sync Queue Inspector:** List of pending or failed queue mutations with retry buttons.
- **Conflict Resolution Modal:** Side-by-side comparison (Local Value vs Cloud Value) with "Keep Local", "Keep Cloud", or "Merge".

### Screen 14: Trending Products
- **Market Intelligence Feed:** Filterable by Shop Category (Grocery, Mobile, Apparel).
- **Cards:** Trending item name, observed regional demand surge percentage, verified data source tag (e.g., "Source: National Retail Feed - Updated 10 Oct"), and a `[ Consider Stocking ]` action.

### Screen 15: Price Comparison
- **Comparison Table:** Shop's Selling Price placed directly against verified distributor and competitor listings.
- **Data Integrity:** Explicit source citation, retrieval timestamp, like-for-like SKU verification tag, and variance margin percentage.

### Screen 16: Expiry Alerts
- **Warning Window List:** Products grouped by urgency:
  - Critical (< 7 Days Remaining): Red badge, remaining quantity, batch number.
  - Warning (7 to 30 Days Remaining): Amber badge.
- **Action Buttons:** `[ Create Clearance Discount ]`, `[ Generate Near-Expiry Bundle ]`.

### Screen 17: Branding Kit Onboarding
- **Step 1:** "Do you have an existing shop logo?" (`[ Yes, Upload Image ]` / `[ No, Create Guided Logo ]`).
- **Guided Wizard:** 4 visual questions: Brand Archetype, Color Tone Preferences, Symbolism/Icons, Typography Script.
- **Result:** Palette extraction tokens, persistent vector logo preview, and approval lock button.

### Screen 18: Campaign Builder
- **Workflow:** Select Campaign Trigger (from Approved Insight, Near-Expiry Stock, or Custom Voice Prompt).
- **Preview Canvas:** Real-time composite preview of generated poster, editable caption box, and platform sharing buttons (WhatsApp Direct, Instagram Direct, Download Image).

### Screen 19: Hospitality Menu & Orders (Admin)
- **Menu Editor:** Categories (Starters, Mains, Beverages), item prices, availability toggles.
- **Live Order Board:** Real-time KDS column cards (New Orders -> In Kitchen -> Ready -> Completed).

### Screen 20: Customer QR Menu (Customer-Facing Web / App)
- **Header:** Shop Name, Table Number indicator.
- **Menu Catalog:** Clean mobile-optimized digital menu with search and dietary badges.
- **Order Cart:** Floating bottom bar with item counter, order summary modal, and "Submit Order" button.

---

## 7. Strict Anti-Patterns & AI Tells (Enforced Prohibitions)
To ensure the interface feels like an enterprise-grade industrial software tool rather than a toy, the following patterns are strictly banned:

1. **No Emojis Anywhere:** Use official Material Symbols / Lucide vector icons exclusively. No emojis in buttons, titles, tables, or AI suggestions.
2. **No `Inter` Font:** Use `Cabinet Grotesk` for display and `Geist` for body to avoid generic default appearances.
3. **No Pure Black (`#000000`):** Use Slate Dark (`#09090B`) or Zinc Charcoal (`#18181B`).
4. **No AI Neon Glows:** No purple outer button glows, electric cyan radial background blurs, or AI cliché sparkles.
5. **No 3-Equal Cards in a Row:** Use asymmetric 2-column bento grids, split comparison tables, or compact vertical list tiles.
6. **No Cliché Copywriting:** Words such as *"Elevate"*, *"Seamless"*, *"Unleash"*, *"Next-Gen"*, or *"Magical"* are hard-banned from all UI copy and generated templates.
7. **No Auto-Applied Modifications:** AI suggestions must never automatically mutate inventory counts, prices, or push marketing posts without user approval.
