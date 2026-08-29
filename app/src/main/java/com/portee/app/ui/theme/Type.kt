package com.portee.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Inter isn't bundled in this project; the platform sans-serif (Roboto on Android)
// is used as the closest available stand-in for the Nocturne "font-body"/"font-heading".
private val bodyFont = FontFamily.SansSerif
private val headingWeight = FontWeight.Medium

object PorteeType {
    val appTitle = TextStyle(fontFamily = bodyFont, fontWeight = headingWeight, fontSize = 23.sp, letterSpacing = (-0.2).sp)
    val dialogTitle = TextStyle(fontFamily = bodyFont, fontWeight = headingWeight, fontSize = 17.sp)
    val rowTitle = TextStyle(fontFamily = bodyFont, fontWeight = headingWeight, fontSize = 16.sp, lineHeight = 20.sp)
    val body = TextStyle(fontFamily = bodyFont, fontSize = 15.sp, lineHeight = 23.sp)
    val bodySmall = TextStyle(fontFamily = bodyFont, fontSize = 13.sp)
    val meta = TextStyle(fontFamily = bodyFont, fontSize = 12.sp)
    val label = TextStyle(fontFamily = bodyFont, fontSize = 11.sp, letterSpacing = 0.9.sp)
    val kicker = TextStyle(fontFamily = bodyFont, fontSize = 10.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.SemiBold)
    val mono = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.5.sp)
}

val PorteeTypography = Typography()
