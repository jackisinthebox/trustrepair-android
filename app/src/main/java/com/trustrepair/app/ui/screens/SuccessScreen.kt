package com.trustrepair.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.demoJob
import com.trustrepair.app.data.demoQuotes
import com.trustrepair.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(
    onContinue: () -> Unit
) {
    val quote = demoQuotes[0]
    val job = demoJob

    // Animation state for checkmark bounce
    var showCheckmark by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showCheckmark = true
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrustBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.success_cta),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        containerColor = Gray50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header with animated checkmark
            SuccessHeader(showCheckmark = showCheckmark)

            // Confirmation card
            ConfirmationCard(providerName = quote.provider.name)

            // Booking summary card
            BookingSummaryCard(
                providerName = quote.provider.name,
                providerInitials = quote.provider.initials,
                providerColor = quote.provider.avatarColor,
                specialty = job.type,
                date = "${quote.date}, ${quote.timeSlot}",
                location = "${job.location}, ${job.postcode}"
            )

            // Next steps section
            NextStepsSection(
                providerName = quote.provider.name,
                timeSlot = quote.timeSlot
            )

            // Escrow reminder
            EscrowReminderCard()

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SuccessHeader(showCheckmark: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Animated checkmark circle
        AnimatedVisibility(
            visible = showCheckmark,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Title
        Text(
            text = stringResource(R.string.success_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            textAlign = TextAlign.Center
        )

        // Subtitle
        Text(
            text = stringResource(R.string.success_subtitle),
            fontSize = 15.sp,
            color = Gray600,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ConfirmationCard(providerName: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SuccessGreenLight, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = SuccessGreen50
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.success_card_title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = SuccessGreenDark
                )
            }

            // Description
            Text(
                text = "$providerName a été prévenu et va confirmer rapidement. Vous recevrez un rappel la veille du rendez-vous.",
                fontSize = 14.sp,
                color = SuccessGreenDark,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun BookingSummaryCard(
    providerName: String,
    providerInitials: String,
    providerColor: Color,
    specialty: String,
    date: String,
    location: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Provider row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        fontSize = 14.sp
                    )
                }

                Column {
                    Text(
                        text = providerName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Gray900
                    )
                    Text(
                        text = specialty,
                        fontSize = 13.sp,
                        color = Gray500
                    )
                }
            }

            // 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Calendar + Date
                SummaryGridItem(
                    icon = Icons.Filled.CalendarToday,
                    iconTint = TrustBlue,
                    text = date,
                    modifier = Modifier.weight(1f)
                )

                // Location + Address
                SummaryGridItem(
                    icon = Icons.Filled.LocationOn,
                    iconTint = SuccessGreen,
                    text = location,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryGridItem(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = Gray600,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun NextStepsSection(
    providerName: String,
    timeSlot: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.success_next_steps),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Gray900
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NextStepItem(
                    number = 1,
                    numberColor = TrustBlue,
                    title = stringResource(R.string.success_step_1_title),
                    description = "$providerName confirme sa venue sous 2h"
                )

                NextStepItem(
                    number = 2,
                    numberColor = TrustBlue,
                    title = stringResource(R.string.success_step_2_title),
                    description = stringResource(R.string.success_step_2_desc)
                )

                NextStepItem(
                    number = 3,
                    numberColor = TrustBlue,
                    title = stringResource(R.string.success_step_3_title),
                    description = "Arrivée entre $timeSlot"
                )

                NextStepItem(
                    number = 4,
                    numberColor = SuccessGreen,
                    title = stringResource(R.string.success_step_4_title),
                    description = stringResource(R.string.success_step_4_desc)
                )
            }
        }
    }
}

@Composable
private fun NextStepItem(
    number: Int,
    numberColor: Color,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Numbered circle
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(numberColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = numberColor
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
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

@Composable
private fun EscrowReminderCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TrustBlueLight, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = TrustBlue50
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TrustBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = TrustBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.success_escrow_title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TrustBlueDark
                )
                Text(
                    text = stringResource(R.string.success_escrow_desc),
                    fontSize = 13.sp,
                    color = TrustBlueDark.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessScreenPreview() {
    TrustRepairTheme {
        SuccessScreen(onContinue = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ConfirmationCard(providerName = "Karim D.")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NextStepItemPreview() {
    TrustRepairTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NextStepItem(
                number = 1,
                numberColor = TrustBlue,
                title = "Confirmation artisan",
                description = "Karim D. confirme sa venue sous 2h"
            )
            NextStepItem(
                number = 4,
                numberColor = SuccessGreen,
                title = "Validation finale",
                description = "Vous confirmez votre satisfaction"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EscrowReminderCardPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EscrowReminderCard()
        }
    }
}
