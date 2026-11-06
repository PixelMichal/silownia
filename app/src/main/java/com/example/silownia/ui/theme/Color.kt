package com.example.silownia.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val LightBg = Color(0xFFE8EBF1)
val LightCard = Color(0xFFF7F8FB)
val LightInk = Color(0xFF17203A)
val LightMut = Color(0xFF5C6580)
val LightLine = Color(0xFFCBD0DE)
val LightAcc = Color(0xFFE8480C)
val LightAccInk = Color(0xFFFFFFFF)
val LightOk = Color(0xFF1F7A4D)

val DarkBg = Color(0xFF10131C)
val DarkCard = Color(0xFF191E2B)
val DarkInk = Color(0xFFEAEDF6)
val DarkMut = Color(0xFF9099B3)
val DarkLine = Color(0xFF2B3245)
val DarkAcc = Color(0xFFFF6B35)
val DarkAccInk = Color(0xFF10131C)
val DarkOk = Color(0xFF4CC38A)

@Immutable
data class AppPalette(
    val bg: Color,
    val card: Color,
    val ink: Color,
    val mut: Color,
    val line: Color,
    val acc: Color,
    val accInk: Color,
    val ok: Color
)

val LightPalette = AppPalette(
    bg = LightBg,
    card = LightCard,
    ink = LightInk,
    mut = LightMut,
    line = LightLine,
    acc = LightAcc,
    accInk = LightAccInk,
    ok = LightOk
)

val DarkPalette = AppPalette(
    bg = DarkBg,
    card = DarkCard,
    ink = DarkInk,
    mut = DarkMut,
    line = DarkLine,
    acc = DarkAcc,
    accInk = DarkAccInk,
    ok = DarkOk
)
