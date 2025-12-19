package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.ActiveJob
import com.trustrepair.app.data.JobStatus
import com.trustrepair.app.data.demoActiveJobs
import com.trustrepair.app.ui.theme.*

// Filter options
private enum class JobFilter(val labelResId: Int) {
    ALL(R.string.active_jobs_filter_all),
    TODAY(R.string.active_jobs_filter_today),
    THIS_WEEK(R.string.active_jobs_filter_week),
    UPCOMING(R.string.active_jobs_filter_upcoming)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveJobsScreen(
    onBack: () -> Unit,
    onJobClick: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(JobFilter.ALL) }

    // Filter jobs based on selection (demo: just show all for now)
    val filteredJobs = when (selectedFilter) {
        JobFilter.ALL -> demoActiveJobs
        JobFilter.TODAY -> demoActiveJobs.filter { it.date.contains("Aujourd'hui", ignoreCase = true) }
        JobFilter.THIS_WEEK -> demoActiveJobs // In real app, filter by date
        JobFilter.UPCOMING -> demoActiveJobs.filter { it.status != JobStatus.COMPLETED }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.active_jobs_title),
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
        ) {
            // Filter chips row
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // Jobs list or empty state
            if (filteredJobs.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredJobs, key = { it.id }) { job ->
                        ActiveJobCard(
                            job = job,
                            onClick = { onJobClick(job.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: JobFilter,
    onFilterSelected: (JobFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobFilter.entries.forEach { filter ->
            FilterChip(
                filter = filter,
                isSelected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    filter: JobFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ProviderPurple else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Gray300)
    ) {
        Text(
            text = stringResource(filter.labelResId),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else Gray700
        )
    }
}

@Composable
private fun ActiveJobCard(
    job: ActiveJob,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top row: Client info + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Client info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Client avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
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
                            fontSize = 16.sp
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
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

            // Description
            Text(
                text = job.description,
                fontSize = 14.sp,
                color = Gray600,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            // Date and time row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${job.date}, ${job.timeSlot}",
                    fontSize = 13.sp,
                    color = Gray600
                )
            }

            // Address row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = job.address,
                    fontSize = 13.sp,
                    color = Gray500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Illustration placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Gray100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Work,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = stringResource(R.string.active_jobs_empty),
                fontSize = 16.sp,
                color = Gray500,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActiveJobsScreenPreview() {
    TrustRepairTheme {
        ActiveJobsScreen(
            onBack = {},
            onJobClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChipsRowPreview() {
    TrustRepairTheme {
        FilterChipsRow(
            selectedFilter = JobFilter.ALL,
            onFilterSelected = {}
        )
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

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    TrustRepairTheme {
        EmptyState()
    }
}
