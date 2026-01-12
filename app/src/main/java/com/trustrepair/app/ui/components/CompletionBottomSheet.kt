package com.trustrepair.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.trustrepair.app.R
import com.trustrepair.app.data.ActiveJob
import com.trustrepair.app.data.demoActiveJobs
import com.trustrepair.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class CompletionStep {
    PHOTOS,
    AMOUNT,
    SIGNATURE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletionBottomSheet(
    job: ActiveJob,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var currentStep by remember { mutableStateOf(CompletionStep.PHOTOS) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Amount adjustment state
    var isAmountAdjusted by remember { mutableStateOf(false) }
    var adjustedAmount by remember { mutableStateOf("") }
    var adjustmentReason by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val cameraMessage = stringResource(R.string.completion_camera_coming)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Step indicator
            StepIndicator(currentStep = currentStep)

            when (currentStep) {
                CompletionStep.PHOTOS -> {
                    PhotosStep(
                        onPhotoClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(cameraMessage)
                            }
                        },
                        onContinue = { currentStep = CompletionStep.AMOUNT }
                    )
                }
                CompletionStep.AMOUNT -> {
                    AmountStep(
                        originalTotal = job.priceBreakdown?.total ?: 0,
                        isAdjusted = isAmountAdjusted,
                        onAdjustedChange = { isAmountAdjusted = it },
                        adjustedAmount = adjustedAmount,
                        onAmountChange = { adjustedAmount = it },
                        adjustmentReason = adjustmentReason,
                        onReasonChange = { adjustmentReason = it },
                        onContinue = { currentStep = CompletionStep.SIGNATURE }
                    )
                }
                CompletionStep.SIGNATURE -> {
                    SignatureStep(
                        onFinish = {
                            showSuccessDialog = true
                        }
                    )
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onComplete()
            }
        )
    }
}

@Composable
private fun StepIndicator(currentStep: CompletionStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompletionStep.entries.forEachIndexed { index, step ->
            val isActive = step == currentStep
            val isCompleted = step.ordinal < currentStep.ordinal

            // Step circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> SuccessGreen
                            isActive -> ProviderPurple
                            else -> Gray200
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = (index + 1).toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) Color.White else Gray500
                    )
                }
            }

            // Connector line (except after last)
            if (index < CompletionStep.entries.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(
                            if (step.ordinal < currentStep.ordinal) SuccessGreen else Gray200
                        )
                )
            }
        }
    }
}

@Composable
private fun PhotosStep(
    onPhotoClick: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.completion_photos_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
        )

        // Photo grid (2x2)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PhotoPlaceholder(
                    modifier = Modifier.weight(1f),
                    onClick = onPhotoClick
                )
                PhotoPlaceholder(
                    modifier = Modifier.weight(1f),
                    onClick = onPhotoClick
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PhotoPlaceholder(
                    modifier = Modifier.weight(1f),
                    onClick = onPhotoClick
                )
                PhotoPlaceholder(
                    modifier = Modifier.weight(1f),
                    onClick = onPhotoClick
                )
            }
        }

        Text(
            text = "Ajoutez des photos du travail terminé pour documenter l'intervention",
            fontSize = 14.sp,
            color = Gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ProviderPurple),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = "Continuer",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun PhotoPlaceholder(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Gray100,
        border = androidx.compose.foundation.BorderStroke(2.dp, Gray300)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = Gray400,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Ajouter",
                    fontSize = 12.sp,
                    color = Gray500
                )
            }
        }
    }
}

@Composable
private fun AmountStep(
    originalTotal: Int,
    isAdjusted: Boolean,
    onAdjustedChange: (Boolean) -> Unit,
    adjustedAmount: String,
    onAmountChange: (String) -> Unit,
    adjustmentReason: String,
    onReasonChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.completion_amount_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
        )

        // Original quote display
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Gray50)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Montant du devis",
                    fontSize = 15.sp,
                    color = Gray600
                )
                Text(
                    text = "$originalTotal €",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
            }
        }

        // Radio options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Conform option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAdjustedChange(false) },
                shape = RoundedCornerShape(12.dp),
                color = if (!isAdjusted) SuccessGreenLight else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (!isAdjusted) 2.dp else 1.dp,
                    color = if (!isAdjusted) SuccessGreen else Gray300
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = !isAdjusted,
                        onClick = { onAdjustedChange(false) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = SuccessGreen
                        )
                    )
                    Text(
                        text = stringResource(R.string.completion_confirm),
                        fontSize = 15.sp,
                        fontWeight = if (!isAdjusted) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (!isAdjusted) SuccessGreenDark else Gray700
                    )
                }
            }

            // Adjusted option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAdjustedChange(true) },
                shape = RoundedCornerShape(12.dp),
                color = if (isAdjusted) WarningAmberLight else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isAdjusted) 2.dp else 1.dp,
                    color = if (isAdjusted) WarningAmber else Gray300
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = isAdjusted,
                        onClick = { onAdjustedChange(true) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = WarningAmber
                        )
                    )
                    Text(
                        text = stringResource(R.string.completion_adjusted),
                        fontSize = 15.sp,
                        fontWeight = if (isAdjusted) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isAdjusted) WarningAmberDark else Gray700
                    )
                }
            }
        }

        // Adjustment fields (shown only when adjusted)
        if (isAdjusted) {
            OutlinedTextField(
                value = adjustedAmount,
                onValueChange = { if (it.all { c -> c.isDigit() }) onAmountChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nouveau montant (€)") },
                placeholder = { Text("Ex: 180") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ProviderPurple,
                    unfocusedBorderColor = Gray300
                )
            )

            OutlinedTextField(
                value = adjustmentReason,
                onValueChange = onReasonChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                label = { Text("Raison de l'ajustement") },
                placeholder = { Text("Ex: Pièce supplémentaire nécessaire...") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ProviderPurple,
                    unfocusedBorderColor = Gray300
                )
            )
        }

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ProviderPurple),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = "Continuer",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SignatureStep(
    onFinish: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.completion_signature_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
        )

        // Signature box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            color = Gray100,
            border = androidx.compose.foundation.BorderStroke(2.dp, Gray300)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Draw,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.completion_signature_hint),
                        fontSize = 16.sp,
                        color = Gray500,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Text(
            text = "En signant, le client confirme la fin des travaux",
            fontSize = 13.sp,
            color = Gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ProviderPurple),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.completion_finish),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SuccessDialog(
    onDismiss: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
        delay(2500)
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedVisibility(
                    visible = showContent,
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
                            .background(SuccessGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(delayMillis = 200))
                ) {
                    Text(
                        text = stringResource(R.string.completion_success),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotosStepPreview() {
    TrustRepairTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            PhotosStep(
                onPhotoClick = {},
                onContinue = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AmountStepPreview() {
    TrustRepairTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            AmountStep(
                originalTotal = 150,
                isAdjusted = false,
                onAdjustedChange = {},
                adjustedAmount = "",
                onAmountChange = {},
                adjustmentReason = "",
                onReasonChange = {},
                onContinue = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignatureStepPreview() {
    TrustRepairTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            SignatureStep(
                onFinish = {}
            )
        }
    }
}
