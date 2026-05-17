package com.malha.app.core.design.component

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.malha.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImagePlaceholder(
    label: String,
    imageUri: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val drawableRes = imageUri?.toDrawableRes()

    if (drawableRes != null) {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(MaterialTheme.shapes.medium)
        )
        return
    }

    if (imageUri != null && imageUri.isNotEmpty() && !imageUri.startsWith("drawable:")) {
        val context = LocalContext.current
        var imageBitmap by remember(imageUri) { mutableStateOf<ImageBitmap?>(null) }
        var isLoading by remember(imageUri) { mutableStateOf(true) }

        LaunchedEffect(imageUri) {
            isLoading = true
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(Uri.parse(imageUri))?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageBitmap = bitmap?.asImageBitmap()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            }
        }

        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(size)
                    .clip(MaterialTheme.shapes.medium)
            )
            return
        } else if (isLoading) {
            Surface(
                modifier = modifier.size(size),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
            return
        }
    }

    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
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
