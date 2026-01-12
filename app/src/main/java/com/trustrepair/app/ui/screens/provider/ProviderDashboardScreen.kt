package com.trustrepair.app.ui.screens.provider

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.*
import com.trustrepair.app.ui.components.JobTypeIcon
import com.trustrepair.app.ui.components.debouncedClickableWithRipple
import com.trustrepair.app.ui.theme.*

/**
 * Parses the expiresIn string to extract minutes.
 * Supports formats like "2h", "30 min", "1h30", "25 min", "1h15"
 */
private fun parseExpiresInMinutes(expiresIn: String): Int {
    val cleaned = expiresIn.lowercase().trim()

    // Try to match "Xh" or "XhY" or "XhYm" patterns
    val hourMinuteRegex = Regex("""(\d+)h\s*(\d*)""")
    val hourMinuteMatch = hourMinuteRegex.find(cleaned)
    if (hourMinuteMatch != null) {
        val hours = hourMinuteMatch.groupValues[1].toIntOrNull() ?: 0
        val minutes = hourMinuteMatch.groupValues[2].toIntOrNull() ?: 0
        return hours * 60 + minutes
    }

    // Try to match "X min" or "Xmin" patterns
    val minuteRegex = Regex("""(\d+)\s*min""")
    val minuteMatch = minuteRegex.find(cleaned)
    if (minuteMatch != null) {
        return minuteMatch.groupValues[1].toIntOrNull() ?: Int.MAX_VALUE
    }

    // Default to a large number if parsing fails
    return Int.MAX_VALUE
}

