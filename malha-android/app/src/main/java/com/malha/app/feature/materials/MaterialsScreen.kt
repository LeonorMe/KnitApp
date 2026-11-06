package com.malha.app.feature.materials

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import com.malha.app.core.util.ImageStorageHelper
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.share.MalhaShare
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import com.malha.app.domain.model.Pattern
import com.malha.app.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsScreen(
    onNavigateToPattern: (String) -> Unit,
    viewModel: MaterialsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var materialToEdit by remember { mutableStateOf<Material?>(null) }
    var materialToDelete by remember { mutableStateOf<Material?>(null) }

    data class MaterialTab(
        val label: String,
        val iconRes: Int
    )

    val tabs = listOf(
        MaterialTab(stringResource(R.string.patterns), R.drawable.ic_pattern),
        MaterialTab(stringResource(R.string.yarn), R.drawable.ic_yarn_ball),
        MaterialTab(stringResource(R.string.needles), R.drawable.ic_needles),
        MaterialTab(stringResource(R.string.useful_tools), R.drawable.ic_content_cut),
        //MaterialTab(stringResource(R.string.wish_list), R.drawable.ic_materials), // TODO
        //MaterialTab(stringResource(R.string.store), R.drawable.ic_project) // TODO
    )

    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.my_materials),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(onClick = { /* navigate to wishlist todo */ }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_shopping_bag),
                                    contentDescription = stringResource(R.string.wish_list),
                                    tint = colorScheme.onSecondaryContainer
                                )
                            }

                            IconButton(onClick = { /* navigate to store todo */ }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_storefront),
                                    contentDescription = stringResource(R.string.store),
                                    tint = colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.6f))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor =  colorScheme.background
                    )
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 12.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(tabs) { index, tab ->

                        val selected = selectedTabIndex == index

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(72.dp)
                                .clickable {
                                    selectedTabIndex = index
                                }
                        ) {

                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = if (selected)
                                    colorScheme.primaryContainer
                                else
                                    colorScheme.surfaceVariant,
                                tonalElevation = if (selected) 4.dp else 0.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = tab.iconRes),
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(28.dp),
                                        tint = if (selected)
                                            colorScheme.onPrimaryContainer
                                        else
                                            colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = tab.label,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selected)
                                    colorScheme.primary
                                else
                                    colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.clearError()
                    showCreateDialog = true
                },
                containerColor = colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = colorScheme.primaryContainer,
                    contentDescription = stringResource(R.string.add_material),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnimatedVisibility(visible = uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.error
                        )
                    }
                }
                item {
                    AnimatedContent(
                        targetState = selectedTabIndex,
                        label = "materials-tab-content"
                    ) { tabIndex ->
                        val selectedType = when (tabIndex) {
                            1 -> MaterialType.Yarn
                            2 -> MaterialType.Needle
                            3 -> MaterialType.Tool
                            else -> null
                        }

                        if (tabIndex == 0){
                            PatternsCategoryCard(
                                patterns = uiState.patterns,
                                onNavigateToPattern = onNavigateToPattern
                            )
                        } else if (selectedType != null) {
                            MaterialCategoryCard(
                                type = selectedType,
                                materials = uiState.materials.filter { it.type == selectedType },
                                isLoading = uiState.isLoading,
                                onEdit = { materialToEdit = it },
                                onDelete = { materialToDelete = it }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                Text(
                                    text = stringResource(R.string.coming_soon, tabs[tabIndex]),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
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

    materialToEdit?.let { material ->
        EditMaterialDialog(
            material = material,
            isSaving = uiState.isCreating,
            onDismiss = { materialToEdit = null },
            onSave = { name, type, quantity, unit, imageUri ->
                viewModel.updateMaterial(
                    id = material.id,
                    name = name,
                    type = type,
                    quantityText = quantity,
                    unit = unit,
                    imageUri = imageUri
                )
                materialToEdit = null
            }
        )
    }

    materialToDelete?.let { material ->
        AlertDialog(
            onDismissRequest = { materialToDelete = null },
            title = { Text(stringResource(R.string.delete_material_dialog_title)) },
            text = {
                Text(stringResource(R.string.delete_material_dialog_message, material.name))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMaterial(material.id)
                        materialToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { materialToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun MaterialCategoryCard(
    type: MaterialType,
    materials: List<Material>,
    isLoading: Boolean,
    onEdit: (Material) -> Unit,
    onDelete: (Material) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = type.label,
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )

            if (isLoading) {
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            } else if (materials.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_yet, type.label.lowercase()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            } else {
                materials.forEach { material ->
                    MaterialRow(
                        material = material,
                        onEdit = { onEdit(material) },
                        onDelete = { onDelete(material) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MaterialRow(
    material: Material,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        ImagePlaceholder(label = material.name, imageUri = material.imageUri)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = material.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                text = "${material.quantity} ${material.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.edit_material)) },
                    onClick = {
                        showMenu = false
                        onEdit()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.share_material)) },
                    onClick = {
                        showMenu = false
                        MalhaShare.shareMaterial(context, material)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.delete_material),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CreateMaterialDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, MaterialType, String, String, String?) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialType.Yarn) }
    var unit by remember(selectedType) { mutableStateOf(defaultUnitFor(selectedType)) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val persistent = ImageStorageHelper.saveImageToInternalStorage(context, uri) ?: uri.toString()
                imageUri = Uri.parse(persistent)
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_material)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
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
                        ImagePlaceholder(label = stringResource(R.string.photo), imageUri = imageUri?.toString())
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
                            Text(stringResource(R.string.gallery))
                        }
                        // Note: Camera launcher would need a File URI provider, skipping for now to keep it simple
                        // but adding a stub button for UI completeness
                        OutlinedButton(
                            onClick = { /* Camera stub */ }, // todo
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.camera))
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        localError = null
                    },
                    label = { Text(stringResource(R.string.material_name)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                        localError = null
                    },
                    label = { Text(stringResource(R.string.quantity)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text(stringResource(R.string.unit)) },
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
            val materialNameRequired = stringResource(R.string.material_name_is_required)
            val quantityMustBeNumber = stringResource(R.string.quantity_must_be_a_number)
            Button(
                enabled = !isCreating,
                onClick = {
                    localError = when {
                        name.isBlank() -> materialNameRequired
                        quantity.replace(',', '.').toDoubleOrNull() == null -> quantityMustBeNumber
                        else -> null
                    }
                    if (localError == null) {
                        onCreate(name, selectedType, quantity, unit, imageUri?.toString())
                    }
                }
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isCreating,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun EditMaterialDialog(
    material: Material,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, MaterialType, String, String, String?) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(material.name) }
    var quantity by remember { mutableStateOf(material.quantity.toString().removeSuffix(".0")) }
    var selectedType by remember { mutableStateOf(material.type) }
    var unit by remember(selectedType) { mutableStateOf(material.unit) }
    var imageUri by remember { mutableStateOf(material.imageUri?.let { Uri.parse(it) }) }
    var localError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val persistent = ImageStorageHelper.saveImageToInternalStorage(context, uri) ?: uri.toString()
                imageUri = Uri.parse(persistent)
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_material)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MaterialType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (unit.isBlank() || unit == defaultUnitFor(selectedType)) {
                                    unit = defaultUnitFor(type)
                                }
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
                        ImagePlaceholder(label = stringResource(R.string.photo), imageUri = imageUri?.toString())
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
                            Text(stringResource(R.string.gallery))
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        localError = null
                    },
                    label = { Text(stringResource(R.string.material_name)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                        localError = null
                    },
                    label = { Text(stringResource(R.string.quantity)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text(stringResource(R.string.unit)) },
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
            val materialNameRequired = stringResource(R.string.material_name_is_required)
            val quantityMustBeNumber = stringResource(R.string.quantity_must_be_a_number)
            Button(
                enabled = !isSaving,
                onClick = {
                    localError = when {
                        name.isBlank() -> materialNameRequired
                        quantity.replace(',', '.').toDoubleOrNull() == null -> quantityMustBeNumber
                        else -> null
                    }
                    if (localError == null) {
                        onSave(name, selectedType, quantity, unit, imageUri?.toString())
                    }
                }
            ) {
                Text(stringResource(R.string.save_action))
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isSaving,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun defaultUnitFor(type: MaterialType): String {
    return when (type) {
        MaterialType.Pattern -> "patterns"
        MaterialType.Yarn -> "skeins"
        MaterialType.Needle -> "pairs"
        MaterialType.Hook -> "hooks"
        MaterialType.Tool -> "items"
        else -> "units"
    }
}

@Composable
private fun PatternsCategoryCard(
    patterns: List<Pattern>,
    onNavigateToPattern: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.patterns),
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurface
            )

            if (patterns.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_patterns_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            } else {
                patterns.forEach { pattern ->
                    PatternRow(pattern = pattern, onClick = { onNavigateToPattern(pattern.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatternRow(pattern: Pattern, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pattern.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Designer: ${pattern.designer ?: "Unknown"} • ${pattern.craft.name.lowercase().capitalize()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${pattern.allSteps.size} steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.primary
                )
            }
        }
    }
}
