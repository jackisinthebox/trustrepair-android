package com.trustrepair.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustrepair.app.R
import com.trustrepair.app.data.demoQuotes
import com.trustrepair.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Chat message data model
data class ChatMessage(
    val id: Int,
    val text: String,
    val isFromUser: Boolean
)

// Chat step enum to track conversation progress
enum class ChatStep {
    GREETING,
    PROBLEM_TYPE,
    PROBLEM_LOCATION,
    PHOTO_OPTION,
    POSTAL_CODE,
    VERIFY_PHONE,
    VERIFIED,
    COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    onVerifyClick: () -> Unit,
    onQuotesClick: () -> Unit,
    isVerified: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // State management - use rememberSaveable for step to survive navigation
    var currentStep by rememberSaveable { mutableStateOf(if (isVerified) ChatStep.VERIFY_PHONE else ChatStep.GREETING) }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var textInput by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var showQuoteCard by remember { mutableStateOf(false) }
    var hasInitialized by rememberSaveable { mutableStateOf(false) }
    var verificationHandled by rememberSaveable { mutableStateOf(false) }

    // String resources
    val quickPlomberie = stringResource(R.string.quick_plomberie)
    val quickElectricite = stringResource(R.string.quick_electricite)
    val quickSerrurerie = stringResource(R.string.quick_serrurerie)
    val quickPhoto = stringResource(R.string.quick_photo)
    val quickNoPhoto = stringResource(R.string.quick_no_photo)
    val quickVerify = stringResource(R.string.quick_verify)
    val chatGreeting = stringResource(R.string.chat_greeting)

    // Quick replies based on current step
    val quickReplies = when (currentStep) {
        ChatStep.PROBLEM_TYPE -> listOf(quickPlomberie, quickElectricite, quickSerrurerie)
        ChatStep.PROBLEM_LOCATION -> listOf("Sous l'évier", "WC", "Chauffe-eau")
        ChatStep.PHOTO_OPTION -> listOf(quickPhoto, quickNoPhoto)
        ChatStep.VERIFY_PHONE -> listOf(quickVerify)
        else -> emptyList()
    }

    // Handle verification return - rebuild conversation and show result
    LaunchedEffect(isVerified) {
        if (isVerified && !verificationHandled) {
            // Set flags first before any suspend calls
            verificationHandled = true
            hasInitialized = true

            // Rebuild conversation history to show context
            val conversationHistory = listOf(
                ChatMessage(id = 0, text = chatGreeting, isFromUser = false),
                ChatMessage(id = 1, text = quickPlomberie, isFromUser = true),
                ChatMessage(id = 2, text = "Où se situe le problème ?", isFromUser = false),
                ChatMessage(id = 3, text = "Sous l'évier", isFromUser = true),
                ChatMessage(id = 4, text = "Avez-vous une photo du problème ? Cela aide nos artisans à mieux préparer leur intervention.", isFromUser = false),
                ChatMessage(id = 5, text = quickNoPhoto, isFromUser = true),
                ChatMessage(id = 6, text = "Quel est votre code postal ?", isFromUser = false),
                ChatMessage(id = 7, text = "78000", isFromUser = true),
                ChatMessage(id = 8, text = "Pour recevoir les devis des artisans, nous devons vérifier votre numéro de téléphone.", isFromUser = false),
                ChatMessage(id = 9, text = "Numéro vérifié !", isFromUser = true)
            )
            messages = conversationHistory

            isTyping = true
            delay(500)
            isTyping = false

            messages = conversationHistory + ChatMessage(
                id = 10,
                text = "Parfait ! J'ai trouvé 3 artisans disponibles dans votre secteur. Voici le meilleur rapport qualité-prix :",
                isFromUser = false
            )
            delay(300)
            showQuoteCard = true
            currentStep = ChatStep.COMPLETE
        }
    }

    // Initialize chat with greeting (only if not returning from verification)
    LaunchedEffect(Unit) {
        if (!isVerified && !hasInitialized) {
            hasInitialized = true
            isTyping = true
            delay(500)
            isTyping = false
            messages = listOf(
                ChatMessage(
                    id = 0,
                    text = chatGreeting,
                    isFromUser = false
                )
            )
            currentStep = ChatStep.PROBLEM_TYPE
        }
    }

