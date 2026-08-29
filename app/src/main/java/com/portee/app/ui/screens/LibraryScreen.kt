package com.portee.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.portee.app.data.Piece
import com.portee.app.ui.components.LevelTag
import com.portee.app.ui.components.PorteeTextField
import com.portee.app.ui.icons.PorteeIcons
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Spacing

@Composable
fun LibraryScreen(
    pieces: List<Piece>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenPiece: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(bottom = Spacing.space4)) {
            Icon(
                PorteeIcons.Search,
                contentDescription = null,
                tint = PorteeColors.text.copy(alpha = 0.45f),
                modifier = Modifier
                    .padding(start = 11.dp)
                    .size(14.dp)
                    .align(Alignment.CenterStart),
            )
            PorteeTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = "Rechercher un morceau ou un compositeur",
                leadingPadding = 32.dp,
            )
        }

        if (pieces.isEmpty()) {
            Text(
                if (searchQuery.isBlank()) {
                    "Ta bibliothèque est vide. Ajoute ton premier morceau depuis l'onglet Ajouter."
                } else {
                    "Aucun morceau ne correspond à « $searchQuery »."
                },
                style = PorteeType.bodySmall,
                color = PorteeColors.text.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = Spacing.space3),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = PaddingValues(bottom = Spacing.space6),
            ) {
                items(pieces, key = { it.id }) { piece ->
                    TimelineRow(piece = piece, onClick = { onOpenPiece(piece.id) })
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(piece: Piece, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Box(modifier = Modifier.width(20.dp).fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(PorteeColors.divider),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 3.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(PorteeColors.accent),
            )
        }
        Column(modifier = Modifier.weight(1f).padding(start = Spacing.space4)) {
            Text(
                piece.added,
                style = PorteeType.label,
                color = PorteeColors.text.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(piece.title, style = PorteeType.rowTitle, color = PorteeColors.text)
                    Text(
                        piece.composer,
                        style = PorteeType.bodySmall,
                        color = PorteeColors.text.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 1.dp),
                    )
                    LevelTag(piece.level, modifier = Modifier.padding(top = 6.dp))
                }
                Icon(
                    PorteeIcons.ArrowRight,
                    contentDescription = null,
                    tint = PorteeColors.text.copy(alpha = 0.35f),
                    modifier = Modifier.size(14.dp),
                )
            }
            HorizontalDivider(color = PorteeColors.divider, thickness = 1.dp)
        }
    }
}
