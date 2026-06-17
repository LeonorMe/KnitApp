package com.malha.app.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.feature.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
    onChangePhoto: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember(uiState.preferences.username) { 
        mutableStateOf(uiState.preferences.username ?: "") 
    }
    var bio by remember(uiState.preferences.bio) { 
        mutableStateOf(uiState.preferences.bio ?: "") 
    }
    var photoUri by remember(uiState.preferences.profilePhotoUri) {
        mutableStateOf(uiState.preferences.profilePhotoUri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            R.string.back
                        ))
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.user != null) {
                    Row (horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(72.dp)) {
                            ImagePlaceholder(
                                label = uiState.preferences.username ?: uiState.user!!.displayName
                                ?: stringResource(
                                    R.string.user
                                ),
                                imageUri = uiState.preferences.profilePhotoUri
                                    ?: uiState.user!!.photoUrl,
                                size = 72.dp
                            )
                        }
                        OutlinedButton(
                            onClick = onChangePhoto,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.change_profile_photo))
                        }
                    }
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(stringResource(R.string.bio)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Button(
                    onClick = {
                        viewModel.updateProfile(username, bio)
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = username.isNotBlank()
                ) {
                    Text(stringResource(R.string.save_changes))
                }
            }
        }
    }
}
