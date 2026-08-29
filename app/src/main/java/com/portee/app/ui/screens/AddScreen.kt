package com.portee.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.portee.app.data.AddForm
import com.portee.app.data.ImportKind
import com.portee.app.ui.components.ImportChoiceButton
import com.portee.app.ui.components.PorteeTextField
import com.portee.app.ui.components.PrimaryButton
import com.portee.app.ui.icons.PorteeIcons
import com.portee.app.ui.theme.PorteeColors
import com.portee.app.ui.theme.PorteeType
import com.portee.app.ui.theme.Radius
import com.portee.app.ui.theme.Spacing

@Composable
fun AddScreen(
    form: AddForm,
    onTitleChange: (String) -> Unit,
    onComposerChange: (String) -> Unit,
    onPickImport: (ImportKind) -> Unit,
    onPickLevel: (Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canSubmit = form.title.isNotBlank() && form.composer.isNotBlank() && form.importKind != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.space6),
    ) {
        Column {
            Text(
                "Importer la partition",
                style = PorteeType.meta,
                color = PorteeColors.text.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.space3)) {
                ImportChoiceButton(
                    label = "PDF",
                    icon = PorteeIcons.Folder,
                    selected = form.importKind == ImportKind.PDF,
                    onClick = { onPickImport(ImportKind.PDF) },
                    modifier = Modifier.weight(1f),
                )
                ImportChoiceButton(
                    label = "Photo",
                    icon = PorteeIcons.Image,
                    selected = form.importKind == ImportKind.PHOTO,
                    onClick = { onPickImport(ImportKind.PHOTO) },
                    modifier = Modifier.weight(1f),
                )
            }
            if (form.importKind != null) {
                Text(
                    "Importé · ${form.importName}",
                    style = PorteeType.meta,
                    color = PorteeColors.accent300,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        FormField(label = "Titre du morceau") {
            PorteeTextField(value = form.title, onValueChange = onTitleChange, placeholder = "Ex. Clair de lune")
        }
        FormField(label = "Compositeur") {
            PorteeTextField(value = form.composer, onValueChange = onComposerChange, placeholder = "Ex. Claude Debussy")
        }
        FormField(label = "Niveau de difficulté") {
            LevelSegmentedControl(level = form.level, onPick = onPickLevel)
        }

        PrimaryButton(
            text = "Ajouter à ma bibliothèque",
            onClick = onSubmit,
            enabled = canSubmit,
            fullWidth = true,
        )
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column {
        Text(
            label,
            style = PorteeType.meta,
            color = PorteeColors.text.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun LevelSegmentedControl(level: Int, onPick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .border(1.dp, PorteeColors.divider, RoundedCornerShape(Radius.md)),
    ) {
        (1..5).forEach { n ->
            val active = n == level
            Text(
                text = n.toString(),
                style = PorteeType.bodySmall,
                color = if (active) PorteeColors.accent else PorteeColors.text.copy(alpha = 0.75f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(if (active) PorteeColors.accent800 else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onPick(n) },
                    )
                    .padding(vertical = 12.dp),
            )
        }
    }
}
