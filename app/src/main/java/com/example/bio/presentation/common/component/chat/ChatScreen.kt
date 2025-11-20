package com.example.bio.presentation.common.component.chat

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.bio.AppDestinations
import com.example.bio.R
import com.example.bio.presentation.common.component.auth.UserViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
<<<<<<< Updated upstream
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
=======
import java.util.*
>>>>>>> Stashed changes

data class ConversationSummary(
    val conversationId: String,
    val lastMessageTimestamp: Long,
    val firstMessageContent: String?
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    navController: NavController,
    userId: Int,
    conversationId: String
) {
    val context = LocalContext.current
    val chatViewModel: ChatViewModel = hiltViewModel()
    val conversationListViewModel: ConversationListViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val chatHistory by chatViewModel.chatHistory.collectAsStateWithLifecycle()
    val isLoading by chatViewModel.isLoading.collectAsStateWithLifecycle()
    val isRecording by chatViewModel.isRecording.collectAsStateWithLifecycle()
    val conversationSummaries by conversationListViewModel.conversationSummaries.collectAsStateWithLifecycle()
    val isHistoryLoading by conversationListViewModel.isLoading.collectAsStateWithLifecycle()

    var userInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var showRationaleDialog by remember { mutableStateOf(false) }
    var isHistoryPageOpen by remember { mutableStateOf(false) }

    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)

    LaunchedEffect(userId, conversationId) {
        chatViewModel.loadDataForConversation(userId, conversationId)
    }

    LaunchedEffect(userId, isHistoryPageOpen) {
        if (isHistoryPageOpen) {
            conversationListViewModel.loadConversationSummaries(userId)
        }
    }

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (isImeVisible && chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                .exclude(WindowInsets.navigationBars)
                .exclude(WindowInsets.ime),
            modifier = Modifier.imePadding(),
            topBar = {
                Surface(shadowElevation = 4.dp) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "Nova AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Chat ID: ...${conversationId.takeLast(4)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(AppDestinations.SUBSCRIPTION_ROUTE) }) {
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = "Subscription",
                                    tint = orangeColor
                                )
                            }
                            IconButton(onClick = { isHistoryPageOpen = true }) {
                                Icon(Icons.Filled.History, "History", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = navyColor
                        )
                    )
                }
            },
            bottomBar = {
                ChatInputArea(
                    userInput = userInput,
                    onUserInputChanged = { userInput = it },
                    onSendMessage = {
                        if (userInput.isNotBlank()) {
                            chatViewModel.sendMessage(userInput)
                            userInput = ""
                        }
                    },
                    isLoading = isLoading,
                    isRecording = isRecording,
                    onRecordStart = {
                        if (recordAudioPermissionState.status.isGranted) {
                            chatViewModel.startRecordingAudio()
                        } else if (recordAudioPermissionState.status.shouldShowRationale) {
                            showRationaleDialog = true
                        } else {
                            recordAudioPermissionState.launchPermissionRequest()
                        }
                    },
                    onRecordStop = {
                        val prompt = userInput.ifBlank { "Describe this audio" }
                        chatViewModel.stopRecordingAudioAndSend(prompt)
                        userInput = ""
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            listOf(backgroundColor, Color.White)
                        )
                    )
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
<<<<<<< Updated upstream
                    items(chatHistory) { message ->
                        ChatBubble(message = message)
=======
                    items(chatHistory, key = { it.id }) { message ->
                        ChatBubble(
                            message = message,
                            onDelete = { chatViewModel.deleteMessage(message.id) }
                        )
>>>>>>> Stashed changes
                    }

                    if (isLoading && chatHistory.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxSize().padding(top = 50.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = orangeColor)
                            }
                        }
                    } else if (isLoading && !isRecording) {
                        item {
                            TypingIndicator(navyColor)
                        }
                    }
                }

                if (chatHistory.isEmpty() && !isLoading && !isRecording) {
                    InitialPrompts(onPromptClick = { chatViewModel.sendMessage(it) })
                }
            }
        }

        HistoryPage(
            visible = isHistoryPageOpen,
            onClose = { isHistoryPageOpen = false },
            conversationSummaries = conversationSummaries,
            isLoading = isHistoryLoading,
            onHistoryItemSelected = { selectedConvId ->
                if (selectedConvId != conversationId) {
                    navController.navigate(AppDestinations.createChatRoute(userId, selectedConvId)) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = false }
                    }
                }
                isHistoryPageOpen = false
            },
            onNewChatClicked = { newConvId ->
                navController.navigate(AppDestinations.createChatRoute(userId, newConvId)) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = false }
                }
                isHistoryPageOpen = false
            },
            currentUserId = userId,
            onLogout = {
                userViewModel.signOut()
                navController.navigate(AppDestinations.LOGIN_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    launchSingleTop = true
                }
                Toast.makeText(context, "از حساب خارج شدید", Toast.LENGTH_SHORT).show()
            }
        )

        AnimatedVisibility(
            visible = isRecording,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            RecordingOverlay()
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("دسترسی میکروفون") },
            text = { Text("برای ضبط پیام صوتی نیاز به دسترسی میکروفون داریم.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRationaleDialog = false
                        recordAudioPermissionState.launchPermissionRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                ) { Text("تایید") }
            },
            dismissButton = {
                Button(onClick = { showRationaleDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = navyColor)) { Text("لغو") }
            }
        )
    }
}

