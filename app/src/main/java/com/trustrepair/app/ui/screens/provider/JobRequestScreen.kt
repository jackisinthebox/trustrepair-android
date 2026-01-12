package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.JobRequest
import com.trustrepair.app.data.demoJobRequests
import com.trustrepair.app.ui.components.JobTypeIcon
import com.trustrepair.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobRequestScreen(
    jobId: String,
    onBack: () -> Unit,
    onDecline: () -> Unit,
    onSendQuote: () -> Unit
) {
    val jobRequest = demoJobRequests.find { it.id == jobId } ?: demoJobRequests[0]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.job_request_title),
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
                    // Timer badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarningAmberLight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = WarningAmberDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.provider_expires_in, jobRequest.expiresIn),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = WarningAmberDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomActionBar(
                onDecline = onDecline,
                onSendQuote = onSendQuote
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
            // Client info card
            ClientInfoCard(
                name = jobRequest.client.name,
                initials = jobRequest.client.initials,
                memberSince = jobRequest.client.memberSince,
                verified = jobRequest.client.verified,
                location = jobRequest.location,
                distanceKm = jobRequest.distanceKm
            )

            // Job details card
            JobDetailsCard(
                jobType = jobRequest.jobType,
                description = jobRequest.description,
                photos = jobRequest.photos,
                urgency = jobRequest.urgency,
                availability = jobRequest.availability
            )

            // Access info card
            AccessInfoCard(
                accessCode = jobRequest.accessCode,
                accessNotes = jobRequest.accessNotes
            )

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ClientInfoCard(
    name: String,
    initials: String,
    memberSince: String,
    verified: Boolean,
    location: String,
    distanceKm: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                                colors = listOf(ProviderPurple, ProviderPurple.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                // Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Name row with verified badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )
                        if (verified) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(TrustBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = stringResource(R.string.job_request_verified),
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Member since
                    Text(
                        text = stringResource(R.string.job_request_member_since, memberSince),
                        fontSize = 14.sp,
                        color = Gray500
                    )
                }
            }

            // Location row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "$location ($distanceKm km)",
                    fontSize = 14.sp,
                    color = Gray600
                )
            }
        }
    }
}

@Composable
private fun JobDetailsCard(
    jobType: String,
    description: String,
    photos: List<String>,
    urgency: String,
    availability: String
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
            // Job type with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TrustBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    JobTypeIcon(
                        jobType = jobType,
                        modifier = Modifier.size(24.dp),
                        tint = TrustBlue
                    )
                }
                Text(
                    text = jobType,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
            }

            // Description
            Text(
                text = description,
                fontSize = 15.sp,
                color = Gray700,
                lineHeight = 22.sp
            )

            // Photos section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Photos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray700
                )

                if (photos.isEmpty()) {
                    // Empty state
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Gray100
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PhotoCamera,
                                    contentDescription = null,
                                    tint = Gray400,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Pas de photos",
                                    fontSize = 14.sp,
                                    color = Gray400
                                )
                            }
                        }
                    }
                } else {
                    // Photos LazyRow
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(photos) { photo ->
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Gray200
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = null,
                                        tint = Gray400,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = Gray100, thickness = 1.dp)

            // Urgency row
            DetailRow(
                icon = Icons.Filled.PriorityHigh,
                iconBackgroundColor = ErrorRedLight,
                iconTint = ErrorRed,
                label = stringResource(R.string.job_request_urgency),
                value = urgency
            )

            // Availability row
            DetailRow(
                icon = Icons.Filled.CalendarToday,
                iconBackgroundColor = SuccessGreenLight,
                iconTint = SuccessGreen,
                label = stringResource(R.string.job_request_availability),
                value = availability
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Gray500
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Gray900
            )
        }
    }
}

@Composable
private fun AccessInfoCard(
    accessCode: String,
    accessNotes: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = null,
                    tint = ProviderPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.job_request_access),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
            }

            // Access code (if present)
            if (accessCode.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ProviderPurple.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pin,
                            contentDescription = null,
                            tint = ProviderPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Code: $accessCode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ProviderPurple
                        )
                    }
                }
            }

            // Access notes
            if (accessNotes.isNotBlank()) {
                Text(
                    text = accessNotes,
                    fontSize = 14.sp,
                    color = Gray600,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    onDecline: () -> Unit,
    onSendQuote: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Decline button (outline)
            OutlinedButton(
                onClick = onDecline,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Gray700
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.job_request_decline),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }

            // Send quote button (primary, purple)
            Button(
                onClick = onSendQuote,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProviderPurple
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.job_request_send_quote),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JobRequestScreenPreview() {
    TrustRepairTheme {
        JobRequestScreen(
            jobId = "req1",
            onBack = {},
            onDecline = {},
            onSendQuote = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClientInfoCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ClientInfoCard(
                name = "Marie D.",
                initials = "MD",
                memberSince = "2023",
                verified = true,
                location = "Versailles",
                distanceKm = 2.3f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JobDetailsCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            JobDetailsCard(
                jobType = "Plomberie",
                description = "Fuite sous évier, le joint du siphon semble abîmé",
                photos = emptyList(),
                urgency = "Dès que possible",
                availability = "En semaine, après-midi"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccessInfoCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AccessInfoCard(
                accessCode = "4521B",
                accessNotes = "3ème étage, interphone Dupont"
            )
        }
    }
}
