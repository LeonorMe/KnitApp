package com.malha.app.core.design.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun DailySquareChallengeDialog(
    onDismiss: () -> Unit,
    onRewardClaimed: (Int) -> Unit,
    onStartChallengePattern: () -> Unit = {}
) {
    var isClaimed by remember { mutableStateOf(false) }
    val rewardAmount = 3

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.8f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!isClaimed) {
                        Text(
                            text = "Desafio 10x10 Swatch",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Tricota um pequeno quadrado de teste de 10x10cm usando o ponto de hoje para praticar a tensão!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        // 10x10 interactive/animated swatch preview
                        SwatchGridPreview()

                        Button(
                            onClick = onStartChallengePattern,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Abrir Receita 10x10 na Execução", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                isClaimed = true
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Já concluí! (+3 Moedas)", fontWeight = FontWeight.Medium)
                        }
                    } else {
                        // Claiming animation / Success message
                        LaunchedEffect(Unit) {
                            delay(1800)
                            onRewardClaimed(rewardAmount)
                        }

                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(72.dp)
                        )

                        Text(
                            text = "Parabéns!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = "+$rewardAmount Moedas adicionadas!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Excelente trabalho a treinar os teus pontos hoje.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SwatchGridPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gridAnim"
    )

    Surface(
        modifier = Modifier
            .size(160.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridCount = 10
                val cellW = size.width / gridCount
                val cellH = size.height / gridCount

                // Draw stitch loops inside the 10x10 grid
                for (row in 0 until gridCount) {
                    for (col in 0 until gridCount) {
                        // Calculate coordinates for the cell
                        val cx = col * cellW + cellW / 2
                        val cy = row * cellH + cellH / 2

                        // Animate opacity based on progress
                        val alpha = if ((row + col) / 20f <= animProgress) 0.9f else 0.25f

                        // Draw a tiny stitch loop (V shape)
                        val path = Path().apply {
                            moveTo(cx - cellW * 0.35f, cy - cellH * 0.35f)
                            quadraticTo(cx, cy + cellH * 0.35f, cx + cellW * 0.35f, cy - cellH * 0.35f)
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF3F6B57).copy(alpha = alpha), // Sage green theme color
                            style = Stroke(width = 3f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}
