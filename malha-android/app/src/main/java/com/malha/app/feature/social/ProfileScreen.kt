package com.malha.app.feature.social

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.MalhaApplication
import com.malha.app.core.app.appContainer
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.domain.model.Post
import com.malha.app.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    application: android.app.Application,
    private val requestedUserId: String?
) : AndroidViewModel(application) {
    private val currentUserFlow = appContainer.socialRepository.observeCurrentUser()

    val user: StateFlow<User?> = if (requestedUserId == null) {
        currentUserFlow
    } else {
        appContainer.socialRepository.observeUser(requestedUserId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val posts: StateFlow<List<Post>> = if (requestedUserId == null) {
        currentUserFlow.flatMapLatest { currentUser ->
            currentUser?.let { appContainer.socialRepository.observeUserPosts(it.id) } ?: flowOf(emptyList())
        }
    } else {
        appContainer.socialRepository.observeUserPosts(requestedUserId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val canEditProfile: Boolean = requestedUserId == null

    fun updateProfile(name: String, bio: String?, profilePicUri: String?) {
        viewModelScope.launch {
            appContainer.socialRepository.updateProfile(name, bio, profilePicUri)
        }
    }

    init {
        if (requestedUserId == null) {
            viewModelScope.launch {
                appContainer.socialRepository.getCurrentUser()
            }
        }
    }

    companion object {
        fun factory(userId: String?) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(MalhaApplication.instance, userId) as T
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userId: String? = null,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(userId))
) {
    val user by viewModel.user.collectAsState()
    val posts by viewModel.posts.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    val displayName = user?.name ?: posts.firstOrNull()?.userName ?: stringResource(R.string.profile_unknown_crafter)
    val displayBio = user?.bio ?: stringResource(R.string.profile_default_bio)
    val displayPhoto = user?.profilePicUri ?: posts.firstOrNull()?.userProfilePic
    val completedProjects = posts.count { it.status == "finished" }
    val level = craftLevelFor(completedProjects)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ProfileHeader(
                name = displayName,
                bio = displayBio,
                profilePicUri = displayPhoto,
                postsCount = posts.size,
                completedProjects = completedProjects,
                level = stringResource(level.titleRes),
                coins = user?.coins ?: 0,
                canEdit = viewModel.canEditProfile,
                onEditClick = { showEditDialog = true }
            )

            HorizontalDivider()

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    Box(modifier = Modifier.aspectRatio(1f)) {
                        ImagePlaceholder(
                            label = stringResource(R.string.profile_post_image),
                            imageUri = post.imageUri,
                            modifier = Modifier.fillMaxSize(),
                            size = 180.dp
                        )
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
private fun ProfileHeader(
    name: String,
    bio: String,
    profilePicUri: String?,
    postsCount: Int,
    completedProjects: Int,
    level: String,
    coins: Int,
    canEdit: Boolean,
    onEditClick: () -> Unit
) {
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
                ImagePlaceholder(label = name, imageUri = profilePicUri)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                StatItem(label = stringResource(R.string.profile_level), value = level)
                StatItem(label = stringResource(R.string.profile_completed), value = completedProjects.toString())
                StatItem(label = stringResource(R.string.profile_posts), value = postsCount.toString())
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(name, style = MaterialTheme.typography.titleLarge)
            Text(
                bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (canEdit) {
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_edit_profile))
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        stringResource(R.string.profile_coins, coins),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
        onResult = { uri: Uri? -> picUri = uri?.toString() }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.profile_edit_profile)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable { photoPickerLauncher.launch("image/*") }
                ) {
                    ImagePlaceholder(label = name, imageUri = picUri)
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.profile_name)) }
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(stringResource(R.string.profile_bio)) },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, bio, picUri) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

private data class CraftLevel(
    val titleRes: Int
)

private fun craftLevelFor(completedProjects: Int): CraftLevel {
    return when {
        completedProjects >= 20 -> CraftLevel(R.string.profile_level_advanced)
        completedProjects >= 8 -> CraftLevel(R.string.profile_level_confident)
        completedProjects >= 3 -> CraftLevel(R.string.profile_level_steady)
        else -> CraftLevel(R.string.profile_level_beginner)
    }
}
