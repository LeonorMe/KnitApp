package com.malha.app.core.design.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

@Composable
fun StitchLinkText(
    text: String,
    knownStitches: List<String>,
    onStitchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        val sortedStitches = knownStitches.sortedByDescending { it.length }
        
        while (currentIndex < text.length) {
            val remainingText = text.substring(currentIndex)
            val match = sortedStitches.firstOrNull { remainingText.startsWith(it, ignoreCase = true) }
            
            if (match != null) {
                withLink(
                    link = LinkAnnotation.Clickable(
                        tag = "STITCH",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ),
                        linkInteractionListener = { 
                            onStitchClick(match)
                        }
                    )
                ) {
                    append(text.substring(currentIndex, currentIndex + match.length))
                }
                currentIndex += match.length
            } else {
                append(text[currentIndex])
                currentIndex++
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun StitchDetailDialog(
    stitchName: String,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stitchName, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Text(
                text = "Learn how to do the $stitchName stitch. This would typically include a description, diagrams, or a video link.",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

val KNOWN_STITCHES = listOf(
    "knit", "purl", "k2tog", "ssk", "yo", "kfb", "slip", "stitch", "row", "round", "rib", "stockinette", "cast on", "bind off",
    "sc", "dc", "tc", "hdc", "ch", "sl st"
)
