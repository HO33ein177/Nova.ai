package com.example.bio.presentation.common.component.auth.change_password

import android.widget.Toast
<<<<<<< Updated upstream
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
=======
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
>>>>>>> Stashed changes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material3.*
<<<<<<< Updated upstream
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
=======
import androidx.compose.runtime.*
>>>>>>> Stashed changes
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
<<<<<<< Updated upstream
=======
import androidx.compose.ui.unit.LayoutDirection
>>>>>>> Stashed changes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bio.R
import com.example.bio.presentation.common.component.auth.login.VazirFont
import com.example.bio.presentation.common.component.auth.login.getPersianErrorMessage
import com.example.bio.presentation.common.component.reusable.MyBasicTextField
<<<<<<< Updated upstream
=======
import kotlinx.coroutines.delay
>>>>>>> Stashed changes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val email by viewModel.email
    val resetStatus by viewModel.resetStatus
    val context = LocalContext.current
    val isLoading = resetStatus is ResetStatus.Loading

    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)
    val greyText = colorResource(R.color.pro_grey_text)

<<<<<<< Updated upstream
=======
    var errorMessage by remember { mutableStateOf<String?>(null) }

>>>>>>> Stashed changes
    LaunchedEffect(resetStatus) {
        when (val status = resetStatus) {
            is ResetStatus.Success -> {
                Toast.makeText(context, "لینک بازیابی ارسال شد.", Toast.LENGTH_LONG).show()
                viewModel.resetStatusHandled()
            }
            is ResetStatus.Error -> {
<<<<<<< Updated upstream
                Toast.makeText(context, status.message, Toast.LENGTH_LONG).show()
=======
                errorMessage = getPersianErrorMessage(status.message)
                delay(3000)
                errorMessage = null
>>>>>>> Stashed changes
                viewModel.resetStatusHandled()
            }
            else -> { }
        }
    }

<<<<<<< Updated upstream
    Scaffold(
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding() // ✅ 1. Padding for keyboard
                .verticalScroll(rememberScrollState()) // ✅ 2. Enable scrolling
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // آیکون بزرگ برای ویژوال بهتر
            Icon(
                imageVector = Icons.Outlined.MarkEmailRead,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = navyColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "فراموشی رمز عبور؟",
                style = MaterialTheme.typography.headlineMedium,
                color = navyColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ایمیل خود را وارد کنید تا لینک بازیابی رمز عبور برای شما ارسال شود.",
                style = MaterialTheme.typography.bodyMedium,
                color = greyText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input
            MyBasicTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = "آدرس ایمیل",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                trailingIcon = Icons.Outlined.Email,
                modifier = Modifier.fillMaxWidth(),
                isError = resetStatus is ResetStatus.Error
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = { viewModel.sendPasswordResetEmail() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && email.isNotBlank(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = orangeColor,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ارسال ایمیل بازیابی", fontSize = 16.sp, fontWeight = FontWeight.Bold)
=======
    fun validateAndSend() {
        if (email.isBlank()) {
            errorMessage = "لطفا ایمیل خود را وارد کنید."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "فرمت ایمیل وارد شده صحیح نیست."
            return
        }
        viewModel.sendPasswordResetEmail()
    }

    // ✅ راست‌چین
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("بازیابی رمز عبور", color = Color.White, fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = navyColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    navigationIcon = {}
                )
            },
            containerColor = backgroundColor
        ) { paddingValues ->

            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(40.dp))
                    Spacer(modifier = Modifier.weight(0.5f))

                    Icon(
                        imageVector = Icons.Outlined.MarkEmailRead,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = navyColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "فراموشی رمز عبور؟",
                        style = MaterialTheme.typography.headlineMedium,
                        color = navyColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = VazirFont
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ایمیل خود را وارد کنید تا لینک بازیابی رمز عبور برای شما ارسال شود.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = greyText,
                        textAlign = TextAlign.Center,
                        fontFamily = VazirFont,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = VazirFont)) {
                        MyBasicTextField(
                            value = email,
                            onValueChange = {
                                viewModel.onEmailChange(it)
                                if(errorMessage != null) errorMessage = null
                            },
                            label = "آدرس ایمیل",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            trailingIcon = Icons.Outlined.Email,
                            modifier = Modifier.fillMaxWidth(),
                            isError = resetStatus is ResetStatus.Error || errorMessage != null
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { validateAndSend() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading && email.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orangeColor,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("ارسال ایمیل بازیابی", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = VazirFont)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = navyColor)
                    ) {
                        Text("بازگشت به صفحه ورود", fontFamily = VazirFont, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))
>>>>>>> Stashed changes
                }

<<<<<<< Updated upstream
            // ✅ تغییر اینجاست: حذف weight(1f) و استفاده از فاصله ثابت
            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.textButtonColors(contentColor = navyColor)
            ) {
                Text("بازگشت به صفحه ورود")
=======
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = slideInVertically(initialOffsetY = { -it }),
                    exit = slideOutVertically(targetOffsetY = { -it }),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ErrorOutline,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Right,
                                fontFamily = VazirFont,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
>>>>>>> Stashed changes
            }

        }
    }
}