    // Auto-scroll to bottom
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1 + if (isTyping) 1 else 0)
        }
    }

    // Handle quick reply selection
    fun onQuickReplyClick(reply: String) {
        scope.launch {
            // Add user message
            messages = messages + ChatMessage(
                id = messages.size,
                text = reply,
                isFromUser = true
            )

            // Check for verify button click
            if (reply == quickVerify) {
                onVerifyClick()
                return@launch
            }

            // Show typing indicator
            isTyping = true
            delay(500)
            isTyping = false

            // Progress to next step with AI response
            when (currentStep) {
                ChatStep.PROBLEM_TYPE -> {
                    messages = messages + ChatMessage(
                        id = messages.size,
                        text = "Où se situe le problème ?",
                        isFromUser = false
                    )
                    currentStep = ChatStep.PROBLEM_LOCATION
                }
                ChatStep.PROBLEM_LOCATION -> {
                    messages = messages + ChatMessage(
                        id = messages.size,
                        text = "Avez-vous une photo du problème ? Cela aide nos artisans à mieux préparer leur intervention.",
                        isFromUser = false
                    )
                    currentStep = ChatStep.PHOTO_OPTION
                }
                ChatStep.PHOTO_OPTION -> {
                    messages = messages + ChatMessage(
                        id = messages.size,
                        text = "Quel est votre code postal ?",
                        isFromUser = false
                    )
                    currentStep = ChatStep.POSTAL_CODE
                }
                else -> {}
            }
        }
    }

    // Handle text input submission (postal code)
    fun onSendMessage() {
        if (textInput.isBlank()) return

        scope.launch {
            val message = textInput
            textInput = ""

            messages = messages + ChatMessage(
                id = messages.size,
                text = message,
                isFromUser = true
            )

            if (currentStep == ChatStep.POSTAL_CODE) {
                isTyping = true
                delay(500)
                isTyping = false
                messages = messages + ChatMessage(
                    id = messages.size,
                    text = "Pour recevoir les devis des artisans, nous devons vérifier votre numéro de téléphone.",
                    isFromUser = false
                )
                currentStep = ChatStep.VERIFY_PHONE
            }
        }
    }

    Scaffold(
        topBar = {
            ChatHeader(onBackClick = onBackClick)
        },
        bottomBar = {
            ChatInputBar(
                textInput = textInput,
                onTextChange = { textInput = it },
                onSendClick = { onSendMessage() },
                enabled = currentStep == ChatStep.POSTAL_CODE
            )
        },
        containerColor = Gray50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }

                // Typing indicator
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }

                // Quote card
                if (showQuoteCard) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        QuotePreviewCard(onQuotesClick = onQuotesClick)
                    }
                }
            }

            // Quick replies
            if (quickReplies.isNotEmpty() && !isTyping && !showQuoteCard) {
                Surface(
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickReplies.forEach { reply ->
                            QuickReplyChip(
                                text = reply,
                                onClick = { onQuickReplyClick(reply) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatHeader(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar with blue gradient
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(TrustBlue, TrustBlueDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Gray900
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Text(
                            text = stringResource(R.string.chat_online),
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Gray700
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Menu action */ }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Menu",
                    tint = Gray700
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(TrustBlue, TrustBlueDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) TrustBlue else Color.White,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Gray200) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (isUser) Color.White else Gray900,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User Avatar (Marie)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Gray200),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Gray600,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                1f at 200
                0.3f at 400
                0.3f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot1"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                0.3f at 200
                1f at 400
                0.3f at 600
                0.3f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot2"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                0.3f at 400
                1f at 600
                0.3f at 800
                0.3f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AI Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(TrustBlue, TrustBlueDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TR",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            ),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Gray500.copy(alpha = dot1Alpha))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Gray500.copy(alpha = dot2Alpha))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Gray500.copy(alpha = dot3Alpha))
                )
            }
        }
    }
}

@Composable
private fun QuickReplyChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, TrustBlue)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = TrustBlue,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ChatInputBar(
    textInput: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Camera button
            IconButton(onClick = { /* Camera action */ }) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Photo",
                    tint = if (enabled) TrustBlue else Gray400
                )
            }

            // Text input with rounded background
            TextField(
                value = textInput,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (enabled) "Votre code postal..." else stringResource(R.string.chat_placeholder),
                        color = Gray400
                    )
                },
                enabled = enabled,
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Gray100,
                    unfocusedContainerColor = Gray100,
                    disabledContainerColor = Gray100,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClick() })
            )

            // Send button
            IconButton(
                onClick = onSendClick,
                enabled = enabled && textInput.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Envoyer",
                    tint = if (enabled && textInput.isNotBlank()) TrustBlue else Gray400
                )
            }
        }
    }
}

@Composable
private fun QuotePreviewCard(onQuotesClick: () -> Unit) {
    val quote = demoQuotes[0]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = SuccessGreenLight
            ) {
                Text(
                    text = stringResource(R.string.quote_badge_best),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = SuccessGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Provider avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(quote.provider.avatarColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quote.provider.initials,
                        color = quote.provider.avatarColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quote.provider.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Gray900
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${quote.provider.rating}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = Gray700
                        )
                        Text(
                            text = "(${quote.provider.reviewCount} avis)",
                            fontSize = 13.sp,
                            color = Gray500
                        )
                    }
                }

                // Price
                Text(
                    text = "${quote.price} €",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Gray900
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quote.date}, ${quote.timeSlot}",
                    fontSize = 13.sp,
                    color = Gray500
                )
                Text(
                    text = "${quote.provider.distanceKm} km",
                    fontSize = 13.sp,
                    color = Gray500
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CTA Button
            Button(
                onClick = onQuotesClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrustBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Comparer les 3 devis",
                    modifier = Modifier.padding(vertical = 4.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    TrustRepairTheme {
        ChatScreen(
            onVerifyClick = {},
            onQuotesClick = {},
            isVerified = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TypingIndicatorPreview() {
    TrustRepairTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TypingIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickReplyChipPreview() {
    TrustRepairTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickReplyChip(text = "Plomberie", onClick = {})
            QuickReplyChip(text = "Électricité", onClick = {})
        }
    }
}
