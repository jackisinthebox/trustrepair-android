package com.trustrepair.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import com.trustrepair.app.ui.theme.Gray600
import com.trustrepair.app.ui.theme.TrustRepairTheme

/**
 * Maps job types to their corresponding Material icons.
 */
fun getJobTypeIcon(jobType: String): ImageVector {
    return when (jobType.lowercase()) {
        "plomberie" -> Icons.Filled.Plumbing
        "électricité", "electricite" -> Icons.Filled.ElectricBolt
        "serrurerie" -> Icons.Filled.Key
        "chauffage" -> Icons.Filled.LocalFireDepartment
        "climatisation" -> Icons.Filled.AcUnit
        else -> Icons.Filled.Build
    }
}

/**
 * Displays an icon corresponding to the job type.
 *
 * @param jobType The type of job (e.g., "Plomberie", "Électricité")
 * @param modifier Modifier for the icon
 * @param tint Color tint for the icon
 */
@Composable
fun JobTypeIcon(
    jobType: String,
    modifier: Modifier = Modifier,
    tint: Color = Gray600
) {
    Icon(
        imageVector = getJobTypeIcon(jobType),
        contentDescription = jobType,
        modifier = modifier,
        tint = tint
    )
}

@Preview(showBackground = true)
@Composable
private fun JobTypeIconPreview() {
    TrustRepairTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            JobTypeIcon(jobType = "Plomberie", modifier = Modifier.size(24.dp))
            JobTypeIcon(jobType = "Électricité", modifier = Modifier.size(24.dp))
            JobTypeIcon(jobType = "Serrurerie", modifier = Modifier.size(24.dp))
            JobTypeIcon(jobType = "Chauffage", modifier = Modifier.size(24.dp))
            JobTypeIcon(jobType = "Climatisation", modifier = Modifier.size(24.dp))
            JobTypeIcon(jobType = "Other", modifier = Modifier.size(24.dp))
        }
    }
}
