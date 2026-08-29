package com.portee.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Radius

// Repeating 45deg stripes, matching the design's
// `repeating-linear-gradient(135deg, surface 0 14px, neutral-800 14px 28px)` placeholder.
@Composable
fun DiagonalStripes(modifier: Modifier = Modifier) {
    val stripeWidthDp = 14.dp
    Canvas(modifier = modifier.background(PorteeColors.surface)) {
        val stripePx = stripeWidthDp.toPx()
        val period = stripePx * 2
        val span = size.width + size.height
        val count = (span / period).toInt() + 2
        for (i in -count..count) {
            val x0 = i * period
            val path = Path().apply {
                moveTo(x0, 0f)
                lineTo(x0 + stripePx, 0f)
                lineTo(x0 + stripePx + size.height, size.height)
                lineTo(x0 + size.height, size.height)
                close()
            }
            drawPath(path, color = PorteeColors.neutral800)
        }
    }
}

@Composable
fun ScorePlaceholder(pages: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.md))
            .border(1.dp, PorteeColors.divider, RoundedCornerShape(Radius.md)),
        contentAlignment = Alignment.Center,
    ) {
        DiagonalStripes(modifier = Modifier.fillMaxSize())
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "PARTITION",
                style = PorteeType.mono.copy(fontSize = 11.sp, letterSpacing = 0.6.sp),
                color = PorteeColors.text.copy(alpha = 0.65f),
            )
            Text(
                "$pages page${if (pages > 1) "s" else ""}",
                style = PorteeType.mono,
                color = PorteeColors.text.copy(alpha = 0.85f),
            )
        }
    }
}
