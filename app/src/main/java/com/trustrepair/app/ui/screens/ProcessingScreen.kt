package com.trustrepair.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    onComplete: () -> Unit
) {
    // Auto-navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onComplete()
    }

    // Spinner rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Custom spinner with credit card icon
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Track (gray background circle)
                    Canvas(modifier = Modifier.size(96.dp)) {
                        drawArc(
                            color = Gray200,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Animated fill (blue arc)
                    Canvas(
                        modifier = Modifier
                            .size(96.dp)
                            .rotate(rotation)
                    ) {
                        drawArc(
                            color = TrustBlue,
                            startAngle = 0f,
                            sweepAngle = 90f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Credit card icon in center
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TrustBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = null,
                            tint = TrustBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Title
                Text(
                    text = stringResource(R.string.processing_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )

                // Subtitle
                Text(
                    text = stringResource(R.string.processing_subtitle),
                    fontSize = 15.sp,
                    color = Gray600
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Step indicators
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Step 1: Done
                    StepIndicator(
                        status = StepStatus.DONE,
                        text = stringResource(R.string.processing_step_card)
                    )

                    // Step 2: Active
                    StepIndicator(
                        status = StepStatus.ACTIVE,
                        text = stringResource(R.string.processing_step_auth),
                        rotation = rotation
                    )

                    // Step 3: Pending
                    StepIndicator(
                        status = StepStatus.PENDING,
                        text = stringResource(R.string.processing_step_confirm)
                    )
                }
            }
        }
    }
}

private enum class StepStatus {
    DONE, ACTIVE, PENDING
}

@Composable
private fun StepIndicator(
    status: StepStatus,
    text: String,
    rotation: Float = 0f
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon based on status
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                StepStatus.DONE -> {
                    // Green checkmark circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                StepStatus.ACTIVE -> {
                    // Spinning blue loader
                    Canvas(
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotation)
                    ) {
                        // Track
                        drawArc(
                            color = Gray200,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Active arc
                        drawArc(
                            color = TrustBlue,
                            startAngle = 0f,
                            sweepAngle = 90f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
                StepStatus.PENDING -> {
                    // Empty gray circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Gray200)
                    )
                }
            }
        }

        // Text
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (status == StepStatus.ACTIVE) FontWeight.SemiBold else FontWeight.Normal,
            color = when (status) {
                StepStatus.DONE -> Gray600
                StepStatus.ACTIVE -> Gray900
                StepStatus.PENDING -> Gray400
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProcessingScreenPreview() {
    TrustRepairTheme {
        ProcessingScreen(onComplete = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun StepIndicatorPreview() {
    TrustRepairTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepIndicator(status = StepStatus.DONE, text = "Carte vérifiée")
            StepIndicator(status = StepStatus.ACTIVE, text = "Autorisation bancaire", rotation = 45f)
            StepIndicator(status = StepStatus.PENDING, text = "Confirmation")
        }
    }
}
