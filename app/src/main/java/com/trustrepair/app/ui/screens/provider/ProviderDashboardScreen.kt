package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.trustrepair.app.ui.components.debouncedClickableWithRipple
import com.trustrepair.app.ui.theme.*

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
                onNotificationClick = onNotificationClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Earnings Hero Card
            EarningsHeroCard(
                earnedThisMonth = stats.earnedThisMonth,
                jobsCompleted = stats.jobsCompleted,
                averageRating = stats.averageRating,
                responseRate = 95,
                trendPercentage = 12,
                onCardClick = onEarningsTab
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pipelineStages.forEach { stage ->
                    val count = jobCountsByStatus[stage.status] ?: 0
                    PipelineStageItem(
                        stage = stage,
                        count = count
                    )
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
    onNotificationClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

@Composable
private fun EarningsHeroCard(
    earnedThisMonth: Int,
    jobsCompleted: Int,
    averageRating: Float,
    responseRate: Int,
    trendPercentage: Int,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(ProviderPurple, ProviderPurpleDark)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary earnings amount
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${java.text.NumberFormat.getInstance(java.util.Locale.FRANCE).format(earnedThisMonth)} €",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Trend indicator chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "$trendPercentage% vs mois dernier",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Secondary stats row
                Text(
                    text = "$jobsCompleted travaux • $averageRating ★ • $responseRate% réponse",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
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
            .debouncedClickableWithRipple(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
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
                        Text(
                            text = job.jobType,
                            fontSize = 14.sp,
                            color = Gray500
                        )
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
                            text = if (job.timeSlot.isNotEmpty()) "${job.date}, ${job.timeSlot}" else job.date,
                            fontSize = 13.sp,
                            color = Gray600
                        )
                    }
                }

                // Expires badge for pending quotes
                if (job.status == JobStatus.PENDING_QUOTE && job.expiresIn.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ErrorRedLight
                    ) {
                        Text(
                            text = "Expire dans ${job.expiresIn}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ErrorRed
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
