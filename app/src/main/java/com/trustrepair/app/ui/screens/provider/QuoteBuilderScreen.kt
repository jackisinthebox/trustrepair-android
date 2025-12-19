package com.trustrepair.app.ui.screens.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.demoPriceBreakdown
import com.trustrepair.app.data.demoJobRequests
import com.trustrepair.app.ui.theme.*

// Line item data class
private data class LineItem(
    val id: Int,
    val description: String,
    val amount: String
)

// Quick add item presets
private data class QuickAddItem(
    val label: String,
    val defaultAmount: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuoteBuilderScreen(
    jobId: String,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val jobRequest = demoJobRequests.find { it.id == jobId } ?: demoJobRequests[0]

    // String resources (must be outside remember)
    val laborLabel = stringResource(R.string.quote_builder_labor)
    val travelLabel = stringResource(R.string.quote_builder_travel)
    val partsLabel = stringResource(R.string.quote_builder_parts)
    val diagnosticLabel = stringResource(R.string.quote_builder_diagnostic)
    val urgentLabel = stringResource(R.string.quote_builder_urgent)

    // Line items state
    var nextId by remember { mutableIntStateOf(2) }
    val lineItems = remember {
        mutableStateListOf(
            LineItem(1, laborLabel, demoPriceBreakdown.labor.toString())
        )
    }

    // Quick add presets
    val quickAddItems = listOf(
        QuickAddItem(travelLabel, 30),
        QuickAddItem(partsLabel, 50),
        QuickAddItem(diagnosticLabel, 40),
        QuickAddItem(urgentLabel, 25)
    )

    // Time slot labels (must be outside remember)
    val morningLabel = stringResource(R.string.morning)
    val afternoonLabel = stringResource(R.string.afternoon)
    val eveningLabel = stringResource(R.string.evening)

    // Date selection
    val availableDates = listOf(
        "Lundi 20 janvier",
        "Mardi 21 janvier",
        "Mercredi 22 janvier",
        "Jeudi 23 janvier",
        "Vendredi 24 janvier"
    )
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Time slot selection
    val timeSlots = listOf(morningLabel, afternoonLabel, eveningLabel)
    var selectedTimeSlot by remember { mutableIntStateOf(1) } // Default: Après-midi

    // Price type
    var isFixedPrice by remember { mutableStateOf(true) }

    // Message
    var message by remember { mutableStateOf("") }

    // Calculate total
    val total = lineItems.sumOf { it.amount.toIntOrNull() ?: 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.quote_builder_title),
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
        bottomBar = {
            BottomActionBar(
                onPreview = { /* Show preview modal - optional */ },
                onSubmit = onSubmit
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
            // Job summary card
            JobSummaryCard(
                clientName = jobRequest.client.name,
                jobType = jobRequest.jobType,
                location = jobRequest.location
            )

            // Line items section
            LineItemsSection(
                lineItems = lineItems,
                onUpdateItem = { id, description, amount ->
                    val index = lineItems.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        lineItems[index] = LineItem(id, description, amount)
                    }
                },
                onDeleteItem = { id ->
                    lineItems.removeAll { it.id == id }
                },
                onAddItem = {
                    lineItems.add(LineItem(nextId++, "", ""))
                }
            )

            // Quick add chips
            QuickAddSection(
                items = quickAddItems,
                onAdd = { item ->
                    lineItems.add(LineItem(nextId++, item.label, item.defaultAmount.toString()))
                }
            )

            // Date picker
            DatePickerSection(
                selectedDate = availableDates[selectedDateIndex],
                onClick = { showDatePicker = true }
            )

            // Time slot selector
            TimeSlotSection(
                timeSlots = timeSlots,
                selectedIndex = selectedTimeSlot,
                onSelect = { selectedTimeSlot = it }
            )

            // Price type toggle
            PriceTypeSection(
                isFixedPrice = isFixedPrice,
                onToggle = { isFixedPrice = it }
            )

            // Total display
            TotalDisplay(
                total = total,
                isFixedPrice = isFixedPrice
            )

            // Optional message
            MessageSection(
                message = message,
                onMessageChange = { message = it }
            )

            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            dates = availableDates,
            selectedIndex = selectedDateIndex,
            onSelect = {
                selectedDateIndex = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun JobSummaryCard(
    clientName: String,
    jobType: String,
    location: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null,
                tint = ProviderPurple,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$clientName — $jobType — $location",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gray700
            )
        }
    }
}

@Composable
private fun LineItemsSection(
    lineItems: List<LineItem>,
    onUpdateItem: (Int, String, String) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onAddItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Text(
                text = "Lignes du devis",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )

            // Line items
            lineItems.forEach { item ->
                LineItemRow(
                    item = item,
                    onDescriptionChange = { onUpdateItem(item.id, it, item.amount) },
                    onAmountChange = { onUpdateItem(item.id, item.description, it) },
                    onDelete = { onDeleteItem(item.id) },
                    showDelete = lineItems.size > 1
                )
            }

            // Add line button
            TextButton(
                onClick = onAddItem,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = ProviderPurple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.quote_builder_add_line),
                    color = ProviderPurple,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LineItemRow(
    item: LineItem,
    onDescriptionChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Description field
        OutlinedTextField(
            value = item.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Description", color = Gray400, fontSize = 14.sp) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProviderPurple,
                unfocusedBorderColor = Gray300,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Gray50
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )

        // Amount field
        OutlinedTextField(
            value = item.amount,
            onValueChange = { newValue ->
                // Only allow digits
                if (newValue.all { it.isDigit() }) {
                    onAmountChange(newValue)
                }
            },
            modifier = Modifier.width(80.dp),
            placeholder = { Text("€", color = Gray400, fontSize = 14.sp) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Text("€", color = Gray500, fontSize = 14.sp)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProviderPurple,
                unfocusedBorderColor = Gray300,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Gray50
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, textAlign = TextAlign.End)
        )

        // Delete button
        if (showDelete) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Supprimer",
                    tint = Gray400,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickAddSection(
    items: List<QuickAddItem>,
    onAdd: (QuickAddItem) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.quote_builder_quick_add),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                SuggestionChip(
                    onClick = { onAdd(item) },
                    label = {
                        Text(
                            text = "${item.label} (${item.defaultAmount}€)",
                            fontSize = 13.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = ProviderPurple.copy(alpha = 0.1f),
                        labelColor = ProviderPurple,
                        iconContentColor = ProviderPurple
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
private fun DatePickerSection(
    selectedDate: String,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.quote_builder_date),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Gray300)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = ProviderPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = selectedDate,
                        fontSize = 15.sp,
                        color = Gray900
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Gray400
                )
            }
        }
    }
}

