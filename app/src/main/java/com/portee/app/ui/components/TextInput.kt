package com.portee.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Radius

@Composable
fun PorteeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingPadding: androidx.compose.ui.unit.Dp = 14.dp,
    textStyle: TextStyle = PorteeType.bodySmall,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(PorteeColors.surface)
            .border(1.dp, PorteeColors.divider, RoundedCornerShape(Radius.md))
            .padding(horizontal = leadingPadding, vertical = 12.dp),
    ) {
        if (value.isEmpty()) {
            Text(placeholder, style = textStyle, color = PorteeColors.text.copy(alpha = 0.4f))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle.copy(color = PorteeColors.text),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(PorteeColors.accent),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
