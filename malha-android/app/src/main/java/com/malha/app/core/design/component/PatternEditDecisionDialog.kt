package com.malha.app.core.design.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.malha.app.R
import com.malha.app.domain.engine.PatternEditDecision

@Composable
fun PatternEditDecisionDialog(
    patternTitle: String,
    activeProjectCount: Int,
    onDecision: (PatternEditDecision) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDecision(PatternEditDecision.CANCEL) },
        title = {
            Text(
                stringResource(R.string.pattern_edit_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(R.string.pattern_edit_dialog_message),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$patternTitle ($activeProjectCount active projects)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onDecision(PatternEditDecision.CREATE_COPY) }
            ) {
                Text(stringResource(R.string.pattern_edit_copy))
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onDecision(PatternEditDecision.UPDATE_SOURCE) }
                ) {
                    Text(stringResource(R.string.pattern_edit_update))
                }
                TextButton(
                    onClick = { onDecision(PatternEditDecision.CANCEL) }
                ) {
                    Text(stringResource(R.string.pattern_edit_cancel))
                }
            }
        }
    )
}