@Composable
fun ChatInputArea(
    userInput: String,
    onUserInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    isLoading: Boolean,
    isRecording: Boolean,
    onRecordStart: () -> Unit,
    onRecordStop: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isMicPressed by interactionSource.collectIsPressedAsState()
    val orangeColor = colorResource(R.color.pro_orange)
    val navyColor = colorResource(R.color.pro_navy_dark)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    LaunchedEffect(isMicPressed) {
        if (isMicPressed) {
            if (!isRecording && !isLoading) onRecordStart()
        } else {
            if (isRecording) onRecordStop()
        }
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- دکمه میکروفون ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .graphicsLayer {
                        scaleX = if (isRecording) pulseScale else 1f
                        scaleY = if (isRecording) pulseScale else 1f
                    }
                    .background(
                        color = if (isRecording) orangeColor else navyColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (!isLoading) {
                                    try {
                                        onRecordStart()
                                        tryAwaitRelease()
                                    } finally {
                                        onRecordStop()
                                    }
                                }
                            }
                        )
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Record",
                    tint = if (isRecording) Color.White else navyColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // --- ورودی متن (هوشمند) ---
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = if (isRecording) BorderStroke(1.dp, orangeColor) else null
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = onUserInputChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),

                        // ✅✅✅ اصلاح جهت متن: ContentOrLtr
                        // اگر متن فارسی باشد راست‌چین می‌شود، اگر انگلیسی باشد چپ‌چین
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textDirection = androidx.compose.ui.text.style.TextDirection.ContentOrLtr
                        ),

                        placeholder = {
                            Text(
                                "...پیامی بنویسید",
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(),
                                // ✅ متن پیش‌فرض را همیشه راست‌چین نگه می‌داریم (چون فارسی است)
                                textAlign = TextAlign.Right
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { if (userInput.isNotBlank()) onSendMessage() }),
                        enabled = !isLoading,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent,
                            cursorColor = orangeColor,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            // --- دکمه ارسال ---
            val isSendEnabled = userInput.isNotBlank() && !isLoading && !isRecording
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = if (isSendEnabled) orangeColor else navyColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(
                        enabled = isSendEnabled,
                        onClick = onSendMessage
                    )
            ) {
                Icon(
                    // اگر آیکون جهت‌دار است، برای زبان فارسی باید Mirrored شود
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (isSendEnabled) Color.White else navyColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
<<<<<<< Updated upstream
fun InitialPrompts(onPromptClick: (String) -> Unit) {
    val prompts = listOf(
        "Explain quantum physics",
        "Explain black holes simply",
        "Write a tweet about global warming",
        "Write a poem about love and roses",
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Try asking:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        prompts.forEach { prompt ->
            SuggestionChip(
                onClick = { onPromptClick(prompt) },
                label = { Text(prompt, textAlign = TextAlign.Center) },
                modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(0.9f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "...or hold the Mic button to speak!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer
    else if (message.isError) MaterialTheme.colorScheme.errorContainer
    else MaterialTheme.colorScheme.secondaryContainer
=======
fun ChatBubble(message: ChatMessage, onDelete: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val navyColor = colorResource(R.color.pro_navy_dark)
    val surfaceColor = colorResource(R.color.pro_surface_light)
>>>>>>> Stashed changes

    val bubbleColor = if (message.isFromUser) navyColor else surfaceColor
    val textColor = if (message.isFromUser) Color.White else Color.Black
    val shadowElevation = if (message.isFromUser) 0.dp else 2.dp

    val shape = if (message.isFromUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val isRtl = message.text.any { it in '\u0600'..'\u06FF' }
    val textAlignment = if (isRtl) TextAlign.Right else TextAlign.Left
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

<<<<<<< Updated upstream

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = if (message.isFromUser) 16.dp else 4.dp,
                    topEnd = if (message.isFromUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = bubbleColor,
                modifier = Modifier
                    .align(alignment)
                    .padding(
                        start = if (message.isFromUser) 40.dp else 0.dp,
                        end = if (message.isFromUser) 0.dp else 40.dp
                    )
                    .wrapContentWidth()
                    .widthIn(min = 60.dp)
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    textAlign = textAlignment
                )
=======
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.align(alignment)) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                if (!message.isFromUser) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = navyColor.copy(alpha = 0.1f),
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.robo_icon),
                                contentDescription = "Bot",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (message.isFromUser && !message.isError) {
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("کپی") },
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(message.text))
                                    Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                                    menuExpanded = false
                                },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("حذف", color = Color.Red) },
                                onClick = {
                                    onDelete()
                                    menuExpanded = false
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Surface(
                    shape = shape,
                    color = bubbleColor,
                    shadowElevation = shadowElevation,
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                        textAlign = if (message.text.any { it in '\u0600'..'\u06FF' }) TextAlign.Right else TextAlign.Left
                    )
                }

                if (!message.isFromUser && !message.isError) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box {
                        IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.MoreVert, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("کپی") }, onClick = {
                                clipboardManager.setText(AnnotatedString(message.text))
                                Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                                menuExpanded = false
                            }, leadingIcon = { Icon(Icons.Default.ContentCopy, null) })

                            DropdownMenuItem(text = { Text("حذف", color = Color.Red) }, onClick = {
                                onDelete()
                                menuExpanded = false
                            }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator(color: Color) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("در حال نوشتن...", style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Composable
fun InitialPrompts(onPromptClick: (String) -> Unit) {
    val prompts = listOf(
        "تفسیر آزمایش خون",
        "توصیه های سلامتی برای دیابت",
        "برنامه غذایی سالم",
        "اطلاعات دارویی"
    )
    val navyColor = colorResource(R.color.pro_navy_dark)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.robo_icon),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 24.dp),
            tint = navyColor.copy(alpha = 0.2f)
        )

        Text(
            "چطور می‌توانم کمکتان کنم؟",
            style = MaterialTheme.typography.headlineSmall,
            color = navyColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // لیست پیشنهادها
        prompts.forEach { prompt ->
            Card(
                onClick = { onPromptClick(prompt) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                // ✅✅✅ تغییر مهم: اعمال جهت راست‌چین (RTL) برای محتوای کارت
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // پر کردن عرض برای چیدمان درست
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // چون RTL کردیم، این آیکون اول (سمت راست) قرار می‌گیرد
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = navyColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        // متن بعد از آیکون (سمت چپ آیکون) قرار می‌گیرد
                        Text(
                            text = prompt,
                            color = navyColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f), // پر کردن فضای باقی‌مانده
                            textAlign = TextAlign.Right // راست‌چین کردن متن داخل فضای خودش
                        )
                    }
                }
>>>>>>> Stashed changes
            }
        }
    }
}


