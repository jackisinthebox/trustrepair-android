package com.trustrepair.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trustrepair.app.ui.theme.*

enum class LoadingVariant {
    CARD,
    LIST,
    DETAIL
}

@Composable
fun LoadingState(
    variant: LoadingVariant,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Gray200,
            Gray100,
            Gray200
        ),
        start = Offset(shimmerTranslate - 200f, 0f),
        end = Offset(shimmerTranslate, 0f)
    )

    when (variant) {
        LoadingVariant.CARD -> CardLoadingState(shimmerBrush, modifier)
        LoadingVariant.LIST -> ListLoadingState(shimmerBrush, modifier)
        LoadingVariant.DETAIL -> DetailLoadingState(shimmerBrush, modifier)
    }
}

@Composable
private fun CardLoadingState(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            ShimmerBox(
                brush = shimmerBrush,
                height = 120.dp,
                cornerRadius = 12.dp
            )
        }
    }
}

@Composable
private fun ListLoadingState(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            ListItemShimmer(shimmerBrush)
        }
    }
}

@Composable
private fun ListItemShimmer(shimmerBrush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Gray50)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(shimmerBrush)
        )

        // Text lines
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Composable
private fun DetailLoadingState(
    shimmerBrush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status banner shimmer
        ShimmerBox(
            brush = shimmerBrush,
            height = 56.dp,
            cornerRadius = 12.dp
        )

        // Client card shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Gray50)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(shimmerBrush)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            }
        }

        // Job info card shimmer
        ShimmerBox(
            brush = shimmerBrush,
            height = 140.dp,
            cornerRadius = 16.dp
        )

        // Access card shimmer
        ShimmerBox(
            brush = shimmerBrush,
            height = 100.dp,
            cornerRadius = 16.dp
        )

        // Price card shimmer
        ShimmerBox(
            brush = shimmerBrush,
            height = 80.dp,
            cornerRadius = 16.dp
        )
    }
}

@Composable
private fun ShimmerBox(
    brush: Brush,
    height: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingStateCardPreview() {
    TrustRepairTheme {
        LoadingState(
            variant = LoadingVariant.CARD,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingStateListPreview() {
    TrustRepairTheme {
        LoadingState(
            variant = LoadingVariant.LIST,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingStateDetailPreview() {
    TrustRepairTheme {
        LoadingState(
            variant = LoadingVariant.DETAIL,
            modifier = Modifier.fillMaxSize()
        )
    }
}
