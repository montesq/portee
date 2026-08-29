package com.portee.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Radius

enum class TagStyle { Accent, Neutral }

@Composable
fun LevelTag(level: Int, modifier: Modifier = Modifier, style: TagStyle = TagStyle.Accent) {
    val (bg, fg) = when (style) {
        TagStyle.Accent -> PorteeColors.accent800 to PorteeColors.accent100
        TagStyle.Neutral -> PorteeColors.neutral800 to PorteeColors.neutral100
    }
    Text(
        text = "Niveau $level/5",
        style = PorteeType.meta,
        color = fg,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}
