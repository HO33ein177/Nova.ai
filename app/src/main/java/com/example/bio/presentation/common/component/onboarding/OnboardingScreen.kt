package com.example.bio.presentation.common.component.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bio.AppDestinations
import com.example.bio.R

@Composable
fun OnboardingScreen(navController: NavController) {

    // رنگ‌های تم
    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)
    val greyText = colorResource(R.color.pro_grey_text)

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // فاصله بین بالا و پایین
        ) {

            // بخش بالای صفحه (عکس و متن)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f), // پر کردن فضای بالا
                verticalArrangement = Arrangement.Center
            ) {
                // ✅✅✅ تغییرات جدید اینجاست:

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(180.dp) // سایز بزرگ و مناسب برای وسط صفحه
                        .padding(bottom = 18.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Nova AI",
                    style = MaterialTheme.typography.displayMedium.copy( // فونت درشت و خوانا
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp
                    ),
                    color = navyColor, // رنگ سرمه‌ای تم
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = "دستیار هوشمند شما",
                    style = MaterialTheme.typography.headlineMedium,
                    color = navyColor,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // بقیه متن‌ها (توضیحات) در ادامه می‌آیند...
                Text(
                    text = "با استفاده از این برنامه می‌توانید\nسوالات خود را به صورت صوت یا متن بپرسید\nو پاسخ هوشمند دریافت کنید.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = greyText,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }

            // بخش پایین (دکمه)
            Button(
                onClick = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.ONBOARDING_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // دکمه بزرگ و راحت
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = orangeColor, // دکمه اکشن نارنجی
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "شروع کنید",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }
    }
}