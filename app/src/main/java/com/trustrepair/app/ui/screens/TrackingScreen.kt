package com.trustrepair.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.trustrepair.app.data.demoJob
import com.trustrepair.app.data.demoQuotes
import com.trustrepair.app.ui.theme.*

// Timeline event status
private enum class TimelineStatus {
    CURRENT, DONE, PENDING
}

// Timeline event data
private data class TimelineEvent(
    val status: TimelineStatus,
    val title: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    onBack: () -> Unit,
    onRate: () -> Unit
) {
    val quote = demoQuotes[0]
    val job = demoJob

    // Timeline events
    val timelineEvents = listOf(
        TimelineEvent(TimelineStatus.CURRENT, "En attente du RDV", "Dans 3 jours"),
        TimelineEvent(TimelineStatus.DONE, "Artisan confirmé", "Aujourd'hui, 10:32"),
        TimelineEvent(TimelineStatus.DONE, "Paiement reçu", "Aujourd'hui, 10:30"),
        TimelineEvent(TimelineStatus.DONE, "Demande créée", "Aujourd'hui, 10:15")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tracking_title),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Gray900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Gray700
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Overflow menu */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Menu",
                            tint = Gray700
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status card
            StatusCard(
                date = quote.date,
                timeSlot = quote.timeSlot
            )

            // Provider card
            ProviderCard(
                name = quote.provider.name,
                initials = quote.provider.initials,
                avatarColor = quote.provider.avatarColor,
                specialty = job.type,
                rating = quote.provider.rating,
                reviewCount = quote.provider.reviewCount
            )

            // Timeline card
            TimelineCard(events = timelineEvents)

            // Confirm finish button
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirmer la fin du travail",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            // Cancel button
            OutlinedButton(
                onClick = { /* Cancel action */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ErrorRed
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(ErrorRed.copy(alpha = 0.5f), ErrorRed.copy(alpha = 0.5f)))
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.tracking_cancel),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusCard(
    date: String,
    timeSlot: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar icon in blue circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(TrustBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = TrustBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Status text
            Text(
                text = stringResource(R.string.tracking_status),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )

            // Date and time
            Text(
                text = "$date, $timeSlot",
                fontSize = 15.sp,
                color = Gray600,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProviderCard(
    name: String,
    initials: String,
    avatarColor: Color,
    specialty: String,
    rating: Float,
    reviewCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Provider header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(avatarColor, avatarColor.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Gray900
                    )
                    Text(
                        text = specialty,
                        fontSize = 14.sp,
                        color = Gray500
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "$rating",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = Gray700
                        )
                        Text(
                            text = "($reviewCount avis)",
                            fontSize = 13.sp,
                            color = Gray500
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Message button (outline)
                OutlinedButton(
                    onClick = { /* Message action */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Gray700
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.tracking_message),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }

                // Call button (primary)
                Button(
                    onClick = { /* Call action */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrustBlue
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.tracking_call),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineCard(events: List<TimelineEvent>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title
            Text(
                text = stringResource(R.string.tracking_history),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Gray900
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Timeline items
            events.forEachIndexed { index, event ->
                TimelineItem(
                    event = event,
                    isLast = index == events.lastIndex
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(
    event: TimelineEvent,
    isLast: Boolean
) {
    // Pulsing animation for current status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status indicator
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (event.status) {
                    TimelineStatus.CURRENT -> {
                        // Pulsing blue dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(TrustBlue.copy(alpha = 0.3f))
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TrustBlue)
                        )
                    }
                    TimelineStatus.DONE -> {
                        // Green checkmark
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    TimelineStatus.PENDING -> {
                        // Gray circle
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Gray300)
                        )
                    }
                }
            }

            // Connecting line (if not last)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (event.status == TimelineStatus.DONE) Gray200 else Gray200
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 24.dp)
        ) {
            Text(
                text = event.title,
                fontWeight = if (event.status == TimelineStatus.CURRENT) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 14.sp,
                color = if (event.status == TimelineStatus.CURRENT) Gray900 else Gray700
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.time,
                fontSize = 12.sp,
                color = if (event.status == TimelineStatus.CURRENT) TrustBlue else Gray500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackingScreenPreview() {
    TrustRepairTheme {
        TrackingScreen(
            onBack = {},
            onRate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            StatusCard(
                date = "Lundi 20 janvier",
                timeSlot = "14h-17h"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelineItemPreview() {
    TrustRepairTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TimelineItem(
                event = TimelineEvent(
                    status = TimelineStatus.CURRENT,
                    title = "En attente du RDV",
                    time = "Dans 3 jours"
                ),
                isLast = false
            )
            TimelineItem(
                event = TimelineEvent(
                    status = TimelineStatus.DONE,
                    title = "Artisan confirmé",
                    time = "Aujourd'hui, 10:32"
                ),
                isLast = true
            )
        }
    }
}
