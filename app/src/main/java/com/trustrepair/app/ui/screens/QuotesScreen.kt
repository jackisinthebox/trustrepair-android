package com.trustrepair.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.Quote
import com.trustrepair.app.data.QuoteBadge
import com.trustrepair.app.data.demoQuotes
import com.trustrepair.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    onBack: () -> Unit,
    onQuoteSelected: () -> Unit
) {
    var selectedQuoteId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.quotes_title),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Gray900
                        )
                        Text(
                            text = "${demoQuotes.size} artisans disponibles",
                            fontSize = 13.sp,
                            color = Gray500
                        )
                    }
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
            // Info banner
            item {
                InfoBanner()
            }

            // Quote cards
            items(demoQuotes, key = { it.id }) { quote ->
                QuoteCard(
                    quote = quote,
                    isSelected = selectedQuoteId == quote.id,
                    onDetailsClick = { /* Show details modal */ },
                    onChooseClick = {
                        selectedQuoteId = quote.id
                        onQuoteSelected()
                    }
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = TrustBlue50
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.quotes_price_gap),
                fontSize = 13.sp,
                color = TrustBlueDark,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isSelected: Boolean,
    onDetailsClick: () -> Unit,
    onChooseClick: () -> Unit
) {
    val borderColor = if (isSelected) TrustBlue else Gray200
    val shadowElevation = if (isSelected) 8.dp else 2.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = shadowElevation,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = TrustBlue.copy(alpha = 0.3f),
                        spotColor = TrustBlue.copy(alpha = 0.3f)
                    )
                } else {
                    Modifier
                }
            )
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Badge (if any)
            quote.badge?.let { badge ->
                QuoteBadgeChip(badge = badge)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Provider row: Avatar + Info + Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar with verified checkmark
                Box {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        quote.provider.avatarColor,
                                        quote.provider.avatarColor.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quote.provider.initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Verified checkmark
                    if (quote.provider.verified) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Vérifié",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Provider info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = quote.provider.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = Gray900
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${quote.provider.rating}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Gray700
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${quote.provider.reviewCount} avis)",
                            fontSize = 14.sp,
                            color = Gray500
                        )
                    }
                }

                // Price column
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${quote.price} €",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Gray900
                    )
                    Text(
                        text = if (quote.isFixed) {
                            stringResource(R.string.quote_fixed_price)
                        } else {
                            stringResource(R.string.quote_estimated)
                        },
                        fontSize = 12.sp,
                        color = Gray500
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(
                    icon = Icons.Filled.CalendarToday,
                    text = "${quote.date}, ${quote.timeSlot}"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailItem(
                    icon = Icons.Filled.WorkOutline,
                    text = "${quote.provider.yearsExperience} ans d'exp."
                )
                DetailItem(
                    icon = Icons.Filled.LocationOn,
                    text = "${quote.provider.distanceKm} km"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Details button (outline)
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(Gray300, Gray300))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Gray700
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.quote_details),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }

                // Choose button (primary)
                Button(
                    onClick = onChooseClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrustBlue
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.quote_choose),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteBadgeChip(badge: QuoteBadge) {
    val (backgroundColor, textColor, text) = when (badge) {
        QuoteBadge.BEST_VALUE -> Triple(
            SuccessGreenLight,
            SuccessGreen,
            stringResource(R.string.quote_badge_best)
        )
        QuoteBadge.AVAILABLE_TODAY -> Triple(
            WarningAmberLight,
            WarningAmber,
            stringResource(R.string.quote_badge_fast)
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Gray600
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuotesScreenPreview() {
    TrustRepairTheme {
        QuotesScreen(
            onBack = {},
            onQuoteSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuoteCardPreview() {
    TrustRepairTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuoteCard(
                quote = demoQuotes[0],
                isSelected = true,
                onDetailsClick = {},
                onChooseClick = {}
            )
            QuoteCard(
                quote = demoQuotes[1],
                isSelected = false,
                onDetailsClick = {},
                onChooseClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuoteBadgeChipPreview() {
    TrustRepairTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuoteBadgeChip(badge = QuoteBadge.BEST_VALUE)
            QuoteBadgeChip(badge = QuoteBadge.AVAILABLE_TODAY)
        }
    }
}
