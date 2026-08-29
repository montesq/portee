package com.portee.app.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.portee.app.camera.rememberDecodedBitmap
import com.portee.app.data.AddForm
import com.portee.app.data.ImportKind
import com.portee.app.ui.components.ImportChoiceButton
import com.portee.app.ui.components.PorteeTextField
import com.portee.app.ui.components.PrimaryButton
import com.portee.app.ui.findActivity
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
    onPickPdf: () -> Unit,
    onPhotoTaken: (String) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onPickLevel: (Int) -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier,
) {
    val canSubmit = form.title.isNotBlank() && form.composer.isNotBlank() && form.importKind != null && !isSubmitting
    val activity = LocalContext.current.findActivity()
    var scanError by remember { mutableStateOf(false) }

    val scannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanResult?.pages?.forEach { page -> onPhotoTaken(page.imageUri.toString()) }
        }
    }

    fun startScan() {
        val act = activity ?: return
        scanError = false
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(false)
            .setPageLimit(10)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(act)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { scanError = true }
    }

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
                    onClick = onPickPdf,
                    modifier = Modifier.weight(1f),
                )
                ImportChoiceButton(
                    label = "Photo",
                    icon = PorteeIcons.Image,
                    selected = form.importKind == ImportKind.PHOTO,
                    onClick = { startScan() },
                    modifier = Modifier.weight(1f),
                )
            }

            if (form.importKind == ImportKind.PDF) {
                Text(
                    "Importé · ${form.importName}",
                    style = PorteeType.meta,
                    color = PorteeColors.accent300,
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else if (form.photoUris.isNotEmpty()) {
                val count = form.photoUris.size
                Text(
                    "Importé · $count page${if (count > 1) "s" else ""} numérisée${if (count > 1) "s" else ""}",
                    style = PorteeType.meta,
                    color = PorteeColors.accent300,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            if (scanError) {
                Text(
                    "La numérisation a échoué. Réessaie.",
                    style = PorteeType.meta,
                    color = PorteeColors.recordRed,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            if (form.photoUris.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    form.photoUris.forEach { uri ->
                        PhotoThumbnail(uriString = uri, onRemove = { onRemovePhoto(uri) })
                    }
                    AddAnotherPhotoTile(onClick = { startScan() })
                }
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
            text = if (isSubmitting) "Traitement…" else "Ajouter à ma bibliothèque",
            onClick = onSubmit,
            enabled = canSubmit,
            fullWidth = true,
        )
    }
}

@Composable
private fun PhotoThumbnail(uriString: String, onRemove: () -> Unit) {
    val bitmap = rememberDecodedBitmap(uriString, 200)

    Box(modifier = Modifier.size(64.dp)) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Radius.sm)),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(PorteeColors.surface)
                    .border(1.dp, PorteeColors.divider, RoundedCornerShape(Radius.sm)),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(PorteeColors.background.copy(alpha = 0.85f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRemove,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text("×", style = PorteeType.bodySmall.copy(fontSize = 13.sp), color = PorteeColors.text)
        }
    }
}

@Composable
private fun AddAnotherPhotoTile(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(Radius.sm))
            .border(1.dp, PorteeColors.divider, RoundedCornerShape(Radius.sm))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text("+", style = PorteeType.dialogTitle, color = PorteeColors.text.copy(alpha = 0.6f))
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
