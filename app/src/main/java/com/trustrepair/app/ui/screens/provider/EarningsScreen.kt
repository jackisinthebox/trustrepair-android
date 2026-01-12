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
import com.trustrepair.app.data.Transaction
import com.trustrepair.app.data.demoProviderStats
import com.trustrepair.app.data.demoTransactions
import com.trustrepair.app.ui.components.EmptyState
import com.trustrepair.app.ui.theme.*

// Period filter options
private enum class EarningsPeriod(val labelResId: Int) {
    WEEK(R.string.earnings_period_week),
    MONTH(R.string.earnings_period_month),
    YEAR(R.string.earnings_period_year)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    onBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf(EarningsPeriod.MONTH) }
    var showPendingInfo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.earnings_title),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance card
            item {
                BalanceCard(
                    amount = demoProviderStats.earnedThisMonth,
                    onWithdraw = { /* Demo: no action */ }
                )
            }

            // Pending card
            item {
                PendingCard(
                    amount = demoProviderStats.pendingAmount,
                    showInfo = showPendingInfo,
                    onInfoClick = { showPendingInfo = !showPendingInfo }
                )
            }

            // Period selector
            item {
                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }

            // Stats row
            item {
                StatsRow(
                    jobsCompleted = demoProviderStats.jobsCompleted,
                    earnings = demoProviderStats.earnedThisMonth,
                    avgRating = demoProviderStats.averageRating
                )
            }

            // Transaction history header
            item {
                Text(
                    text = stringResource(R.string.earnings_history),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Transaction list or empty state
            if (demoTransactions.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Wallet,
                        title = "Pas encore de revenus",
                        subtitle = "Terminez votre premier travail",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            } else {
                items(demoTransactions, key = { it.id }) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BalanceCard(
    amount: Int,
    onWithdraw: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(ProviderPurple, ProviderPurpleDark)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Label
                Text(
                    text = stringResource(R.string.earnings_available),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                // Amount
                Text(
                    text = "$amount €",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Withdraw button
                Button(
                    onClick = onWithdraw,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = ProviderPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalance,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.earnings_withdraw),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingCard(
    amount: Int,
    showInfo: Boolean,
    onInfoClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = WarningAmberLight
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = WarningAmberDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.earnings_pending),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = WarningAmberDark
                    )
                }

                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = WarningAmberDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "$amount €",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WarningAmberDark
            )

            // Info tooltip
            if (showInfo) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Gray600,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.earnings_pending_info),
                            fontSize = 13.sp,
                            color = Gray700
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: EarningsPeriod,
    onPeriodSelected: (EarningsPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EarningsPeriod.entries.forEach { period ->
            PeriodChip(
                period = period,
                isSelected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) }
            )
        }
    }
}

@Composable
private fun PeriodChip(
    period: EarningsPeriod,
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
            text = stringResource(period.labelResId),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else Gray700
        )
    }
}

@Composable
private fun StatsRow(
    jobsCompleted: Int,
    earnings: Int,
    avgRating: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = stringResource(R.string.earnings_jobs_completed),
            value = jobsCompleted.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = stringResource(R.string.earnings_total),
            value = "$earnings €",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = stringResource(R.string.earnings_avg_rating),
            value = "★ $avgRating",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Gray500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Client avatar + details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.isPaid) SuccessGreenLight else WarningAmberLight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.clientName.take(1),
                        color = if (transaction.isPaid) SuccessGreenDark else WarningAmberDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Details
                Column {
                    Text(
                        text = transaction.clientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray900
                    )
                    Text(
                        text = "${transaction.jobType} • ${transaction.date}",
                        fontSize = 13.sp,
                        color = Gray500
                    )
                }
            }

            // Right: Amount + status
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${transaction.amount} €",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )

                // Status badge
                TransactionStatusBadge(isPaid = transaction.isPaid)
            }
        }
    }
}

@Composable
private fun TransactionStatusBadge(isPaid: Boolean) {
    val (backgroundColor, textColor, textResId) = if (isPaid) {
        Triple(SuccessGreenLight, SuccessGreenDark, R.string.earnings_status_paid)
    } else {
        Triple(WarningAmberLight, WarningAmberDark, R.string.earnings_status_pending)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Text(
            text = stringResource(textResId),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EarningsScreenPreview() {
    TrustRepairTheme {
        EarningsScreen(onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BalanceCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BalanceCard(
                amount = 2450,
                onWithdraw = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PendingCard(
                amount = 180,
                showInfo = true,
                onInfoClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionItemPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TransactionItem(
                transaction = demoTransactions[0]
            )
        }
    }
}
