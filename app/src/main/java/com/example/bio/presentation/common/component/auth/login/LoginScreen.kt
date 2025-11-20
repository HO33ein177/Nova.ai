package com.example.bio.presentation.common.component.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bio.AppDestinations
import com.example.bio.R
import com.example.bio.presentation.common.component.reusable.MyBasicTextField
import com.example.bio.presentation.common.component.reusable.RoundedButton

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val email by viewModel.email
    val password by viewModel.password
    val loginState by viewModel.loginState
    val context = LocalContext.current
    val isLoading = loginState is LoginResult.Loading

    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)
    val surfaceColor = colorResource(R.color.pro_surface_light)

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginResult.Success -> {
                Toast.makeText(context, "ورود موفقیت‌آمیز بود", Toast.LENGTH_SHORT).show()
                val commonConversationId = "GLOBAL_CHAT_ID"
                val userId = state.userId
                navController.navigate(AppDestinations.createChatRoute(userId, commonConversationId)) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
                viewModel.resetLoginState()
            }
            is LoginResult.Error -> Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            else -> {}
        }
    }

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .imePadding() // ✅ 1. Padding for keyboard
                .verticalScroll(rememberScrollState()), // ✅ 2. Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally
            // removed verticalArrangement = Arrangement.Center to let weight/spacers handle positioning
        ) {

            // --- Logo and Header Section ---

            // Push content down slightly from top if needed, but weight below handles centering logic
            Spacer(modifier = Modifier.height(40.dp))
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Nova AI",
                    style = LocalTextStyle.current.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp
                    ),
                    color = navyColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    modifier = Modifier.size(100.dp)
                )
            }



            Spacer(modifier = Modifier.height(24.dp))

            // --- Login Form Card ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    // Removed .weight(1f) here because inside a Scrollable Column, items shouldn't typically have weight unless the column fills max height.
                    // But here we want it to just take necessary space.
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                color = surfaceColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "خوش آمدید",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.vazirmatn_bold)),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = navyColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "لطفاً وارد حساب کاربری خود شوید",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(R.color.pro_grey_text),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // --- Email Input ---
                    MyBasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        label = "آدرس ایمیل",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        keyboardActions = KeyboardActions(),
                        trailingIcon = Icons.Outlined.Email,
                        onValueChange = viewModel::changeEmail,
                        isError = loginState is LoginResult.Error
                    )

                    Spacer(Modifier.height(16.dp))

                    // --- Password Input ---
                    MyBasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        label = "رمز عبور",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { viewModel.attemptLogin() }),
                        isPassword = true,
                        trailingIcon = Icons.Outlined.Lock,
                        onValueChange = viewModel::changePassword,
                        isError = loginState is LoginResult.Error
                    )

                    Spacer(Modifier.height(32.dp))

                    // --- Login Button ---
                    Button(
                        onClick = { viewModel.attemptLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orangeColor,
                            contentColor = Color.White,
                            disabledContainerColor = orangeColor.copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "ورود",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- Links (with TextButton) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { navController.navigate(AppDestinations.FORGET_PASSWORD_ROUTE) },
                            colors = ButtonDefaults.textButtonColors(contentColor = navyColor)
                        ) {
                            Text(
                                text = "فراموشی رمز عبور",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(
                            onClick = { navController.navigate(AppDestinations.SIGNUP_ROUTE) },
                            colors = ButtonDefaults.textButtonColors(contentColor = navyColor)
                        ) {
                            Text(
                                text = "ثبت نام",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}