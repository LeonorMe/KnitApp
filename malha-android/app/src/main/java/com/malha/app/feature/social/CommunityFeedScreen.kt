package com.malha.app.feature.social

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.core.app.appContainer
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.share.MalhaShare
import com.malha.app.data.social.SocialRepository
import com.malha.app.domain.model.Comment
import com.malha.app.domain.model.Post
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(application: android.app.Application) : AndroidViewModel(application) {
    val repository: SocialRepository = appContainer.socialRepository
    
    val feed: StateFlow<List<Post>> = repository
        .observeFeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun likePost(postId: String) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
        }
    }

    fun observeComments(postId: String): StateFlow<List<Comment>> {
        return repository.observeComments(postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            repository.addComment(postId, content)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen(
    onCreatePost: () -> Unit,
    onNavigateToPattern: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val feed by viewModel.feed.collectAsState()
    var query by remember { mutableStateOf("") }
    val filteredFeed = remember(feed, query) {
        if (query.isBlank()) {
            feed
        } else {
            val normalized = query.trim().lowercase()
            feed.filter { post ->
                listOfNotNull(
                    post.userName,
                    post.patternName,
                    post.description,
                    post.status
                ).any { it.lowercase().contains(normalized) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.community),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    IconButton(onClick = onCreatePost) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.create_post),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    placeholder = {
                        Text(stringResource(R.string.community_search_placeholder))
                    }
                )
            }

            if (filteredFeed.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (query.isBlank()) {
                                    stringResource(R.string.nothing_publised)
                                } else {
                                    stringResource(R.string.community_no_search_results)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.be_the_first_to_share_your_work),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            items(filteredFeed, key = { it.id }) { post ->
                PostCard(
                    post = post, 
                    onLike = { viewModel.likePost(post.id) },
                    onAddComment = { viewModel.addComment(post.id, it) },
                    observeComments = { viewModel.observeComments(post.id) },
                    onPatternClick = onNavigateToPattern,
                    onAuthorClick = { onNavigateToProfile(post.userId) }
                )
            }
        }
    }
}

@Composable
private fun PostCard(
    post: Post, 
    onLike: () -> Unit,
    onAddComment: (String) -> Unit,
    observeComments: () -> StateFlow<List<Comment>>,
    onPatternClick: (String) -> Unit,
    onAuthorClick: () -> Unit
) {
    val context = LocalContext.current
    val heartScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "HeartScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // User Header
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.size(42.dp)) {
                    Surface(
                        onClick = onAuthorClick,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        ImagePlaceholder(
                            label = post.userName.take(2).uppercase(),
                            imageUri = post.userProfilePic,
                            size = 42.dp
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onAuthorClick)
                ) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (post.patternName != null && post.patternId != null) {
                        Text(
                            text = stringResource(R.string.community_using_pattern, post.patternName.orEmpty()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onPatternClick(post.patternId) }
                                .padding(vertical = 2.dp)
                        )
                    }
                }
                
                Surface(
                    color = if (post.status == "finished") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (post.status == "finished") {
                            stringResource(R.string.community_status_finished)
                        } else {
                            stringResource(R.string.community_status_ongoing)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (post.status == "finished") MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                ImagePlaceholder(
                    label = "Project Photo",
                    imageUri = post.imageUri,
                    size = 320.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Interactions (Hidden Like Counter - Only Interactive Heart Icon)
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showComments by remember { mutableStateOf(false) }
                
                IconButton(onClick = onLike) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.community_like),
                        tint = if (post.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.scale(heartScale)
                    )
                }
                
                IconButton(onClick = { showComments = true }) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = stringResource(R.string.community_comment),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { MalhaShare.sharePost(context, post) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.community_share),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (showComments) {
                    CommentsDialog(
                        onDismiss = { showComments = false },
                        onPostComment = onAddComment,
                        commentsFlow = observeComments()
                    )
                }
            }

            // Caption
            Column(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                            append("${post.userName} ")
                        }
                        append(post.description)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = stringResource(R.string.community_view_comments),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clickable { } // Or open comments directly
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsDialog(
    onDismiss: () -> Unit,
    onPostComment: (String) -> Unit,
    commentsFlow: StateFlow<List<Comment>>
) {
    val comments by commentsFlow.collectAsState()
    var newComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = stringResource(R.string.community_comments_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.community_no_comments),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments) { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = comment.userName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    placeholder = { Text(stringResource(R.string.community_add_comment_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                enabled = newComment.isNotBlank(),
                onClick = {
                    onPostComment(newComment)
                    newComment = ""
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.community_publish_comment))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { 
                Text(stringResource(R.string.action_close)) 
            }
        }
    )
}