// Bottom navigation items
private enum class ProviderNavItem(
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    REQUESTS(R.string.provider_nav_requests, Icons.Filled.Home, Icons.Outlined.Home),
    JOBS(R.string.provider_nav_jobs, Icons.Filled.Work, Icons.Outlined.Work),
    EARNINGS(R.string.provider_nav_earnings, Icons.Filled.Wallet, Icons.Outlined.Wallet),
    PROFILE(R.string.provider_nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

// Pipeline stage for visual widget
private data class PipelineStage(
    val status: JobStatus,
    val labelResId: Int,
    val color: Color,
    val bgColor: Color
)

private val pipelineStages = listOf(
    PipelineStage(JobStatus.PENDING_QUOTE, R.string.job_status_pending_quote, ProviderPurple, ProviderPurpleLight),
    PipelineStage(JobStatus.QUOTE_SENT, R.string.job_status_quote_sent, TrustBlue, TrustBlueLight),
    PipelineStage(JobStatus.QUOTE_ACCEPTED, R.string.job_status_quote_accepted, WarningAmber, WarningAmberLight),
    PipelineStage(JobStatus.CONFIRMED, R.string.job_status_confirmed, SuccessGreen, SuccessGreenLight),
    PipelineStage(JobStatus.IN_PROGRESS, R.string.job_status_in_progress, WarningAmber, WarningAmberLight)
)

// Availability status options
private enum class AvailabilityStatus {
    AVAILABLE,
    UNAVAILABLE_TODAY,
    UNAVAILABLE_UNTIL
}

@Composable
fun ProviderDashboardScreen(
    onJobRequestClick: (String) -> Unit,
    onActiveJobClick: (String) -> Unit,
    onActiveJobsTab: () -> Unit,
    onEarningsTab: () -> Unit,
    onProfileTab: () -> Unit,
    onNotificationClick: () -> Unit,
    onLogout: () -> Unit
) {
    val provider = currentProvider
    val stats = demoProviderStats
    val allJobs = demoActiveJobs

    // Count jobs by status
    val jobCountsByStatus = allJobs.groupingBy { it.status }.eachCount()

    // Get urgent/recent jobs (excluding completed)
    val activeJobs = allJobs.filter { it.status != JobStatus.COMPLETED }

    var selectedNavItem by remember { mutableStateOf(ProviderNavItem.REQUESTS) }

    // Availability state
    var availabilityStatus by remember { mutableStateOf(AvailabilityStatus.AVAILABLE) }
    var showAvailabilitySheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Gray50,
        bottomBar = {
            ProviderBottomNavigation(
                selectedItem = selectedNavItem,
                onItemSelected = { item ->
                    selectedNavItem = item
                    when (item) {
                        ProviderNavItem.REQUESTS -> { /* Already here */ }
                        ProviderNavItem.JOBS -> onActiveJobsTab()
                        ProviderNavItem.EARNINGS -> onEarningsTab()
                        ProviderNavItem.PROFILE -> onProfileTab()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            DashboardHeader(
                firstName = provider.firstName,
                initials = "${provider.firstName.first()}${provider.lastName.first()}",
                avatarColor = provider.avatarColor,
                availabilityStatus = availabilityStatus,
                onAvailabilityClick = { showAvailabilitySheet = true },
                onNotificationClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Grid (2x2)
            StatsGrid(
                earnedThisMonth = stats.earnedThisMonth,
                jobsCompleted = stats.jobsCompleted,
                averageRating = stats.averageRating,
                onEarningsClick = onEarningsTab
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pipeline widget
            PipelineWidget(
                jobCountsByStatus = jobCountsByStatus,
                onStageClick = { onActiveJobsTab() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Active jobs list
            if (activeJobs.isNotEmpty()) {
                SectionHeader(
                    title = "Travaux actifs",
                    count = activeJobs.size,
                    onSeeAllClick = onActiveJobsTab
                )

                activeJobs.take(5).forEach { job ->
                    DashboardJobCard(
                        job = job,
                        onClick = { onActiveJobClick(job.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Availability bottom sheet
    if (showAvailabilitySheet) {
        AvailabilityBottomSheet(
            currentStatus = availabilityStatus,
            onStatusSelected = { status ->
                availabilityStatus = status
                showAvailabilitySheet = false
            },
            onDismiss = { showAvailabilitySheet = false }
        )
    }
}

@Composable
private fun PipelineWidget(
    jobCountsByStatus: Map<JobStatus, Int>,
    onStageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onStageClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pipeline",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Gray400
                )
            }

            // Pipeline stages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pipelineStages.forEachIndexed { index, stage ->
                    val count = jobCountsByStatus[stage.status] ?: 0
                    PipelineStageItem(
                        stage = stage,
                        count = count
                    )

                    // Arrow between stages (except after last)
                    if (index < pipelineStages.lastIndex) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Gray400,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Visual flow indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pipelineStages.forEachIndexed { index, stage ->
                    val count = jobCountsByStatus[stage.status] ?: 0

                    // Stage dot
                    Box(
                        modifier = Modifier
                            .size(if (count > 0) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (count > 0) stage.color else Gray300)
                    )

                    // Connector line (except after last)
                    if (index < pipelineStages.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Gray200)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PipelineStageItem(
    stage: PipelineStage,
    count: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(64.dp)
    ) {
        // Count circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (count > 0) stage.bgColor else Gray100),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (count > 0) stage.color else Gray400
            )
        }

        // Label
        Text(
            text = stringResource(stage.labelResId),
            fontSize = 11.sp,
            color = if (count > 0) Gray700 else Gray400,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardHeader(
    firstName: String,
    initials: String,
    avatarColor: Color,
    availabilityStatus: AvailabilityStatus,
    onAvailabilityClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val isAvailable = availabilityStatus == AvailabilityStatus.AVAILABLE

    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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

                    // Greeting
                    Text(
                        text = stringResource(R.string.provider_greeting, firstName),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900
                    )
                }

                // Right side: Availability chip + Notification bell
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Availability chip
                    FilterChip(
                        selected = isAvailable,
                        onClick = onAvailabilityClick,
                        label = {
                            Text(
                                text = stringResource(
                                    if (isAvailable) R.string.availability_available
                                    else R.string.availability_unavailable
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isAvailable) Icons.Filled.Check else Icons.Filled.Block,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Gray100,
                            labelColor = Gray600,
                            iconColor = Gray500,
                            selectedContainerColor = SuccessGreenLight,
                            selectedLabelColor = SuccessGreenDark,
                            selectedLeadingIconColor = SuccessGreen
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent,
                            selectedBorderColor = SuccessGreen
                        )
                    )

                    // Notification bell
                    IconButton(onClick = onNotificationClick) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = ErrorRed,
                                    contentColor = Color.White
                                ) {
                                    Text("2")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Gray700,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvailabilityBottomSheet(
    currentStatus: AvailabilityStatus,
    onStatusSelected: (AvailabilityStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.availability_manage),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Available now option
            AvailabilityOption(
                title = stringResource(R.string.availability_now),
                isSelected = currentStatus == AvailabilityStatus.AVAILABLE,
                color = SuccessGreen,
                icon = Icons.Filled.CheckCircle,
                onClick = { onStatusSelected(AvailabilityStatus.AVAILABLE) }
            )

            // Unavailable today option
            AvailabilityOption(
                title = stringResource(R.string.availability_today),
                isSelected = currentStatus == AvailabilityStatus.UNAVAILABLE_TODAY,
                color = WarningAmber,
                icon = Icons.Filled.PauseCircle,
                onClick = { onStatusSelected(AvailabilityStatus.UNAVAILABLE_TODAY) }
            )

            // Unavailable until option
            AvailabilityOption(
                title = stringResource(R.string.availability_until),
                isSelected = currentStatus == AvailabilityStatus.UNAVAILABLE_UNTIL,
                color = Gray500,
                icon = Icons.Filled.EventBusy,
                onClick = { onStatusSelected(AvailabilityStatus.UNAVAILABLE_UNTIL) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Manage schedule link
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = ProviderPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.availability_manage),
                    color = ProviderPurple,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AvailabilityOption(
    title: String,
    isSelected: Boolean,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.1f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else Gray200
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else Gray400,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) color else Gray700,
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = color,
                    unselectedColor = Gray300
                )
            )
        }
    }
}

@Composable
private fun StatsGrid(
    earnedThisMonth: Int,
    jobsCompleted: Int,
    averageRating: Float,
    onEarningsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First row: Earnings and Jobs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Ce mois",
                value = earnedThisMonth,
                suffix = " €",
                icon = Icons.Filled.TrendingUp,
                gradientColors = listOf(SuccessGreenLight, Color.White),
                iconTint = SuccessGreen,
                onClick = onEarningsClick
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Travaux",
                value = jobsCompleted,
                suffix = "",
                icon = Icons.Filled.CheckCircle,
                gradientColors = listOf(ProviderPurpleLight, Color.White),
                iconTint = ProviderPurple,
                onClick = {}
            )
        }

        // Second row: Rating and Response Rate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Note moyenne",
                floatValue = averageRating,
                suffix = " ★",
                icon = Icons.Filled.Star,
                gradientColors = listOf(WarningAmberLight, Color.White),
                iconTint = WarningAmber,
                onClick = {}
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Réponse",
                value = 95,
                suffix = "%",
                icon = Icons.Filled.Speed,
                gradientColors = listOf(TrustBlueLight, Color.White),
                iconTint = TrustBlue,
                onClick = {}
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Int = 0,
    floatValue: Float? = null,
    suffix: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    iconTint: Color,
    onClick: () -> Unit
) {
    // Animate the value on first render using Animatable
    val animatedValue = remember { Animatable(0f) }
    val animatedFloatValue = remember { Animatable(0f) }

    LaunchedEffect(value) {
        animatedValue.animateTo(
            targetValue = value.toFloat(),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(floatValue) {
        animatedFloatValue.animateTo(
            targetValue = floatValue ?: 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = gradientColors)
                )
        ) {
            // Background icon (top-left, subtle)
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp)
                    .align(Alignment.TopStart),
                tint = iconTint.copy(alpha = 0.3f)
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gray500
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Value
                Text(
                    text = if (floatValue != null) {
                        "${String.format("%.1f", animatedFloatValue.value)}$suffix"
                    } else {
                        "${java.text.NumberFormat.getInstance(java.util.Locale.FRANCE).format(animatedValue.value.toInt())}$suffix"
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )

            Surface(
                shape = CircleShape,
                color = ProviderPurple
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        TextButton(onClick = onSeeAllClick) {
            Text(
                text = "Voir tout",
                fontSize = 14.sp,
                color = ProviderPurple
            )
        }
    }
}

@Composable
private fun DashboardJobCard(
    job: ActiveJob,
    onClick: () -> Unit
) {
    val isEarlyStage = job.status in listOf(
        JobStatus.PENDING_QUOTE,
        JobStatus.QUOTE_SENT,
        JobStatus.QUOTE_ACCEPTED
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = ProviderPurple),
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top row: Client info + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Client avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ProviderPurple, ProviderPurpleDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = job.client.initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Column {
                        Text(
                            text = job.client.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            JobTypeIcon(
                                jobType = job.jobType,
                                modifier = Modifier.size(20.dp),
                                tint = Gray500
                            )
                            Text(
                                text = job.jobType,
                                fontSize = 14.sp,
                                color = Gray500
                            )
                        }
                    }
                }

                // Status badge (use the shared one)
                StatusBadge(status = job.status)
            }

            // Description
            Text(
                text = job.description,
                fontSize = 14.sp,
                color = Gray600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date or urgency
                if (isEarlyStage && job.urgency.isNotEmpty()) {
                    Row(
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
                            text = job.urgency,
                            fontSize = 13.sp,
                            color = WarningAmberDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (job.date.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = Gray400,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = job.displayDate,
                            fontSize = 13.sp,
                            color = Gray600
                        )
                    }
                }

                // Expires badge for pending quotes with pulsing animation when urgent
                if (job.status == JobStatus.PENDING_QUOTE && job.expiresIn.isNotEmpty()) {
                    val minutesRemaining = parseExpiresInMinutes(job.expiresIn)
                    val isUrgent = minutesRemaining < 30

                    // Pulsing animation for urgent badges
                    val infiniteTransition = rememberInfiniteTransition(label = "urgency_pulse")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = if (isUrgent) 1.15f else 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = if (isUrgent) 0.7f else 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )

                    Surface(
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isUrgent) WarningAmberLight else ErrorRedLight
                    ) {
                        Text(
                            text = "Expire dans ${job.expiresIn}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isUrgent) WarningAmberDark else ErrorRed
                        )
                    }
                }

                // Price if available
                job.priceBreakdown?.let { breakdown ->
                    Text(
                        text = "${breakdown.total} €",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SuccessGreenDark
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderBottomNavigation(
    selectedItem: ProviderNavItem,
    onItemSelected: (ProviderNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        ProviderNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = if (selectedItem == item) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.labelResId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelResId),
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ProviderPurple,
                    selectedTextColor = ProviderPurple,
                    unselectedIconColor = Gray400,
                    unselectedTextColor = Gray400,
                    indicatorColor = ProviderPurple.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProviderDashboardScreenPreview() {
    TrustRepairTheme {
        ProviderDashboardScreen(
            onJobRequestClick = {},
            onActiveJobClick = {},
            onActiveJobsTab = {},
            onEarningsTab = {},
            onProfileTab = {},
            onNotificationClick = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PipelineWidgetPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PipelineWidget(
                jobCountsByStatus = mapOf(
                    JobStatus.PENDING_QUOTE to 2,
                    JobStatus.QUOTE_SENT to 1,
                    JobStatus.QUOTE_ACCEPTED to 1,
                    JobStatus.CONFIRMED to 1,
                    JobStatus.IN_PROGRESS to 1
                ),
                onStageClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardJobCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DashboardJobCard(
                job = demoActiveJobs[0],
                onClick = {}
            )
        }
    }
}
