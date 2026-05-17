package com.malha.app.core.design.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.malha.app.domain.model.Pattern

@Composable
fun YarnEstimatorCard(
    pattern: Pattern?,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Text(
                    text = "AI Yarn Estimation",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            if (pattern != null) {
                val totalStitches = pattern.allSteps.sumOf { it.stitchCountData?.toIntOrNull() ?: 0 }
                val estimatedGrams = (totalStitches * 0.015).toInt() // Dummy math
                
                Text(
                    text = "Based on the structured execution graph, you will need approximately:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$estimatedGrams grams of yarn",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Confidence Score: ${(pattern.aiConfidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                Text(
                    text = "Select or import a pattern to see yarn estimation.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
