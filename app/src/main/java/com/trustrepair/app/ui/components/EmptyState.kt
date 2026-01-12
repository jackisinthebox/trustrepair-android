package com.trustrepair.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.ui.theme.*

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    ctaText: String? = null,
    onCtaClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icon in circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Gray100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Title
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray700,
                textAlign = TextAlign.Center
            )

            // Subtitle (optional)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Gray500,
                    textAlign = TextAlign.Center
                )
            }

            // CTA button (optional)
            if (ctaText != null && onCtaClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCtaClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProviderPurple),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = ctaText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateBasicPreview() {
    TrustRepairTheme {
        EmptyState(
            icon = Icons.Filled.Notifications,
            title = "Tout est à jour !",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateWithSubtitlePreview() {
    TrustRepairTheme {
        EmptyState(
            icon = Icons.Filled.Wallet,
            title = "Pas encore de revenus",
            subtitle = "Terminez votre premier travail",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStateWithCtaPreview() {
    TrustRepairTheme {
        EmptyState(
            icon = Icons.Filled.Work,
            title = "Aucun travail",
            subtitle = "Répondez aux demandes pour remplir votre planning",
            ctaText = "Voir les demandes",
            onCtaClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
