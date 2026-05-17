package com.malha.app.feature.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.app.appContainer
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.domain.model.Post
import com.malha.app.domain.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: android.app.Application) : AndroidViewModel(application) {
    val user: StateFlow<User?> = appContainer.socialRepository
        .observeCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val myPosts: StateFlow<List<Post>> = user.let { userFlow ->
        // In a real app we'd use flatMapLatest
        appContainer.socialRepository.observeUserPosts("default-user")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    fun updateProfile(name: String, bio: String?, profilePicUri: String?) {
        viewModelScope.launch {
            appContainer.socialRepository.updateProfile(name, bio, profilePicUri)
        }
    }

    init {
        viewModelScope.launch {
            appContainer.socialRepository.getCurrentUser()
        }
    }
}

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()
    val posts by viewModel.myPosts.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Profile Header
            ProfileHeader(
                user = user,
                postsCount = posts.size,
                onEditClick = { showEditDialog = true }
            )

            HorizontalDivider()

            // Posts Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(posts) { post ->
                    Box(modifier = Modifier.aspectRatio(1f)) {
                        ImagePlaceholder(label = "Post", imageUri = post.imageUri)
                    }
                }
            }
        }
    }

    if (showEditDialog && user != null) {
        EditProfileDialog(
            user = user!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, bio, picUri ->
                viewModel.updateProfile(name, bio, picUri)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun ProfileHeader(user: User?, postsCount: Int, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(80.dp)) {
                ImagePlaceholder(label = user?.name ?: "User", imageUri = user?.profilePicUri)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StatItem(label = "Projetos", value = postsCount.toString())
                StatItem(label = "Amigos", value = "42")
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                user?.name ?: "Loading...",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                user?.bio ?: "No bio yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile")
            }
            
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.height(40.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    Text("${user?.coins ?: 0} Coins", style = MaterialTheme.typography.labelLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio ?: "") }
    var picUri by remember { mutableStateOf(user.profilePicUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> picUri = uri?.toString() }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally).clickable { photoPickerLauncher.launch("image/*") }) {
                    ImagePlaceholder(label = name, imageUri = picUri)
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, minLines = 3)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, bio, picUri) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
