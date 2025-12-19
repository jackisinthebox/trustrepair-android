package com.trustrepair.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.demoPhoneNumber
import com.trustrepair.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    // OTP state - 6 digits
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    // Resend countdown state
    var resendEnabled by remember { mutableStateOf(true) }
    var countdown by remember { mutableIntStateOf(0) }

    // Countdown timer effect
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
            if (countdown == 0) {
                resendEnabled = true
            }
        }
    }

    // Auto-verify when all digits entered
    LaunchedEffect(otpValues) {
        if (otpValues.all { it.isNotEmpty() }) {
            delay(300) // Small delay for visual feedback
            onVerified()
        }
    }

    // Request focus on first field when screen opens
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            // Bottom sticky verify button
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        if (otpValues.all { it.isNotEmpty() }) {
                            onVerified()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = otpValues.all { it.isNotEmpty() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TrustBlue,
                        disabledContainerColor = Gray300
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.verify_button),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Phone icon in blue circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(TrustBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = TrustBlue,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = stringResource(R.string.verify_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with phone number
            Text(
                text = stringResource(R.string.verify_subtitle),
                fontSize = 15.sp,
                color = Gray500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = demoPhoneNumber,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray700
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Input Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpValues.forEachIndexed { index, value ->
                    OtpInputField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                val newOtpValues = otpValues.toMutableList()
                                newOtpValues[index] = newValue
                                otpValues = newOtpValues

                                // Auto-advance to next field
                                if (newValue.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        onBackspace = {
                            if (value.isEmpty() && index > 0) {
                                // Move to previous field on backspace when empty
                                focusRequesters[index - 1].requestFocus()
                                val newOtpValues = otpValues.toMutableList()
                                newOtpValues[index - 1] = ""
                                otpValues = newOtpValues
                            } else if (value.isNotEmpty()) {
                                // Clear current field
                                val newOtpValues = otpValues.toMutableList()
                                newOtpValues[index] = ""
                                otpValues = newOtpValues
                            }
                        },
                        isFilled = value.isNotEmpty(),
                        focusRequester = focusRequesters[index]
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Resend section
            Text(
                text = stringResource(R.string.verify_resend_question),
                fontSize = 14.sp,
                color = Gray500
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    if (resendEnabled) {
                        resendEnabled = false
                        countdown = 30
                        // Clear OTP fields
                        otpValues = List(6) { "" }
                        focusRequesters[0].requestFocus()
                    }
                },
                enabled = resendEnabled
            ) {
                Text(
                    text = if (resendEnabled) {
                        stringResource(R.string.verify_resend)
                    } else {
                        "Renvoyer dans ${countdown}s"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (resendEnabled) TrustBlue else Gray400
                )
            }
        }
    }
}

@Composable
private fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    isFilled: Boolean,
    focusRequester: FocusRequester
) {
    val borderColor = when {
        isFilled -> SuccessGreen
        else -> Gray300
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .size(width = 48.dp, height = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(if (isFilled) SuccessGreenLight.copy(alpha = 0.3f) else Gray50)
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Backspace) {
                    onBackspace()
                    true
                } else {
                    false
                }
            },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Gray900
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun VerifyScreenPreview() {
    TrustRepairTheme {
        VerifyScreen(
            onBack = {},
            onVerified = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpInputFieldPreview() {
    TrustRepairTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OtpInputField(
                value = "5",
                onValueChange = {},
                onBackspace = {},
                isFilled = true,
                focusRequester = FocusRequester()
            )
            OtpInputField(
                value = "",
                onValueChange = {},
                onBackspace = {},
                isFilled = false,
                focusRequester = FocusRequester()
            )
        }
    }
}
