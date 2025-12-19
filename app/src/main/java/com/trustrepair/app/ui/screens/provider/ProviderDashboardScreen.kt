package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.*
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

@Composable
fun ProviderDashboardScreen(
    onJobRequestClick: (String) -> Unit,
    onActiveJobClick: (String) -> Unit,
    onActiveJobsTab: () -> Unit,
    onEarningsTab: () -> Unit,
    onProfileTab: () -> Unit,
    onLogout: () -> Unit
) {
    val provider = currentProvider
    val stats = demoProviderStats
    val jobRequests = demoJobRequests
    val activeJobs = demoActiveJobs

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
                onNotificationClick = { /* Notification action */ }
            )

            // Stats row
            StatsRow(
                earnedThisMonth = stats.earnedThisMonth,
                pendingAmount = stats.pendingAmount,
                averageRating = stats.averageRating
            )

            Spacer(modifier = Modifier.height(24.dp))

            // New Requests Section
            if (jobRequests.isNotEmpty()) {
                SectionHeader(
                    title = stringResource(R.string.provider_new_requests),
                    count = jobRequests.size
                )

                jobRequests.forEach { request ->
                    JobRequestCard(
                        request = request,
                        onClick = { onJobRequestClick(request.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Jobs Section
            if (activeJobs.isNotEmpty()) {
                SectionHeader(
                    title = stringResource(R.string.provider_active_jobs),
                    count = activeJobs.size
                )

                activeJobs.forEach { job ->
                    ActiveJobCard(
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
private fun StatsRow(
    earnedThisMonth: Int,
    pendingAmount: Int,
    averageRating: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Earned this month
        StatCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.provider_stats_month),
            value = "$earnedThisMonth €",
            iconBackgroundColor = ProviderPurple.copy(alpha = 0.1f),
            icon = Icons.Filled.TrendingUp,
            iconTint = ProviderPurple
        )

        // Pending
        StatCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.provider_stats_pending),
            value = "$pendingAmount €",
            iconBackgroundColor = WarningAmberLight,
            icon = Icons.Filled.Schedule,
            iconTint = WarningAmber
        )

        // Rating
        StatCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.provider_stats_rating),
            value = "$averageRating ★",
            iconBackgroundColor = StarYellow.copy(alpha = 0.2f),
            icon = Icons.Filled.Star,
            iconTint = StarYellow
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    iconBackgroundColor: Color,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
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

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            Text(
                text = label,
                fontSize = 12.sp,
                color = Gray500
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Gray900
        )

        // Count badge
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
}

@Composable
private fun JobRequestCard(
    request: JobRequest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top row: Job type icon + title + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Job type icon
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

                    Column {
                        Text(
                            text = request.jobType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray900
                        )
                        Text(
                            text = request.client.name,
                            fontSize = 14.sp,
                            color = Gray500
                        )
                    }
                }

                // Time ago
                Text(
                    text = stringResource(R.string.provider_time_ago, request.receivedAgo),
                    fontSize = 12.sp,
                    color = Gray400
                )
            }

            // Description
            Text(
                text = request.description,
                fontSize = 14.sp,
                color = Gray600,
                lineHeight = 20.sp,
                maxLines = 2
            )

            // Bottom row: Location + distance + expires
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${request.location} (${request.distanceKm} km)",
                        fontSize = 13.sp,
                        color = Gray500
                    )
                }

                // Expires badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarningAmberLight
                ) {
                    Text(
                        text = stringResource(R.string.provider_expires_in, request.expiresIn),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = WarningAmberDark
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveJobCard(
    job: ActiveJob,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
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
                                    colors = listOf(ProviderPurple, ProviderPurple.copy(alpha = 0.7f))
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

                // Status badge
                StatusBadge(status = job.status)
            }

            // Date and time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${job.date}, ${job.timeSlot}",
                    fontSize = 14.sp,
                    color = Gray600
                )
            }

            // Address (truncated)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = job.address,
                    fontSize = 14.sp,
                    color = Gray500,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: JobStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        JobStatus.CONFIRMED -> Triple(
            SuccessGreenLight,
            SuccessGreenDark,
            stringResource(R.string.job_status_confirmed)
        )
        JobStatus.EN_ROUTE -> Triple(
            TrustBlueLight,
            TrustBlueDark,
            stringResource(R.string.job_status_en_route)
        )
        JobStatus.IN_PROGRESS -> Triple(
            WarningAmberLight,
            WarningAmberDark,
            stringResource(R.string.job_status_in_progress)
        )
        JobStatus.COMPLETED -> Triple(
            Gray200,
            Gray700,
            stringResource(R.string.job_status_completed)
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
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
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardHeaderPreview() {
    TrustRepairTheme {
        DashboardHeader(
            firstName = "Karim",
            initials = "KD",
            avatarColor = ProviderGreen,
            onNotificationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsRowPreview() {
    TrustRepairTheme {
        StatsRow(
            earnedThisMonth = 2450,
            pendingAmount = 180,
            averageRating = 4.9f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JobRequestCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            JobRequestCard(
                request = demoJobRequests[0],
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveJobCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ActiveJobCard(
                job = demoActiveJobs[0],
                onClick = {}
            )
        }
    }
}
