package com.trustrepair.app.ui.screens.provider

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.*
import com.trustrepair.app.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// Bottom navigation items for calendar screen
private enum class CalendarNavItem(
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    REQUESTS(R.string.provider_nav_requests, Icons.Filled.Home, Icons.Outlined.Home),
    JOBS(R.string.provider_nav_jobs, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    EARNINGS(R.string.provider_nav_earnings, Icons.Filled.Wallet, Icons.Outlined.Wallet),
    PROFILE(R.string.provider_nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onJobClick: (String) -> Unit,
    onDashboardTab: () -> Unit,
    onEarningsTab: () -> Unit,
    onProfileTab: () -> Unit
) {
    val context = LocalContext.current
    var selectedNavItem by remember { mutableStateOf(CalendarNavItem.JOBS) }

    // Current week state
    var currentWeekStart by remember {
        mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY))
    }

    val jobs = demoActiveJobs.filter {
        it.status in listOf(
            JobStatus.CONFIRMED,
            JobStatus.QUOTE_SENT,
            JobStatus.IN_PROGRESS,
            JobStatus.QUOTE_ACCEPTED
        )
    }

    Scaffold(
        containerColor = Gray50,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.calendar_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            CalendarBottomNavigation(
                selectedItem = selectedNavItem,
                onItemSelected = { item ->
                    selectedNavItem = item
                    when (item) {
                        CalendarNavItem.REQUESTS -> onDashboardTab()
                        CalendarNavItem.JOBS -> { /* Already here */ }
                        CalendarNavItem.EARNINGS -> onEarningsTab()
                        CalendarNavItem.PROFILE -> onProfileTab()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Vue mois à venir", Toast.LENGTH_SHORT).show()
                },
                containerColor = ProviderPurple,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarViewMonth,
                    contentDescription = "Toggle month view"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Week navigation header
            WeekNavigationHeader(
                weekStart = currentWeekStart,
                onPreviousWeek = { currentWeekStart = currentWeekStart.minusWeeks(1) },
                onNextWeek = { currentWeekStart = currentWeekStart.plusWeeks(1) }
            )

            // Calendar grid
            WeekCalendarGrid(
                weekStart = currentWeekStart,
                jobs = jobs,
                onJobClick = onJobClick
            )
        }
    }
}

@Composable
private fun WeekNavigationHeader(
    weekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val weekEnd = weekStart.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH)
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

    val weekRangeText = if (weekStart.month == weekEnd.month) {
        "${weekStart.dayOfMonth} - ${formatter.format(weekEnd)} ${yearFormatter.format(weekEnd)}"
    } else {
        "${formatter.format(weekStart)} - ${formatter.format(weekEnd)} ${yearFormatter.format(weekEnd)}"
    }

    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousWeek) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Previous week",
                    tint = Gray700
                )
            }

            Text(
                text = weekRangeText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )

            IconButton(onClick = onNextWeek) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next week",
                    tint = Gray700
                )
            }
        }
    }
}

@Composable
private fun WeekCalendarGrid(
    weekStart: LocalDate,
    jobs: List<ActiveJob>,
    onJobClick: (String) -> Unit
) {
    val today = LocalDate.now()
    val timeSlots = (8..18).toList() // 8h to 18h (last slot starts at 18h, ends at 19h)
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }

    val hourHeight = 60.dp
    val dayColumnWidth = 100.dp
    val timeColumnWidth = 40.dp

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {
        // Time column (fixed)
        Column(
            modifier = Modifier
                .width(timeColumnWidth)
                .verticalScroll(rememberScrollState())
        ) {
            // Empty header cell
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(Gray50)
            )

            // Time labels
            timeSlots.forEach { hour ->
                Box(
                    modifier = Modifier
                        .height(hourHeight)
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = "${hour}h",
                        fontSize = 11.sp,
                        color = Gray500
                    )
                }
            }
        }

        // Day columns
        days.forEach { day ->
            val isToday = day == today
            val dayJobs = getJobsForDay(jobs, day)

            DayColumn(
                date = day,
                isToday = isToday,
                jobs = dayJobs,
                timeSlots = timeSlots,
                hourHeight = hourHeight,
                columnWidth = dayColumnWidth,
                onJobClick = onJobClick
            )
        }
    }
}

