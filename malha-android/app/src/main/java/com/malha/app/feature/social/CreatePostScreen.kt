package com.malha.app.feature.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.core.app.appContainer
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreatePostViewModel(application: android.app.Application) : AndroidViewModel(application) {
    val patterns: StateFlow<List<Pattern>> = appContainer.patternRepository
        .observePatterns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createPost(
        patternId: String?,
        imageUri: String,
        description: String,
        status: String,
        onComplete: () -> Unit) {
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
                title = { Text(stringResource(R.string.new_post)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back)) }
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
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.share))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                onClick = { photoPickerLauncher.launch("image/*") }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (imageUri != null) { // todo
                        ImagePlaceholder(label = stringResource(R.string.photo), imageUri = imageUri!!.toString())
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text(stringResource(R.string.select_a_photo_of_your_work))
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Pattern Link
            Text(stringResource(R.string.link_a_pattern_optional), style = MaterialTheme.typography.titleMedium)
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
                    label = { Text(stringResource(R.string.ongoing)) }
                )
                FilterChip(
                    selected = status == "finished",
                    onClick = { status = "finished" },
                    label = { Text(stringResource(R.string.finished)) }
                )
            }
        }
    }
}

@Composable
private fun ScrollableRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}
