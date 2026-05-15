package com.malha.app.feature.materials

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                    text = "Track yarn, needles, hooks, and accessories for your projects.",
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
            onCreate = { name, type, quantity, unit, imageUri ->
                viewModel.createMaterial(name, type, quantity, unit, imageUri)
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
        }
    }
}

@Composable
private fun CreateMaterialDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, MaterialType, String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialType.Yarn) }
    var unit by remember(selectedType) { mutableStateOf(defaultUnitFor(selectedType)) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New material") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        ImagePlaceholder(label = "Photo", imageUri = imageUri?.toString())
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Gallery")
                        }
                        // Note: Camera launcher would need a File URI provider, skipping for now to keep it simple
                        // but adding a stub button for UI completeness
                        OutlinedButton(
                            onClick = { /* Camera stub */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Camera")
                        }
                    }
                }

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
                        onCreate(name, selectedType, quantity, unit, imageUri?.toString())
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
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
        else -> "units"
    }
}