@Composable
private fun DayColumn(
    date: LocalDate,
    isToday: Boolean,
    jobs: List<ActiveJob>,
    timeSlots: List<Int>,
    hourHeight: Dp,
    columnWidth: Dp,
    onJobClick: (String) -> Unit
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.FRENCH)
        .replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .width(columnWidth)
            .border(
                width = 0.5.dp,
                color = Gray200
            )
    ) {
        // Day header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (isToday) ProviderPurple.copy(alpha = 0.1f) else Color.White)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayName,
                fontSize = 12.sp,
                color = if (isToday) ProviderPurple else Gray500,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .then(
                        if (isToday) {
                            Modifier
                                .clip(CircleShape)
                                .background(ProviderPurple)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isToday) Color.White else Gray900
                )
            }
        }

        // Time slots with jobs
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeight * timeSlots.size)
        ) {
            // Grid lines
            Column {
                timeSlots.forEach { _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hourHeight)
                            .border(
                                width = 0.5.dp,
                                color = Gray100
                            )
                    )
                }
            }

            // Job blocks
            jobs.forEach { job ->
                val startHour = parseStartHour(job.timeSlot)
                val duration = parseDuration(job.timeSlot)

                if (startHour != null) {
                    val topOffset = (startHour - 8) * hourHeight.value
                    val blockHeight = duration * hourHeight.value

                    JobBlock(
                        job = job,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .offset(y = topOffset.dp)
                            .height(blockHeight.dp)
                            .fillMaxWidth(),
                        onClick = { onJobClick(job.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun JobBlock(
    job: ActiveJob,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when (job.status) {
        JobStatus.CONFIRMED -> SuccessGreen
        JobStatus.QUOTE_SENT -> WarningAmber
        JobStatus.IN_PROGRESS -> TrustBlue
        JobStatus.QUOTE_ACCEPTED -> WarningAmber
        else -> Gray400
    }

    val abbreviatedJobType = when {
        job.jobType.length <= 6 -> job.jobType
        else -> job.jobType.take(5) + "."
    }

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = job.client.initials,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = abbreviatedJobType,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CalendarBottomNavigation(
    selectedItem: CalendarNavItem,
    onItemSelected: (CalendarNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        CalendarNavItem.entries.forEach { item ->
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

// Helper functions to parse time slots
private fun parseStartHour(timeSlot: String): Int? {
    // Parse formats like "14h - 17h" or "9h - 12h"
    val regex = Regex("""(\d{1,2})h""")
    val match = regex.find(timeSlot)
    return match?.groupValues?.get(1)?.toIntOrNull()
}

private fun parseDuration(timeSlot: String): Int {
    // Parse formats like "14h - 17h" and return duration in hours
    val regex = Regex("""(\d{1,2})h\s*-\s*(\d{1,2})h""")
    val match = regex.find(timeSlot)
    return if (match != null) {
        val start = match.groupValues[1].toIntOrNull() ?: 0
        val end = match.groupValues[2].toIntOrNull() ?: 0
        (end - start).coerceAtLeast(1)
    } else {
        3 // Default 3 hour duration
    }
}

private fun getJobsForDay(jobs: List<ActiveJob>, date: LocalDate): List<ActiveJob> {
    val today = LocalDate.now()

    return jobs.filter { job ->
        when {
            job.date.contains("Aujourd'hui", ignoreCase = true) -> date == today
            job.date.contains("Lundi", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.MONDAY
            job.date.contains("Mardi", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.TUESDAY
            job.date.contains("Mercredi", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.WEDNESDAY
            job.date.contains("Jeudi", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.THURSDAY
            job.date.contains("Vendredi", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.FRIDAY
            job.date.contains("Samedi", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.SATURDAY
            job.date.contains("Dimanche", ignoreCase = true) -> date.dayOfWeek == DayOfWeek.SUNDAY
            else -> false
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    TrustRepairTheme {
        CalendarScreen(
            onJobClick = {},
            onDashboardTab = {},
            onEarningsTab = {},
            onProfileTab = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekNavigationHeaderPreview() {
    TrustRepairTheme {
        WeekNavigationHeader(
            weekStart = LocalDate.of(2026, 1, 13),
            onPreviousWeek = {},
            onNextWeek = {}
        )
    }
}
