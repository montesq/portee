package com.portee.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.portee.app.ui.icons.PorteeIcons
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType

enum class RootTab { Library, Add, Suggestions }

@Composable
fun PorteeTabBar(selected: RootTab, onSelect: (RootTab) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = PorteeColors.divider, thickness = 1.dp)
        Row(modifier = Modifier.fillMaxWidth().background(PorteeColors.surface)) {
            TabItem(
                label = "Bibliothèque",
                icon = PorteeIcons.Folder,
                active = selected == RootTab.Library,
                onClick = { onSelect(RootTab.Library) },
                modifier = Modifier.weight(1f),
            )
            TabItem(
                label = "Ajouter",
                icon = null,
                active = selected == RootTab.Add,
                onClick = { onSelect(RootTab.Add) },
                modifier = Modifier.weight(1f),
            )
            TabItem(
                label = "Suggestions",
                icon = PorteeIcons.Sparkle,
                active = selected == RootTab.Suggestions,
                onClick = { onSelect(RootTab.Suggestions) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TabItem(
    label: String,
    icon: ImageVector?,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (active) PorteeColors.accent else PorteeColors.text.copy(alpha = 0.55f)
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(top = 10.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        } else {
            PlusGlyph(color = color)
        }
        Text(label, style = PorteeType.label, color = color, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun PlusGlyph(color: androidx.compose.ui.graphics.Color) {
    Box(modifier = Modifier.size(18.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 18.dp, height = 2.dp)
                .background(color),
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 2.dp, height = 18.dp)
                .background(color),
        )
    }
}