@Composable
private fun TimeSlotSection(
    timeSlots: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.quote_builder_time),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timeSlots.forEachIndexed { index, slot ->
                val isSelected = index == selectedIndex
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(index) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) ProviderPurple else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Gray300)
                ) {
                    Text(
                        text = slot,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Gray700
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceTypeSection(
    isFixedPrice: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Type de prix",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fixed price
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggle(true) },
                shape = RoundedCornerShape(10.dp),
                color = if (isFixedPrice) ProviderPurple else Color.White,
                border = if (isFixedPrice) null else androidx.compose.foundation.BorderStroke(1.dp, Gray300)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isFixedPrice) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = stringResource(R.string.quote_builder_fixed),
                        fontSize = 14.sp,
                        fontWeight = if (isFixedPrice) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isFixedPrice) Color.White else Gray700
                    )
                }
            }

            // Estimate
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggle(false) },
                shape = RoundedCornerShape(10.dp),
                color = if (!isFixedPrice) ProviderPurple else Color.White,
                border = if (!isFixedPrice) null else androidx.compose.foundation.BorderStroke(1.dp, Gray300)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isFixedPrice) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = stringResource(R.string.quote_builder_estimate),
                        fontSize = 14.sp,
                        fontWeight = if (!isFixedPrice) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (!isFixedPrice) Color.White else Gray700
                    )
                }
            }
        }
    }
}

@Composable
private fun TotalDisplay(
    total: Int,
    isFixedPrice: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ProviderPurple.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.quote_builder_total),
                fontSize = 14.sp,
                color = Gray600
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$total €",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProviderPurple
                )
                if (!isFixedPrice) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WarningAmberLight
                    ) {
                        Text(
                            text = "~",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmberDark
                        )
                    }
                }
            }
            Text(
                text = if (isFixedPrice) "Prix fixe" else "Estimation",
                fontSize = 12.sp,
                color = Gray500
            )
        }
    }
}

@Composable
private fun MessageSection(
    message: String,
    onMessageChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.quote_builder_message),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700
        )

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = {
                Text(
                    text = "Message au client...",
                    color = Gray400
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProviderPurple,
                unfocusedBorderColor = Gray300,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun DatePickerDialog(
    dates: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choisir une date",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dates.forEachIndexed { index, date ->
                    val isSelected = index == selectedIndex
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(index) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ProviderPurple.copy(alpha = 0.1f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = date,
                                fontSize = 15.sp,
                                color = if (isSelected) ProviderPurple else Gray700,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = ProviderPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", color = ProviderPurple)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun BottomActionBar(
    onPreview: () -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Preview button (outline)
            OutlinedButton(
                onClick = onPreview,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Gray700
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.quote_builder_preview),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }

            // Submit button (primary, purple)
            Button(
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProviderPurple
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.quote_builder_submit),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuoteBuilderScreenPreview() {
    TrustRepairTheme {
        QuoteBuilderScreen(
            jobId = "req1",
            onBack = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TotalDisplayPreview() {
    TrustRepairTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TotalDisplay(total = 130, isFixedPrice = true)
            TotalDisplay(total = 130, isFixedPrice = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LineItemRowPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LineItemRow(
                item = LineItem(1, "Main d'œuvre", "80"),
                onDescriptionChange = {},
                onAmountChange = {},
                onDelete = {},
                showDelete = true
            )
        }
    }
}
