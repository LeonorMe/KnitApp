package com.malha.app.feature.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.app.appContainer
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {
    val patterns: StateFlow<List<Pattern>> = appContainer.patternRepository
        .observePatterns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createPost(patternId: String?, imageUri: String, description: String, status: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            appContainer.socialRepository.createPost(patternId, imageUri, description, status)
            onComplete()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onBack: () -> Unit,
    viewModel: CreatePostViewModel = viewModel()
) {
    val patterns by viewModel.patterns.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedPatternId by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("ongoing") }
    var isSaving by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(
                        enabled = imageUri != null && description.isNotBlank() && !isSaving,
                        onClick = {
                            isSaving = true
                            viewModel.createPost(selectedPatternId, imageUri!!.toString(), description, status) {
                                onBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Selection
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                onClick = { photoPickerLauncher.launch("image/*") }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (imageUri != null) {
                        ImagePlaceholder(label = "Photo", imageUri = imageUri!!.toString())
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("Select a photo of your work")
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Pattern Link
            Text("Link a Pattern (Optional)", style = MaterialTheme.typography.titleMedium)
            ScrollableRow {
                patterns.forEach { pattern ->
                    FilterChip(
                        selected = selectedPatternId == pattern.id,
                        onClick = { selectedPatternId = if (selectedPatternId == pattern.id) null else pattern.id },
                        label = { Text(pattern.title) }
                    )
                }
            }

            // Status
            Text("Project Status", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = status == "ongoing",
                    onClick = { status = "ongoing" },
                    label = { Text("Ongoing") }
                )
                FilterChip(
                    selected = status == "finished",
                    onClick = { status = "finished" },
                    label = { Text("Finished") }
                )
            }
        }
    }
}

@Composable
private fun ScrollableRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().androidx.compose.foundation.horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}
