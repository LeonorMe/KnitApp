package com.malha.app.feature.aidi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.ai.AidiMessage
import com.malha.app.core.ai.AidiSender

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AidiScreen(viewModel: AidiViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var draft by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Aidi",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "AI craft assistance for planning, materials, pattern explanations, and progress decisions.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.suggestedPrompts.forEach { prompt ->
                    AssistChip(
                        onClick = { viewModel.sendMessage(prompt) },
                        label = { Text(prompt) },
                        enabled = !uiState.isThinking
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.messages) { message ->
                    AidiMessageCard(message = message)
                }
                if (uiState.isThinking) {
                    item {
                        AidiMessageCard(
                            message = AidiMessage(
                                id = "thinking",
                                sender = AidiSender.Aidi,
                                text = "Aidi is thinking..."
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    label = { Text("Ask Aidi") },
                    minLines = 1,
                    maxLines = 3,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    enabled = draft.isNotBlank() && !uiState.isThinking,
                    onClick = {
                        viewModel.sendMessage(draft)
                        draft = ""
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
private fun AidiMessageCard(message: AidiMessage) {
    val isAidi = message.sender == AidiSender.Aidi

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAidi) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isAidi) "Aidi" else "You",
                style = MaterialTheme.typography.titleLarge,
                color = if (isAidi) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isAidi) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )
        }
    }
}

