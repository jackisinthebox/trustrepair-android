package com.trustrepair.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.demoQuotes
import com.trustrepair.app.ui.theme.*

// Star color
private val StarAmber = Color(0xFFFBBF24)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val provider = demoQuotes[0].provider

    // State
    var selectedRating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    // Rating labels
    val ratingLabels = listOf(
        "", // 0 - no selection
        stringResource(R.string.rating_1),
        stringResource(R.string.rating_2),
        stringResource(R.string.rating_3),
        stringResource(R.string.rating_4),
        stringResource(R.string.rating_5)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.rating_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Gray900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = Gray700
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedRating > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrustBlue,
                        disabledContainerColor = Gray300
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rating_submit),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Provider avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                provider.avatarColor,
                                provider.avatarColor.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = provider.initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question
            Text(
                text = stringResource(R.string.rating_question),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(R.string.rating_subtitle),
                fontSize = 15.sp,
                color = Gray600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Star rating
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (star in 1..5) {
                    StarButton(
                        filled = star <= selectedRating,
                        onClick = { selectedRating = star }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating label
            if (selectedRating > 0) {
                Text(
                    text = ratingLabels[selectedRating],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray700,
                    textAlign = TextAlign.Center
                )
            } else {
                // Placeholder to maintain spacing
                Text(
                    text = " ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Comment section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.rating_comment_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(
                            text = "Partagez votre expérience avec ${provider.name}...",
                            color = Gray400
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrustBlue,
                        unfocusedBorderColor = Gray300,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Gray50
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StarButton(
    filled: Boolean,
    onClick: () -> Unit
) {
    // Scale animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 400f
        ),
        label = "starScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarOutline,
            contentDescription = null,
            tint = if (filled) StarAmber else Gray300,
            modifier = Modifier.size(40.dp)
        )
    }

    // Reset press state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingScreenPreview() {
    TrustRepairTheme {
        RatingScreen(
            onBack = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StarButtonPreview() {
    TrustRepairTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StarButton(filled = true, onClick = {})
            StarButton(filled = true, onClick = {})
            StarButton(filled = true, onClick = {})
            StarButton(filled = false, onClick = {})
            StarButton(filled = false, onClick = {})
        }
    }
}
