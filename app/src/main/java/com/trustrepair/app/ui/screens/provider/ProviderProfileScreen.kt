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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.trustrepair.app.data.currentProvider
import com.trustrepair.app.ui.theme.*

// Menu item data
private data class ProfileMenuItem(
    val icon: ImageVector,
    val labelResId: Int
)

private val menuItems = listOf(
    ProfileMenuItem(Icons.Filled.Person, R.string.profile_personal_info),
    ProfileMenuItem(Icons.Filled.Description, R.string.profile_documents),
    ProfileMenuItem(Icons.Filled.Map, R.string.profile_zone),
    ProfileMenuItem(Icons.Filled.Schedule, R.string.profile_availability),
    ProfileMenuItem(Icons.Filled.Notifications, R.string.profile_notifications),
    ProfileMenuItem(Icons.Filled.HelpOutline, R.string.profile_help)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
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
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Profile card
            item {
                ProfileCard()
            }

            // Menu items section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Column {
                        menuItems.forEachIndexed { index, item ->
                            ProfileMenuRow(
                                icon = item.icon,
                                label = stringResource(item.labelResId),
                                onClick = { /* Prototype: no action */ }
                            )
                            if (index < menuItems.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(start = 56.dp),
                                    color = Gray200,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }

            // Bottom section: Logout + Version
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BottomSection(onLogout = onLogout)
            }
        }
    }
}

@Composable
private fun ProfileCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar with edit overlay
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                // Large avatar
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(ProviderPurple, ProviderPurpleDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${currentProvider.firstName.first()}${currentProvider.lastName.first()}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }

                // Edit icon overlay
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = 4.dp, y = 4.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit avatar",
                            tint = ProviderPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Name
            Text(
                text = "${currentProvider.firstName} ${currentProvider.lastName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            // Specialty and experience
            Text(
                text = "${currentProvider.specialty} · ${currentProvider.yearsExperience} ans d'exp.",
                fontSize = 15.sp,
                color = Gray600
            )

            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = StarYellow,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${currentProvider.rating}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Text(
                    text = "(${currentProvider.reviewCount} avis)",
                    fontSize = 15.sp,
                    color = Gray500
                )
            }

            // View public profile button
            TextButton(
                onClick = { /* Prototype: no action */ }
            ) {
                Text(
                    text = stringResource(R.string.profile_view_public),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ProviderPurple
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gray600,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Label
        Text(
            text = label,
            fontSize = 16.sp,
            color = Gray900,
            modifier = Modifier.weight(1f)
        )

        // Chevron
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun BottomSection(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logout button
        TextButton(
            onClick = onLogout
        ) {
            Icon(
                imageVector = Icons.Filled.Logout,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.profile_logout),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ErrorRed
            )
        }

        // App version
        Text(
            text = stringResource(R.string.profile_version, "1.0.0"),
            fontSize = 13.sp,
            color = Gray400,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProviderProfileScreenPreview() {
    TrustRepairTheme {
        ProviderProfileScreen(
            onBack = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileCardPreview() {
    TrustRepairTheme {
        ProfileCard()
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileMenuRowPreview() {
    TrustRepairTheme {
        Surface(color = Color.White) {
            ProfileMenuRow(
                icon = Icons.Filled.Person,
                label = "Informations personnelles",
                onClick = {}
            )
        }
    }
}
