package com.malha.app.core.design.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
        
        // Very simple search for known stitches
        // In a real app, this would use a more sophisticated regex or tokenizer
        val sortedStitches = knownStitches.sortedByDescending { it.length }
        
        while (currentIndex < text.length) {
            val remainingText = text.substring(currentIndex)
            val match = sortedStitches.firstOrNull { remainingText.startsWith(it, ignoreCase = true) }
            
            if (match != null) {
                pushStringAnnotation(tag = "STITCH", annotation = match)
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(text.substring(currentIndex, currentIndex + match.length))
                }
                pop()
                currentIndex += match.length
            } else {
                append(text[currentIndex])
                currentIndex++
            }
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "STITCH", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onStitchClick(annotation.item)
                }
        }
    )
}
