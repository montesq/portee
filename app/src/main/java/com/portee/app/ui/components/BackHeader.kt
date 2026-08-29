package com.portee.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.overflow.TextOverflow
import androidx.compose.ui.unit.dp
import com.portee.app.ui.icons.PorteeIcons
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun BackHeader(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.space6, bottom = Spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconOnlyButton(icon = PorteeIcons.ArrowRight, contentDescription = "Retour", onClick = onBack)
        Text(
            title,
            style = PorteeType.dialogTitle,
            color = PorteeColors.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = Spacing.space3),
        )
    }
}
