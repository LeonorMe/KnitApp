package com.malha.app.core.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.malha.app.R

@Composable
fun ImagePlaceholder(
    label: String,
    imageUri: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val drawableRes = imageUri?.toDrawableRes()

    when {
        drawableRes != null -> {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(size)
                    .clip(MaterialTheme.shapes.medium)
            )
        }

        !imageUri.isNullOrBlank() -> {
            val context = LocalContext.current
            val imageRequest = remember(imageUri) {
                ImageRequest.Builder(context)
                    .data(imageUri)
                    .crossfade(true)
                    .build()
            }

            SubcomposeAsyncImage(
                model = imageRequest,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(size)
                    .clip(MaterialTheme.shapes.medium),
                loading = {
                    DefaultPlaceholderBox(label = label, size = size, modifier = modifier)
                },
                error = {
                    DefaultPlaceholderBox(label = label, size = size, modifier = modifier)
                }
            )
        }

        else -> {
            DefaultPlaceholderBox(label = label, size = size, modifier = modifier)
        }
    }
}

@Composable
private fun DefaultPlaceholderBox(
    label: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label.take(12),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String.toDrawableRes(): Int? {
    return when (this) {
        "drawable:malha_asset_project" -> R.drawable.malha_asset_project
        "drawable:malha_asset_yarn" -> R.drawable.malha_asset_yarn
        else -> null
    }
}
