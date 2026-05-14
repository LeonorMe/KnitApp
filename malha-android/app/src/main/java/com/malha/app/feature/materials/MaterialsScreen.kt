package com.malha.app.feature.materials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType

@Composable
fun MaterialsScreen(viewModel: MaterialsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Materials",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            item {
                Text(
                    text = "Track yarn, needles, hooks, purchases, and project assignments.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (uiState.errorMessage != null) {
                item {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        viewModel.clearError()
                        showCreateDialog = true
                    },
                    enabled = !uiState.isCreating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add material")
                }
            }

            MaterialType.entries.forEach { type ->
                val materials = uiState.materials.filter { it.type == type }
                item {
                    MaterialCategoryCard(
                        type = type,
                        materials = materials,
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateMaterialDialog(
            isCreating = uiState.isCreating,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, type, quantity, unit ->
                viewModel.createMaterial(name, type, quantity, unit)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun MaterialCategoryCard(
    type: MaterialType,
    materials: List<Material>,
    isLoading: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = type.label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isLoading) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (materials.isEmpty()) {
                Text(
                    text = "No ${type.label.lowercase()} yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                materials.forEach { material ->
                    MaterialRow(material = material)
                }
            }
        }
    }
}

@Composable
private fun MaterialRow(material: Material) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ImagePlaceholder(label = material.name, imageUri = material.imageUri)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = material.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${material.quantity} ${material.unit}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (material.imageUri == null) "No image added" else "Image attached",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CreateMaterialDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, MaterialType, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialType.Yarn) }
    var unit by remember(selectedType) { mutableStateOf(defaultUnitFor(selectedType)) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New material") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MaterialType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                unit = defaultUnitFor(type)
                            },
                            label = { Text(type.label) }
                        )
                    }
                }
                ImagePlaceholder(label = "Photo")
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        localError = null
                    },
                    label = { Text("Material name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                        localError = null
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    supportingText = {
                        if (localError != null) {
                            Text(localError.orEmpty())
                        } else {
                            Text("Image upload will be connected in the next media step.")
                        }
                    },
                    isError = localError != null,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isCreating,
                onClick = {
                    localError = when {
                        name.isBlank() -> "Material name is required."
                        quantity.replace(',', '.').toDoubleOrNull() == null -> "Quantity must be a number."
                        else -> null
                    }
                    if (localError == null) {
                        onCreate(name, selectedType, quantity, unit)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(
                enabled = !isCreating,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun defaultUnitFor(type: MaterialType): String {
    return when (type) {
        MaterialType.Yarn -> "skeins"
        MaterialType.Needle -> "pairs"
        MaterialType.Hook -> "hooks"
        MaterialType.Accessory -> "items"
    }
}
