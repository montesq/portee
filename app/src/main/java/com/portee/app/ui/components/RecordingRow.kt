package com.portee.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.portee.app.data.Recording
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Radius
import com.portee.app.ui.theme.Spacing

@Composable
fun RecordingRow(recording: Recording, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(PorteeColors.surface)
            .padding(Spacing.space3),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(recording.date, style = PorteeType.bodySmall, color = PorteeColors.text.copy(alpha = 0.75f))
            Text(recording.duration, style = PorteeType.bodySmall, color = PorteeColors.text.copy(alpha = 0.55f))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PorteeColors.neutral800),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(recording.quality / 100f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(PorteeColors.accent),
                )
            }
            Text("${recording.quality}%", style = PorteeType.label, color = PorteeColors.text.copy(alpha = 0.6f))
        }
    }
}
