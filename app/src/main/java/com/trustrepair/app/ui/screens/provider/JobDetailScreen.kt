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
import com.trustrepair.app.ui.components.CompletionBottomSheet
import com.trustrepair.app.ui.components.ErrorState
import com.trustrepair.app.ui.components.JobTypeIcon
import com.trustrepair.app.ui.components.LoadingState
import com.trustrepair.app.ui.components.LoadingVariant
import com.trustrepair.app.ui.theme.*
import kotlinx.coroutines.delay

// UI State for JobDetailScreen
private sealed class JobDetailUiState {
    data object Loading : JobDetailUiState()
    data class Error(val message: String) : JobDetailUiState()
    data class Success(val job: ActiveJob) : JobDetailUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    onSendQuote: (String) -> Unit = {},
    onComplete: () -> Unit
) {
    // UI State management
    var uiState by remember { mutableStateOf<JobDetailUiState>(JobDetailUiState.Loading) }

    // Mutable status for simulation - lifted to share between bottomBar and content
    var currentStatus by remember { mutableStateOf<JobStatus?>(null) }

    // Simulate loading with LaunchedEffect
    LaunchedEffect(jobId) {
        uiState = JobDetailUiState.Loading
        currentStatus = null
        delay(500) // Simulate network delay
        val foundJob = demoActiveJobs.find { it.id == jobId }
        uiState = if (foundJob != null) {
            currentStatus = foundJob.status
            JobDetailUiState.Success(foundJob)
        } else {
            JobDetailUiState.Error("Travail non trouvé")
        }
    }

    // Overflow menu state
    var showMenu by remember { mutableStateOf(false) }

    // Completion bottom sheet state
    var showCompletionSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            // Only show action button when in Success state and status is available
            if (uiState is JobDetailUiState.Success && currentStatus != null) {
                ActionButton(
                    status = currentStatus!!,
                    jobId = jobId,
                    onStatusChange = { newStatus ->
                        currentStatus = newStatus
                    },
                    onSendQuote = onSendQuote,
                    onComplete = { showCompletionSheet = true }
                )
            }
        },
        containerColor = Gray50
    ) { padding ->
        when (val state = uiState) {
            is JobDetailUiState.Loading -> {
                LoadingState(
                    variant = LoadingVariant.DETAIL,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            is JobDetailUiState.Error -> {
                ErrorState(
                    onRetry = {
                        uiState = JobDetailUiState.Loading
                        // Trigger reload by changing state - in real app, would call viewModel
                    },
                    subtitle = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            is JobDetailUiState.Success -> {
                val job = state.job
                val displayStatus = currentStatus ?: job.status

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Status banner
                    StatusBanner(
                        status = displayStatus,
                        date = job.date,
                        timeSlot = job.timeSlot,
                        urgency = job.urgency,
                        expiresIn = job.expiresIn
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
                            photos = emptyList(),
                            instructions = job.accessNotes,
                            urgency = job.urgency,
                            availability = job.availability
                        )

                        // Access card
                        AccessCard(
                            address = job.address,
                            accessCode = job.accessCode,
                            accessNotes = job.accessNotes,
                            onOpenMaps = { /* Open maps */ }
                        )

                        // Price card (only if quote exists)
                        job.priceBreakdown?.let { breakdown ->
                            PriceCard(
                                laborPrice = breakdown.labor,
                                partsPrice = breakdown.parts,
                                totalPrice = breakdown.total,
                                isFixed = job.isFixed ?: true
                            )
                        } ?: run {
                            // No quote yet placeholder
                            NoQuoteCard()
                        }

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Completion bottom sheet
                if (showCompletionSheet) {
                    CompletionBottomSheet(
                        job = job,
                        onDismiss = { showCompletionSheet = false },
                        onComplete = {
                            showCompletionSheet = false
                            onComplete()
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBanner(
    status: JobStatus,
    date: String,
    timeSlot: String,
    urgency: String = "",
    expiresIn: String = ""
) {
    val (backgroundColor, textColor, statusText, icon) = when (status) {
        JobStatus.PENDING_QUOTE -> {
            listOf(
                ProviderPurple,
                Color.White,
                stringResource(R.string.job_status_pending_quote),
                Icons.Filled.NewReleases
            )
        }
        JobStatus.QUOTE_SENT -> {
            listOf(
                TrustBlue,
                Color.White,
                stringResource(R.string.job_status_quote_sent),
                Icons.Filled.Send
            )
        }
        JobStatus.QUOTE_ACCEPTED -> {
            listOf(
                WarningAmber,
                Color.White,
                stringResource(R.string.job_status_quote_accepted),
                Icons.Filled.ThumbUp
            )
        }
        JobStatus.CONFIRMED -> {
            listOf(
                SuccessGreen,
                Color.White,
                stringResource(R.string.job_status_confirmed),
                Icons.Filled.CheckCircle
            )
        }
        JobStatus.EN_ROUTE -> {
            listOf(
                TrustBlue,
                Color.White,
                stringResource(R.string.job_status_en_route),
                Icons.Filled.DirectionsCar
            )
        }
        JobStatus.IN_PROGRESS -> {
            listOf(
                WarningAmber,
                Color.White,
                stringResource(R.string.job_status_in_progress),
                Icons.Filled.Construction
            )
        }
        JobStatus.COMPLETED -> {
            listOf(
                Gray500,
                Color.White,
                stringResource(R.string.job_status_completed),
                Icons.Filled.Done
            )
        }
    }

    // Build the info text based on available data
    val infoText = when {
        date.isNotEmpty() && timeSlot.isNotEmpty() -> "$date, $timeSlot"
        date.isNotEmpty() -> date
        urgency.isNotEmpty() -> urgency
        else -> ""
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor as Color
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
                    contentDescription = null,
                    tint = textColor as Color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (infoText.isNotEmpty()) "$statusText — $infoText" else statusText as String,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }

            // Show expiration warning for pending quotes
            if (status == JobStatus.PENDING_QUOTE && expiresIn.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Expire dans $expiresIn",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
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
            Text(
                text = stringResource(R.string.job_detail_client),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ProviderPurple, ProviderPurpleDark)
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

                Column(modifier = Modifier.weight(1f)) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onMessage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Gray700),
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

                Button(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProviderPurple),
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
    instructions: String,
    urgency: String = "",
    availability: String = ""
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
            Text(
                text = stringResource(R.string.job_detail_job_info),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500
            )

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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
            }

            Text(
                text = description,
                fontSize = 15.sp,
                color = Gray700,
                lineHeight = 22.sp
            )

            // Urgency and availability (for early-stage jobs)
            if (urgency.isNotEmpty() || availability.isNotEmpty()) {
                Divider(color = Gray100, thickness = 1.dp)

                if (urgency.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.job_request_urgency),
                                fontSize = 12.sp,
                                color = Gray500
                            )
                            Text(
                                text = urgency,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = WarningAmberDark
                            )
                        }
                    }
                }

                if (availability.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EventAvailable,
                            contentDescription = null,
                            tint = Gray500,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.job_request_availability),
                                fontSize = 12.sp,
                                color = Gray500
                            )
                            Text(
                                text = availability,
                                fontSize = 14.sp,
                                color = Gray700
                            )
                        }
                    }
                }
            }

            if (photos.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(photos) { _ ->
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
            Text(
                text = stringResource(R.string.job_detail_access),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray500
            )

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
                Text(
                    text = address,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray900,
                    lineHeight = 22.sp,
                    modifier = Modifier.weight(1f)
                )
            }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.payment_labor), fontSize = 14.sp, color = Gray600)
                Text(text = "$laborPrice €", fontSize = 14.sp, color = Gray700)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.payment_parts), fontSize = 14.sp, color = Gray600)
                Text(text = "$partsPrice €", fontSize = 14.sp, color = Gray700)
            }

            Divider(color = Gray200, thickness = 1.dp)

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
private fun NoQuoteCard() {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.RequestQuote,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.job_detail_no_quote_yet),
                fontSize = 15.sp,
                color = Gray500,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ActionButton(
    status: JobStatus,
    jobId: String,
    onStatusChange: (JobStatus) -> Unit,
    onSendQuote: (String) -> Unit,
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
                JobStatus.PENDING_QUOTE -> {
                    Button(
                        onClick = { onSendQuote(jobId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProviderPurple),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.job_detail_action_send_quote),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
                JobStatus.QUOTE_SENT -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = TrustBlueLight
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HourglassTop,
                                contentDescription = null,
                                tint = TrustBlueDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.job_detail_quote_pending),
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = TrustBlueDark
                            )
                        }
                    }
                }
                JobStatus.QUOTE_ACCEPTED -> {
                    Button(
                        onClick = { onStatusChange(JobStatus.CONFIRMED) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EventAvailable,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.job_detail_action_confirm),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
                JobStatus.CONFIRMED -> {
                    Button(
                        onClick = { onStatusChange(JobStatus.EN_ROUTE) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TrustBlue),
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
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
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
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
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
            jobId = "job5",
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
            StatusBanner(JobStatus.PENDING_QUOTE, "", "", "Dès que possible", "2h")
            StatusBanner(JobStatus.QUOTE_SENT, "Mercredi 22 janvier", "9h - 12h", "", "")
            StatusBanner(JobStatus.CONFIRMED, "Lundi 20 janvier", "14h-17h", "", "")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoQuoteCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NoQuoteCard()
        }
    }
}
