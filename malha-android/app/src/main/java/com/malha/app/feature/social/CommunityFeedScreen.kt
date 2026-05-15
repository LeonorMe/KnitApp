package com.malha.app.feature.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.app.appContainer
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.domain.model.Post
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {
    val feed: StateFlow<List<Post>> = appContainer.socialRepository
        .observeFeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun likePost(postId: String) {
        // TODO: Implement likes
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen(
    onCreatePost: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val feed by viewModel.feed.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Knitstagram") },
                actions = {
                    IconButton(onClick = onCreatePost) {
                        Icon(Icons.Default.Add, contentDescription = "Create Post")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (feed.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No posts yet. Be the first to share your work!")
                    }
                }
            }
            items(feed) { post ->
                PostCard(post = post, onLike = { viewModel.likePost(post.id) })
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, onLike: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // User Header
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.size(40.dp)) {
                    ImagePlaceholder(label = post.userName, imageUri = post.userProfilePic)
                }
                Column {
                    Text(post.userName, style = MaterialTheme.typography.titleMedium)
                    if (post.patternName != null) {
                        Text(
                            text = "using ${post.patternName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Badge(
                    containerColor = if (post.status == "finished") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(post.status.uppercase(), modifier = Modifier.padding(4.dp))
                }
            }

            // Image
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                ImagePlaceholder(
                    label = "Project Photo",
                    imageUri = post.imageUri
                )
            }

            // Interactions
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                var showComments by remember { mutableStateOf(false) }
                IconButton(onClick = onLike) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Like")
                }
                IconButton(onClick = { showComments = true }) {
                    Icon(Icons.Default.Comment, contentDescription = "Comment")
                }
                IconButton(onClick = { /* TODO: Share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
                
                if (showComments) {
                    CommentsDialog(postId = post.id, onDismiss = { showComments = false })
                }
            }

            // Caption
            Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${post.userName} ")
                        }
                        append(post.description)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "View all comments",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CommentsDialog(postId: String, onDismiss: () -> Unit) {
    val comments by appContainer.socialRepository.observeComments(postId).collectAsState(emptyList())
    var newComment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Comments") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(comments) { comment ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(comment.userName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text(comment.content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Add a comment...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = newComment.isNotBlank(),
                onClick = {
                    scope.launch {
                        appContainer.socialRepository.addComment(postId, newComment)
                        newComment = ""
                    }
                }
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun buildAnnotatedString(block: androidx.compose.ui.text.AnnotatedString.Builder.() -> Unit) = 
    androidx.compose.ui.text.buildAnnotatedString(block)

private fun withStyle(style: androidx.compose.ui.text.SpanStyle, block: androidx.compose.ui.text.AnnotatedString.Builder.() -> Unit) = 
    androidx.compose.ui.text.withStyle(style, block)