@Composable
fun HistoryPage(
    visible: Boolean,
    onClose: () -> Unit,
    conversationSummaries: List<ConversationSummary>,
    isLoading: Boolean,
    onHistoryItemSelected: (String) -> Unit,
    onNewChatClicked: (String) -> Unit,
    currentUserId: Int,
    onLogout: () -> Unit
) {
    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)

    Box(modifier = Modifier.fillMaxSize()) {
        if (visible) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onClose() }
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.85f),
                color = colorResource(R.color.pro_white_smoke),
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    Spacer(modifier = Modifier.height(12.dp))

                    // Header
                    Row(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .background(navyColor)
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onClose() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Spacer(Modifier.weight(1f))
                        Text("تاریخچه چت‌ها", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // New Chat Button
                    Surface(
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(12.dp)).clickable {
                            onNewChatClicked(UUID.randomUUID().toString())
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.Add, null, tint = orangeColor)
                            Spacer(Modifier.width(8.dp))
                            Text("شروع چت جدید", color = orangeColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isLoading) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = orangeColor)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(conversationSummaries) { summary ->
                                HistoryRow(
                                    summary = summary,
                                    isSelected = false,
                                    onItemClick = { onHistoryItemSelected(it.conversationId) },
                                    formatTimestamp = { time -> SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(time)) }
                                )
                                Divider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }

                    Divider(color = Color.LightGray)
                    // Footer
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = navyColor.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("U", color = navyColor, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("کاربر", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("$currentUserId", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "خروج",
                                tint = Color.Red.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryRow(
    summary: ConversationSummary,
    isSelected: Boolean,
    onItemClick: (ConversationSummary) -> Unit,
    formatTimestamp: (Long) -> String
) {
    val navyColor = colorResource(R.color.pro_navy_dark)

    ListItem(
        modifier = Modifier.clickable { onItemClick(summary) },
        headlineContent = {
            Text(
                text = summary.firstMessageContent?.take(40) ?: "گفتگوی جدید",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                color = navyColor
            )
        },
        supportingContent = {
            Text(text = formatTimestamp(summary.lastMessageTimestamp), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        },
        leadingContent = {
            Icon(Icons.Filled.Chat, null, tint = navyColor.copy(alpha = 0.5f))
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

// ✅✅✅ اصلاح شده: انیمیشن ضبط با چیدمان صحیح (Column)
@Composable
fun RecordingOverlay() {
    val orangeColor = colorResource(R.color.pro_orange)

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        // استفاده از Column برای چیدن آیکون و متن زیر هم
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // دایره پالس
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .background(orangeColor, CircleShape)
                )

                // آیکون ثابت
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = orangeColor,
                    shadowElevation = 10.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Recording",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "...در حال ضبط",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // ✅ متن را داخل کادر وسط‌چین می‌کند
                modifier = Modifier.fillMaxWidth() // ✅ عرض کامل می‌گیرد تا وسط‌چین دقیق باشد
            )
        }
    }
}