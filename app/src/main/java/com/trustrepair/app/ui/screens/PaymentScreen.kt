package com.trustrepair.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.trustrepair.app.data.demoJob
import com.trustrepair.app.data.demoPriceBreakdown
import com.trustrepair.app.data.demoQuotes
import com.trustrepair.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onPay: () -> Unit
) {
    val quote = demoQuotes[0]
    val job = demoJob
    val price = demoPriceBreakdown

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.payment_title),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Gray900
                        )
                        Text(
                            text = "Étape 1 sur 2",
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
        bottomBar = {
            PaymentBottomBar(
                totalPrice = price.total,
                onPay = onPay
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security badge
            SecurityBadge()

            // Booking card
            BookingCard(
                providerName = quote.provider.name,
                providerInitials = quote.provider.initials,
                providerColor = quote.provider.avatarColor,
                jobType = job.type,
                jobDescription = job.description,
                date = "${quote.date}, ${quote.timeSlot}",
                location = "${job.location}, ${job.postcode}"
            )

            // Price breakdown
            PriceBreakdown(
                laborPrice = price.labor,
                partsPrice = price.parts,
                totalPrice = price.total
            )

            // Guarantee cards
            GuaranteeCard(
                icon = Icons.Filled.Shield,
                iconBackgroundColor = TrustBlueLight,
                iconTint = TrustBlue,
                title = stringResource(R.string.payment_guarantee_escrow_title),
                description = stringResource(R.string.payment_guarantee_escrow_desc)
            )

            GuaranteeCard(
                icon = Icons.Filled.CurrencyExchange,
                iconBackgroundColor = SuccessGreenLight,
                iconTint = SuccessGreen,
                title = stringResource(R.string.payment_guarantee_refund_title),
                description = stringResource(R.string.payment_guarantee_refund_desc)
            )

            // Bottom spacing for scroll
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SecurityBadge() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SuccessGreenLight
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.payment_secure),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SuccessGreenDark
            )
        }
    }
}

@Composable
private fun BookingCard(
    providerName: String,
    providerInitials: String,
    providerColor: Color,
    jobType: String,
    jobDescription: String,
    date: String,
    location: String
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
            // Provider header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(providerColor, providerColor.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = providerInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Column {
                    Text(
                        text = providerName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Gray900
                    )
                    Text(
                        text = jobType,
                        fontSize = 14.sp,
                        color = Gray500
                    )
                }
            }

            Divider(color = Gray100, thickness = 1.dp)

            // Job details
            BookingDetailItem(
                icon = Icons.Filled.Build,
                iconBackgroundColor = TrustBlueLight,
                iconTint = TrustBlue,
                text = jobDescription
            )

            BookingDetailItem(
                icon = Icons.Filled.CalendarToday,
                iconBackgroundColor = WarningAmberLight,
                iconTint = WarningAmber,
                text = date
            )

            BookingDetailItem(
                icon = Icons.Filled.LocationOn,
                iconBackgroundColor = SuccessGreenLight,
                iconTint = SuccessGreen,
                text = location
            )
        }
    }
}

@Composable
private fun BookingDetailItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
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
            text = text,
            fontSize = 14.sp,
            color = Gray700,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PriceBreakdown(
    laborPrice: Int,
    partsPrice: Int,
    totalPrice: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Gray100
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            Divider(color = Gray300, thickness = 1.dp)

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.payment_total),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Text(
                    text = "$totalPrice €",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
            }
        }
    }
}

@Composable
private fun GuaranteeCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Gray900
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Gray500,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PaymentBottomBar(
    totalPrice: Int,
    onPay: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onPay,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrustBlue
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Payer $totalPrice € de manière sécurisée",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Text(
                text = stringResource(R.string.payment_escrow_note),
                fontSize = 12.sp,
                color = Gray500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    TrustRepairTheme {
        PaymentScreen(
            onBack = {},
            onPay = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecurityBadgePreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SecurityBadge()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceBreakdownPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PriceBreakdown(
                laborPrice = 80,
                partsPrice = 20,
                totalPrice = 100
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GuaranteeCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GuaranteeCard(
                icon = Icons.Filled.Shield,
                iconBackgroundColor = TrustBlueLight,
                iconTint = TrustBlue,
                title = "Paiement en séquestre",
                description = "L'argent est bloqué jusqu'à votre confirmation de satisfaction"
            )
        }
    }
}
