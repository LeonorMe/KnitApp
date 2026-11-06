package com.malha.app.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.malha.app.R
import com.malha.app.core.app.appContainer
import com.malha.app.domain.pattern.Technique
import com.malha.app.domain.pattern.TechniqueAlias
import com.malha.app.domain.pattern.TechniqueSourceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniqueHelpBottomSheet(
    techniqueId: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var technique by remember { mutableStateOf<Technique?>(null) }
    var aliases by remember { mutableStateOf<List<TechniqueAlias>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(techniqueId) {
        val repo = context.appContainer.techniqueRepository
        technique = repo.getTechniqueById(techniqueId)
        aliases = repo.getAliasesForTechnique(techniqueId)
        isLoading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val tech = technique
                val title = tech?.displayName() ?: techniqueId.replace("-", " ").replaceFirstChar { it.uppercase() }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (tech != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = tech.craftType.name,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Description
                val description = tech?.customDescription
                    ?: tech?.descriptionKey?.let { key ->
                        // Attempt to read from localized string resources, fallback to default text
                        val resId = context.resources.getIdentifier(key, "string", context.packageName)
                        if (resId != 0) context.getString(resId) else null
                    }
                    ?: stringResource(R.string.technique_default_description, title)

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Aliases
                if (aliases.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.technique_aliases_label),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            aliases.take(6).forEach { alias ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(alias.displayAlias) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_got_it))
                }
            }
        }
    }
}
