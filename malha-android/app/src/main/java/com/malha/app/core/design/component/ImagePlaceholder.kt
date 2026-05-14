package com.malha.app.core.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.malha.app.R

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
