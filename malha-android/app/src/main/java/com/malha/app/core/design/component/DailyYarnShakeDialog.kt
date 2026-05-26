package com.malha.app.core.design.component

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.malha.app.core.util.ShakeDetector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DailyYarnShakeDialog(
    onDismiss: () -> Unit,
    onRewardClaimed: (Int) -> Unit
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(ShakeState.READY) }
    var shakeProgress by remember { mutableFloatStateOf(0f) }
    var rewardAmount by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "yarn")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val bounce by animateFloatAsState(
        targetValue = if (state == ShakeState.SHAKING) -20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Shake Detector setup
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val detector = ShakeDetector {
            if (state == ShakeState.READY || state == ShakeState.SHAKING) {
                state = ShakeState.SHAKING
                shakeProgress += 0.05f
            }
        }

        sensorManager.registerListener(detector, accelerometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(detector)
        }
    }

    // Logic for completing shake
    LaunchedEffect(shakeProgress) {
        if (shakeProgress >= 1f && state == ShakeState.SHAKING) {
            state = ShakeState.CELEBRATING
            rewardAmount = Random.nextInt(1, 6)
            delay(2000)
            onRewardClaimed(rewardAmount)
        }
    }

    // Auto-progress for emulators/testing if needed (Optional: can be triggered by clicking)
    
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    when (state) {
                        ShakeState.READY -> {
                            Text("Shake your phone to tangle the yarn!", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                            YarnShakePlayfield(
                                progress = shakeProgress,
                                rotation = 0f,
                                isShaking = false
                            )
                            Button(onClick = { state = ShakeState.SHAKING; scope.launch { while(shakeProgress < 1f) { delay(100); shakeProgress += 0.03f } } }) {
                                Text("Click if you can't shake")
                            }
                        }
                        ShakeState.SHAKING -> {
                            Text("Keep shaking!", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            YarnShakePlayfield(
                                progress = shakeProgress,
                                rotation = rotation,
                                isShaking = true
                            )
                            LinearProgressIndicator(
                                progress = { shakeProgress },
                                modifier = Modifier.width(200.dp).height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                        }
                        ShakeState.CELEBRATING -> {
                            Text("Yay! You untangled it!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(48.dp))
                                Text("+$rewardAmount Coins", style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                            Text("Adding to your stash...", color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YarnShakePlayfield(
    progress: Float,
    rotation: Float,
    isShaking: Boolean
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
        contentAlignment = Alignment.Center
    ) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        val ballSize = 132.dp
        val horizontalTravel = (maxWidth - ballSize) * 0.42f
        val verticalTravel = (maxHeight - ballSize) * 0.36f
        val phase = clampedProgress * 2.8f * PI.toFloat()
        val xOffset = if (isShaking) {
            horizontalTravel * cos(phase * 1.35f)
        } else {
            0.dp
        }
        val yOffset = if (isShaking) {
            verticalTravel * sin(phase * 1.9f)
        } else {
            0.dp
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val yarnPath = Path()
            val points = (clampedProgress * 90).toInt().coerceAtLeast(1)
            for (index in 0..points) {
                val t = index / 90f
                val angle = t * 2.8f * PI.toFloat()
                val radiusPulse = 0.72f + 0.28f * sin(t * 7.2f * PI.toFloat())
                val x = size.width / 2f +
                    cos(angle * 1.35f) * size.width * 0.34f * radiusPulse
                val y = size.height / 2f +
                    sin(angle * 1.9f) * size.height * 0.32f

                if (index == 0) {
                    yarnPath.moveTo(x, y)
                } else {
                    yarnPath.lineTo(x, y)
                }
            }

            drawPath(
                path = yarnPath,
                color = Color(0xFFFF80AB).copy(alpha = 0.72f),
                style = Stroke(
                    width = 10f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            drawPath(
                path = yarnPath,
                color = Color.White.copy(alpha = 0.32f),
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        YarnBall(
            rotation = rotation,
            modifier = Modifier.offset(x = xOffset, y = yOffset)
        )
    }
}

@Composable
private fun YarnBall(
    rotation: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFE91E63),
                radius = size.minDimension / 2
            )
            // Draw some "tangled" lines
            for (i in 0..10) {
                drawArc(
                    color = Color.White.copy(alpha = 0.5f),
                    startAngle = (i * 40).toFloat(),
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}

enum class ShakeState {
    READY, SHAKING, CELEBRATING
}
