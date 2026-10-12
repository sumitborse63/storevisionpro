package com.sktech.storevisionpro.ui.theme

import androidx.compose.ui.graphics.Color

// StoreVisionPro Semantic Design System — Calibrated Industrial Palette
// Neutral Canvas & Surfaces (Zero pure black #000000)
val SlateDark = Color(0xFF09090B)       // Root window background
val ZincCharcoal = Color(0xFF18181B)    // Cards, list containers, modals
val ZincMetal = Color(0xFF27272A)       // Elevated inputs, active chip surfaces
val SlateDivider = Color(0xFF3F3F46)    // 1px structural borders, row dividers

// Text & Content Hierarchy
val OffWhitePearl = Color(0xFFF4F4F5)   // Primary headlines, totals, barcode data
val MutedSilver = Color(0xFFA1A1AA)     // Subtitles, metadata, timestamps, units
val DimGhost = Color(0xFF71717A)        // Placeholder text, disabled labels

// Singular Primary Functional Accent (Saturation < 80%, zero neon glows)
val ElectricEmerald = Color(0xFF10B981) // Checkout CTA, confirm actions, positive deltas
val EmeraldMuted = Color(0xFF064E3B)    // Active tab background, selected chip fill
val EmeraldBorder = Color(0xFF059669)   // Active focus borders

// Semantic Alerts & Provenance Indicators
val WarningAmber = Color(0xFFF59E0B)    // Low-stock threshold, near-expiry alerts
val AmberMuted = Color(0xFF451A03)      // Warning badge surface
val SignalCrimson = Color(0xFFEF4444)   // Dead stock, out of stock, reject actions
val CrimsonMuted = Color(0xFF450A0A)    // Danger badge surface
val PrecisionAzure = Color(0xFF38BDF8)  // Verified market feeds, source citations
val AzureMuted = Color(0xFF082F49)      // Intelligence chip fill

// Light Theme Neutrals (for optional daylight mode)
val CanvasLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceElevatedLight = Color(0xFFF1F5F9)
val DividerLight = Color(0xFFE2E8F0)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)