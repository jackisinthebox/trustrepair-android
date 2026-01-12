package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.NotificationType
import com.trustrepair.app.data.ProviderNotification
import com.trustrepair.app.data.demoNotifications
import com.trustrepair.app.ui.components.EmptyState
import com.trustrepair.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit
) {
    // Mutable notifications state for toggling read status
    var notifications by remember { mutableStateOf(demoNotifications) }

    val unreadNotifications = notifications.filter { !it.isRead }
    val readNotifications = notifications.filter { it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notifications_title),
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
                actions = {
                    if (unreadNotifications.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                notifications = notifications.map { it.copy(isRead = true) }
                            }
                        ) {
                            Text(
                                text = "Tout lire",
                                color = ProviderPurple,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Gray50
    ) { padding ->
        if (notifications.isEmpty()) {
            // Empty state
            EmptyState(
                icon = Icons.Filled.Notifications,
                title = stringResource(R.string.notifications_empty),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Unread section
                if (unreadNotifications.isNotEmpty()) {
                    item {
                        SectionHeader(title = stringResource(R.string.notifications_new))
                    }

                    items(
                        items = unreadNotifications,
                        key = { it.id }
                    ) { notification ->
                        ClickableNotificationItem(
                            notification = notification,
                            onMarkRead = {
                                notifications = notifications.map {
                                    if (it.id == notification.id) it.copy(isRead = true) else it
                                }
                            }
                        )
                    }
                }

                // Read section
                if (readNotifications.isNotEmpty()) {
                    item {
                        SectionHeader(title = stringResource(R.string.notifications_older))
                    }

                    items(
                        items = readNotifications,
                        key = { it.id }
                    ) { notification ->
                        ClickableNotificationItem(
                            notification = notification,
                            onMarkRead = { /* Already read */ }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Gray500
    )
}

@Composable
private fun ClickableNotificationItem(
    notification: ProviderNotification,
    onMarkRead: () -> Unit
) {
    NotificationItem(
        notification = notification,
        onClick = onMarkRead
    )
}

@Composable
private fun NotificationItem(
    notification: ProviderNotification,
    onClick: () -> Unit = {}
) {
    val (iconColor, bgColor, icon) = getNotificationStyle(notification.type)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (notification.isRead) Color.White else ProviderPurple.copy(alpha = 0.03f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        color = Gray900,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notification.timeAgo,
                            fontSize = 12.sp,
                            color = Gray400
                        )

                        // Unread indicator dot
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ProviderPurple)
                            )
                        }
                    }
                }

                Text(
                    text = notification.subtitle,
                    fontSize = 14.sp,
                    color = Gray600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    // Divider
    Divider(
        modifier = Modifier.padding(start = 72.dp),
        thickness = 0.5.dp,
        color = Gray200
    )
}

private fun getNotificationStyle(type: NotificationType): Triple<Color, Color, ImageVector> {
    return when (type) {
        NotificationType.NEW_REQUEST -> Triple(
            ProviderPurple,
            ProviderPurpleLight,
            Icons.Filled.Assignment
        )
        NotificationType.QUOTE_ACCEPTED -> Triple(
            SuccessGreen,
            SuccessGreenLight,
            Icons.Filled.ThumbUp
        )
        NotificationType.PAYMENT_RECEIVED -> Triple(
            SuccessGreen,
            SuccessGreenLight,
            Icons.Filled.Payments
        )
        NotificationType.REMINDER -> Triple(
            WarningAmber,
            WarningAmberLight,
            Icons.Filled.NotificationsActive
        )
        NotificationType.SYSTEM -> Triple(
            Gray500,
            Gray100,
            Icons.Filled.Info
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    TrustRepairTheme {
        NotificationScreen(
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationItemPreview() {
    TrustRepairTheme {
        Column {
            NotificationItem(
                notification = ProviderNotification(
                    "1",
                    NotificationType.NEW_REQUEST,
                    "Nouvelle demande",
                    "Marie D. - Plomberie à Versailles",
                    "Il y a 15 min",
                    false
                )
            )
            NotificationItem(
                notification = ProviderNotification(
                    "2",
                    NotificationType.QUOTE_ACCEPTED,
                    "Devis accepté !",
                    "Thomas R. a accepté votre devis de 120 €",
                    "Il y a 2h",
                    true
                )
            )
        }
    }
}

