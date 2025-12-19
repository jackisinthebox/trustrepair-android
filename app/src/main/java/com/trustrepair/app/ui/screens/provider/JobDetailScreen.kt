package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.ActiveJob
import com.trustrepair.app.data.JobStatus
import com.trustrepair.app.data.demoActiveJobs
import com.trustrepair.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val job = demoActiveJobs.find { it.id == jobId } ?: demoActiveJobs[0]

    // Mutable status for simulation
    var currentStatus by remember { mutableStateOf(job.status) }

    // Overflow menu state
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.job_detail_title),
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
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Menu",
                                tint = Gray700
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Contacter le support") },
                                onClick = { showMenu = false },
                                leadingIcon = {
                                    Icon(Icons.Filled.Support, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Signaler un problème") },
                                onClick = { showMenu = false },
                                leadingIcon = {
                                    Icon(Icons.Filled.Report, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            ActionButton(
                status = currentStatus,
                onStatusChange = { newStatus ->
                    currentStatus = newStatus
                },
                onComplete = onComplete
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Status banner
            StatusBanner(
                status = currentStatus,
                date = job.date,
                timeSlot = job.timeSlot
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Client card
                ClientCard(
                    name = job.client.name,
                    initials = job.client.initials,
                    phone = job.client.phone,
                    onMessage = { /* Message action */ },
                    onCall = { /* Call action */ }
                )

                // Job info card
                JobInfoCard(
                    jobType = job.jobType,
                    description = job.description,
                    photos = emptyList(), // No photos in current data model
                    instructions = job.accessNotes
                )

                // Access card
                AccessCard(
                    address = job.address,
                    accessCode = job.accessCode,
                    accessNotes = job.accessNotes,
                    onOpenMaps = { /* Open maps */ }
                )

                // Price card
                PriceCard(
                    laborPrice = job.priceBreakdown.labor,
                    partsPrice = job.priceBreakdown.parts,
                    totalPrice = job.priceBreakdown.total,
                    isFixed = job.isFixed
                )

                // Bottom spacing
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatusBanner(
    status: JobStatus,
    date: String,
    timeSlot: String
) {
    val (backgroundColor, textColor, statusText) = when (status) {
        JobStatus.CONFIRMED -> Triple(
            TrustBlue,
            Color.White,
            stringResource(R.string.job_status_confirmed)
        )
        JobStatus.EN_ROUTE -> Triple(
            WarningAmber,
            Color.White,
            stringResource(R.string.job_status_en_route)
        )
        JobStatus.IN_PROGRESS -> Triple(
            SuccessGreen,
            Color.White,
            stringResource(R.string.job_status_in_progress)
        )
        JobStatus.COMPLETED -> Triple(
            Gray500,
            Color.White,
            stringResource(R.string.job_status_completed)
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = when (status) {
                    JobStatus.CONFIRMED -> Icons.Filled.CheckCircle
                    JobStatus.EN_ROUTE -> Icons.Filled.DirectionsCar
                    JobStatus.IN_PROGRESS -> Icons.Filled.Construction
                    JobStatus.COMPLETED -> Icons.Filled.Done
                },
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$statusText — $date, $timeSlot",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
private fun ClientCard(
    name: String,
    initials: String,
    phone: String,
    onMessage: () -> Unit,
    onCall: () -> Unit
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
            // Header
            Text(
                text = stringResource(R.string.job_detail_client),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500
            )

            // Client info row
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = null,
                            tint = Gray400,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = phone,
                            fontSize = 14.sp,
                            color = Gray500
                        )
                    }
                }
            }

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Message button (outline)
                OutlinedButton(
                    onClick = onMessage,
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
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProviderPurple
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
private fun JobInfoCard(
    jobType: String,
    description: String,
    photos: List<String>,
    instructions: String
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
            // Header
            Text(
                text = stringResource(R.string.job_detail_job_info),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500
            )

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
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = null,
                        tint = TrustBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = jobType,
                    fontSize = 17.sp,
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

            // Photos (if any)
            if (photos.isNotEmpty()) {
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

            // Special instructions (if any)
            if (instructions.isNotBlank()) {
                Divider(color = Gray100, thickness = 1.dp)

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Instructions",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray700
                        )
                        Text(
                            text = instructions,
                            fontSize = 14.sp,
                            color = Gray600,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessCard(
    address: String,
    accessCode: String,
    accessNotes: String,
    onOpenMaps: () -> Unit
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
            Text(
                text = stringResource(R.string.job_detail_access),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500
            )

            // Address
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SuccessGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = address,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray900,
                        lineHeight = 22.sp
                    )
                }
            }

            // Open in Maps button
            TextButton(
                onClick = onOpenMaps,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = null,
                    tint = ProviderPurple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.job_detail_open_maps),
                    color = ProviderPurple,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Divider(color = Gray100, thickness = 1.dp)

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stairs,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = accessNotes,
                        fontSize = 14.sp,
                        color = Gray600
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceCard(
    laborPrice: Int,
    partsPrice: Int,
    totalPrice: Int,
    isFixed: Boolean
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
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.job_detail_price),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray500
                )

                // Price type badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isFixed) SuccessGreenLight else WarningAmberLight
                ) {
                    Text(
                        text = if (isFixed)
                            stringResource(R.string.quote_builder_fixed)
                        else
                            stringResource(R.string.quote_builder_estimate),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isFixed) SuccessGreenDark else WarningAmberDark
                    )
                }
            }

            // Labor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.payment_labor),
                    fontSize = 14.sp,
                    color = Gray600
                )
                Text(
                    text = "$laborPrice €",
                    fontSize = 14.sp,
                    color = Gray700
                )
            }

            // Parts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.payment_parts),
                    fontSize = 14.sp,
                    color = Gray600
                )
                Text(
                    text = "$partsPrice €",
                    fontSize = 14.sp,
                    color = Gray700
                )
            }

            Divider(color = Gray200, thickness = 1.dp)

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.payment_total),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Text(
                    text = "$totalPrice €",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProviderPurple
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    status: JobStatus,
    onStatusChange: (JobStatus) -> Unit,
    onComplete: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (status) {
                JobStatus.CONFIRMED -> {
                    Button(
                        onClick = { onStatusChange(JobStatus.EN_ROUTE) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TrustBlue
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.job_detail_action_en_route),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
                JobStatus.EN_ROUTE -> {
                    Button(
                        onClick = { onStatusChange(JobStatus.IN_PROGRESS) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningAmber
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.job_detail_action_arrived),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
                JobStatus.IN_PROGRESS -> {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.job_detail_action_complete),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
                JobStatus.COMPLETED -> {
                    // No action for completed jobs
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Gray100
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                                tint = Gray500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Travail terminé",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Gray500
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JobDetailScreenPreview() {
    TrustRepairTheme {
        JobDetailScreen(
            jobId = "job1",
            onBack = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBannerPreview() {
    TrustRepairTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBanner(JobStatus.CONFIRMED, "Lundi 20 janvier", "14h-17h")
            StatusBanner(JobStatus.EN_ROUTE, "Lundi 20 janvier", "14h-17h")
            StatusBanner(JobStatus.IN_PROGRESS, "Lundi 20 janvier", "14h-17h")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PriceCard(
                laborPrice = 100,
                partsPrice = 50,
                totalPrice = 150,
                isFixed = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionButtonPreview() {
    TrustRepairTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(JobStatus.CONFIRMED, {}, {})
            ActionButton(JobStatus.EN_ROUTE, {}, {})
            ActionButton(JobStatus.IN_PROGRESS, {}, {})
        }
    }
}
