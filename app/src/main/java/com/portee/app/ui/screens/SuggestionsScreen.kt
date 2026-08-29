package com.portee.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.portee.app.data.Suggestion
import com.portee.app.ui.components.LevelTag
import com.portee.app.ui.components.PrimaryButton
import com.portee.app.ui.components.SecondaryButton
import com.portee.app.ui.components.TagStyle
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Radius
import com.portee.app.ui.theme.Spacing

@Composable
fun SuggestionsScreen(
    suggestions: List<Suggestion>,
    addedIds: Set<String>,
    onAdd: (Suggestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.space4),
    ) {
        items(suggestions, key = { it.id }) { suggestion ->
            SuggestionCard(
                suggestion = suggestion,
                added = suggestion.id in addedIds,
                onAdd = { onAdd(suggestion) },
            )
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: Suggestion, added: Boolean, onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(PorteeColors.surface)
            .padding(Spacing.space4),
    ) {
        Text(
            suggestion.reason.uppercase(),
            style = PorteeType.kicker,
            color = PorteeColors.accent300,
        )
        Text(
            suggestion.title,
            style = PorteeType.dialogTitle,
            color = PorteeColors.text,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            suggestion.composer,
            style = PorteeType.bodySmall,
            color = PorteeColors.text.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 2.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LevelTag(suggestion.level, style = TagStyle.Neutral)
            if (added) {
                SecondaryButton(text = "Ajouté ✓", onClick = {})
            } else {
                PrimaryButton(text = "Ajouter", onClick = onAdd)
            }
        }
    }
}
