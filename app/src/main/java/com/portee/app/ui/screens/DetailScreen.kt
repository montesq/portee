package com.portee.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.portee.app.data.Piece
import com.portee.app.ui.components.GhostButton
import com.portee.app.ui.components.LevelTag
import com.portee.app.ui.components.PrimaryButton
import com.portee.app.ui.components.RecordingRow
import com.portee.app.ui.components.ScorePreview
import com.portee.app.ui.components.SecondaryButton
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun DetailScreen(
    piece: Piece,
    onPlay: () -> Unit,
    onRecord: () -> Unit,
    onSeeSuggestions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.space5),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.space2)) {
                LevelTag(piece.level)
                Text("Ajouté le ${piece.added}", style = PorteeType.label, color = PorteeColors.text.copy(alpha = 0.5f))
            }
            Text(
                piece.composer,
                style = PorteeType.body,
                color = PorteeColors.text.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        ScorePreview(
            imageUris = piece.scoreImageUris,
            pageIndex = 0,
            pages = piece.pages,
            modifier = Modifier.fillMaxWidth().height(170.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.space3)) {
            PrimaryButton(
                text = "Jouer",
                onClick = onPlay,
                modifier = Modifier.weight(1f),
                leading = { PlayGlyph() },
            )
            SecondaryButton(
                text = "S'enregistrer",
                onClick = onRecord,
                modifier = Modifier.weight(1f),
                leading = { RecordDot() },
            )
        }

        Column {
            Text(
                "HISTORIQUE DES INTERPRÉTATIONS",
                style = PorteeType.meta,
                color = PorteeColors.text.copy(alpha = 0.55f),
                modifier = Modifier.padding(bottom = 10.dp),
            )
            if (piece.recordings.isEmpty()) {
                Text(
                    "Aucun enregistrement pour l'instant.",
                    style = PorteeType.bodySmall,
                    color = PorteeColors.text.copy(alpha = 0.5f),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.space3)) {
                    piece.recordings.forEach { recording ->
                        RecordingRow(recording = recording)
                    }
                }
            }
        }

        GhostButton(text = "Voir des suggestions →", onClick = onSeeSuggestions)
    }
}

@Composable
private fun PlayGlyph() {
    Canvas(modifier = Modifier.size(width = 9.dp, height = 12.dp)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, size.height / 2)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color = PorteeColors.accent)
    }
}

@Composable
private fun RecordDot() {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(PorteeColors.recordRed),
    )
}
