package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
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
import com.trustrepair.app.ui.components.debouncedClickableWithRipple
import com.trustrepair.app.ui.theme.*

// Pipeline filter options
private enum class PipelineFilter(val labelResId: Int, val statuses: List<JobStatus>) {
    ALL(R.string.active_jobs_filter_all, JobStatus.entries),
    NEW(R.string.active_jobs_filter_new, listOf(JobStatus.PENDING_QUOTE)),
    QUOTES(R.string.active_jobs_filter_quotes, listOf(JobStatus.QUOTE_SENT, JobStatus.QUOTE_ACCEPTED)),
    SCHEDULED(R.string.active_jobs_filter_scheduled, listOf(JobStatus.CONFIRMED)),
    ACTIVE(R.string.active_jobs_filter_active, listOf(JobStatus.EN_ROUTE, JobStatus.IN_PROGRESS)),
    COMPLETED(R.string.active_jobs_filter_completed, listOf(JobStatus.COMPLETED))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveJobsScreen(
    onBack: () -> Unit,
    onJobClick: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(PipelineFilter.ALL) }

    // Filter jobs based on pipeline stage
    val filteredJobs = demoActiveJobs.filter { job ->
        job.status in selectedFilter.statuses
    }

    // Count jobs per filter for badges
    val jobCounts = PipelineFilter.entries.associateWith { filter ->
        demoActiveJobs.count { it.status in filter.statuses }
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
            // Pipeline filter chips row
            PipelineFilterRow(
                selectedFilter = selectedFilter,
                jobCounts = jobCounts,
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
                        PipelineJobCard(
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
private fun PipelineFilterRow(
    selectedFilter: PipelineFilter,
    jobCounts: Map<PipelineFilter, Int>,
    onFilterSelected: (PipelineFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PipelineFilter.entries.forEach { filter ->
            PipelineFilterChip(
                filter = filter,
                count = jobCounts[filter] ?: 0,
                isSelected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun PipelineFilterChip(
    filter: PipelineFilter,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ProviderPurple else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Gray300)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(filter.labelResId),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else Gray700
            )
            // Show count badge (except for ALL)
            if (filter != PipelineFilter.ALL && count > 0) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color.White.copy(alpha = 0.2f) else Gray200
                ) {
                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else Gray600
                    )
                }
            }
        }
    }
}

@Composable
private fun PipelineJobCard(
    job: ActiveJob,
    onClick: () -> Unit
) {
    val isEarlyStage = job.status in listOf(
        JobStatus.PENDING_QUOTE,
        JobStatus.QUOTE_SENT,
        JobStatus.QUOTE_ACCEPTED
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickableWithRipple(onClick = onClick),
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
                                    colors = listOf(ProviderPurple, ProviderPurpleDark)
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

            // Date/time OR urgency info depending on stage
            if (isEarlyStage) {
                // For early stages: show urgency and time info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (job.urgency.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = WarningAmberDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = job.urgency,
                                fontSize = 13.sp,
                                color = WarningAmberDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (job.expiresIn.isNotEmpty() && job.status == JobStatus.PENDING_QUOTE) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Expire dans ${job.expiresIn}",
                                fontSize = 13.sp,
                                color = ErrorRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else if (job.date.isNotEmpty()) {
                // For scheduled jobs: show date and time
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
                        text = if (job.timeSlot.isNotEmpty()) "${job.date}, ${job.timeSlot}" else job.date,
                        fontSize = 13.sp,
                        color = Gray600
                    )
                }
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

            // Price row (if quote exists)
            job.priceBreakdown?.let { breakdown ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Euro,
                        contentDescription = null,
                        tint = SuccessGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${breakdown.total} €",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SuccessGreenDark
                    )
                    if (job.isFixed == true) {
                        Text(
                            text = "(prix fixe)",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: JobStatus) {
    val (backgroundColor, textColor, textResId) = when (status) {
        JobStatus.PENDING_QUOTE -> Triple(
            ProviderPurpleLight,
            ProviderPurple,
            R.string.job_status_pending_quote
        )
        JobStatus.QUOTE_SENT -> Triple(
            TrustBlueLight,
            TrustBlueDark,
            R.string.job_status_quote_sent
        )
        JobStatus.QUOTE_ACCEPTED -> Triple(
            WarningAmberLight,
            WarningAmberDark,
            R.string.job_status_quote_accepted
        )
        JobStatus.CONFIRMED -> Triple(
            SuccessGreenLight,
            SuccessGreenDark,
            R.string.job_status_confirmed
        )
        JobStatus.EN_ROUTE -> Triple(
            TrustBlueLight,
            TrustBlueDark,
            R.string.job_status_en_route
        )
        JobStatus.IN_PROGRESS -> Triple(
            WarningAmberLight,
            WarningAmberDark,
            R.string.job_status_in_progress
        )
        JobStatus.COMPLETED -> Triple(
            Gray200,
            Gray700,
            R.string.job_status_completed
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = stringResource(textResId),
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
                text = stringResource(R.string.active_jobs_empty_filter),
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
private fun PipelineFilterRowPreview() {
    TrustRepairTheme {
        PipelineFilterRow(
            selectedFilter = PipelineFilter.ALL,
            jobCounts = mapOf(
                PipelineFilter.ALL to 7,
                PipelineFilter.NEW to 2,
                PipelineFilter.QUOTES to 2,
                PipelineFilter.SCHEDULED to 1,
                PipelineFilter.ACTIVE to 1,
                PipelineFilter.COMPLETED to 1
            ),
            onFilterSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PipelineJobCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PipelineJobCard(
                job = demoActiveJobs[0],
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreview() {
    TrustRepairTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusBadge(status = JobStatus.PENDING_QUOTE)
            StatusBadge(status = JobStatus.QUOTE_SENT)
            StatusBadge(status = JobStatus.CONFIRMED)
        }
    }
}
