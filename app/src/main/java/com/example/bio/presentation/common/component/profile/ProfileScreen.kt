package com.example.bio.presentation.common.component.profile

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.bio.AppDestinations
import com.example.bio.R
import com.example.bio.presentation.common.component.auth.UserViewModel
import com.example.bio.presentation.common.component.auth.login.VazirFont
import com.example.bio.presentation.common.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userId: Int,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)

    val context = LocalContext.current
    val userInfo by userViewModel.userInfo.collectAsStateWithLifecycle()

    // وضعیت‌های مربوط به برش عکس
    var showImageCropper by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(userId) {
        userViewModel.getUserInfo(userId)
    }

    // انتخاب عکس از گالری
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                // عکس انتخاب شد، حالا بیت‌مپ را می‌سازیم و دیالوگ برش را باز می‌کنیم
                val bitmap = ImageUtils.getBitmapFromUri(context, uri)
                if (bitmap != null) {
                    tempBitmap = bitmap
                    tempImageUri = uri
                    showImageCropper = true
                } else {
                    Toast.makeText(context, "خطا در بارگذاری عکس", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    // --- دیالوگ برش عکس (Custom Image Cropper) ---
    if (showImageCropper && tempBitmap != null) {
        ImageCropDialog(
            bitmap = tempBitmap!!,
            onDismiss = { showImageCropper = false },
            onCropSuccess = { croppedFile ->
                // ارسال فایل نهایی برش خورده به ویومدل
                userViewModel.updateProfilePicture(userId, Uri.fromFile(croppedFile))
                showImageCropper = false
            }
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    title = { Text("پروفایل کاربری", color = Color.White, fontFamily = VazirFont, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = navyColor)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // --- آواتار ---
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        shape = CircleShape,
                        color = navyColor.copy(alpha = 0.1f),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        border = BorderStroke(2.dp, navyColor.copy(alpha = 0.2f))
                    ) {
                        if (userInfo?.profilePicturePath != null) {
                            AsyncImage(
                                model = File(userInfo!!.profilePicturePath!!),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = navyColor,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = orangeColor,
                        modifier = Modifier
                            .size(36.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userInfo?.name ?: "کاربر مهمان",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = VazirFont,
                    fontWeight = FontWeight.Bold,
                    color = navyColor
                )
                Text(
                    text = "شناسه کاربر: $userId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontFamily = VazirFont
                )

                Spacer(modifier = Modifier.height(32.dp))

                // کارت اشتراک
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = orangeColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.WorkspacePremium, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("وضعیت اشتراک", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium, fontFamily = VazirFont)
                            Text(
                                text = "طرح رایگان (پایه)",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = VazirFont
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { navController.navigate(AppDestinations.SUBSCRIPTION_ROUTE) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = orangeColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("ارتقا", fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                InfoItem(icon = Icons.Filled.Person, label = "نام کامل", value = userInfo?.name ?: "-")
                Spacer(modifier = Modifier.height(12.dp))
                InfoItem(icon = Icons.Filled.Email, label = "ایمیل", value = userInfo?.email ?: "-")
                Spacer(modifier = Modifier.height(12.dp))
                InfoItem(icon = Icons.Filled.CreditCard, label = "تاریخ عضویت", value = "۱۴۰۳/۰۸/۲۹")

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        userViewModel.signOut()
                        navController.navigate(AppDestinations.LOGIN_ROUTE) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "از حساب خارج شدید", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("خروج از حساب کاربری", fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    val navyColor = colorResource(R.color.pro_navy_dark)
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = navyColor.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontFamily = VazirFont)
                Text(value, style = MaterialTheme.typography.bodyLarge, color = Color.Black, fontFamily = VazirFont)
            }
        }
    }
}

// --- کامپوننت دیالوگ برش عکس ---
@Composable
fun ImageCropDialog(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onCropSuccess: (File) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSizePx by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // تمام صفحه
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // ناحیه نمایش عکس با قابلیت زوم و پن
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale *= zoom
                            // محدود کردن زوم
                            scale = scale.coerceIn(0.5f, 5f)

                            // حرکت دادن عکس
                            offset += pan
                        }
                    }
                    .onGloballyPositioned { coordinates ->
                        // عرض صفحه را برای محاسبه برش نگه می‌داریم (چون برش مربعی است)
                        containerSizePx = coordinates.size.width
                    },
                contentAlignment = Alignment.Center
            ) {
                // عکس اصلی
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth() // عکس عرض صفحه را پر می‌کند
                        .aspectRatio(1f) // عکس را در کادر مربعی نشان می‌دهیم
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.Fit
                )

                // ماسک دایره‌ای (برای راهنمایی کاربر)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val circleRadius = size.width / 2f
                    val path = Path().apply {
                        addOval(androidx.compose.ui.geometry.Rect(center = center, radius = circleRadius))
                    }

                    // سیاه کردن اطراف دایره
                    clipPath(path, clipOp = ClipOp.Difference) {
                        drawRect(color = Color.Black.copy(alpha = 0.7f))
                    }

                    // خط دور دایره
                    drawCircle(
                        color = Color.White,
                        radius = circleRadius,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // دکمه‌های پایین
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Icon(Icons.Filled.Close, null)
                    Spacer(Modifier.width(8.dp))
                    Text("لغو")
                }

                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            if (containerSizePx > 0) {
                                val cropped = ImageUtils.cropBitmap(bitmap, scale, offset, containerSizePx)
                                val file = ImageUtils.saveBitmapToFile(context, cropped)
                                withContext(Dispatchers.Main) {
                                    if (file != null) {
                                        onCropSuccess(file)
                                    } else {
                                        Toast.makeText(context, "خطا در ذخیره عکس", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.pro_orange))
                ) {
                    Icon(Icons.Filled.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("تایید")
                }
            }
        }
    }
}