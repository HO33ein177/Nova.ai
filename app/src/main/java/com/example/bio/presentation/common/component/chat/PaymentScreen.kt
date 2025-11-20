package com.example.bio.presentation.common.component.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    planTitle: String,
    planPrice: String
) {
    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)
    val surfaceColor = colorResource(R.color.pro_surface_light)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("تکمیل خرید", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = navyColor)
            )
        }
    ) { paddingValues ->

        // ✅ اعمال جهت راست‌چین برای کل صفحه
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // کارت فاکتور
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceColor,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // ✅ "خلاصه سفارش" وسط‌چین باقی می‌ماند
                        Text(
                            text = "خلاصه سفارش",
                            style = MaterialTheme.typography.titleLarge,
                            color = navyColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center // وسط‌چین
                        )

                        // ردیف نوع اشتراک (راست‌چین)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("نوع اشتراک:", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                            Text(planTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = navyColor)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // ردیف قیمت (راست‌چین)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("مبلغ قابل پرداخت:", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                            // چون RTL است، قیمت در سمت چپ (انتهای سطر) و متن در سمت راست قرار می‌گیرد
                            // اگر می‌خواهید برعکس باشد (قیمت راست، متن چپ)، جای Text ها را عوض کنید
                            Text(
                                text = planPrice,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = orangeColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // توضیحات امنیتی (راست‌چین)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // کل ردیف در مرکز افقی صفحه باشد
                ) {
                    Icon(Icons.Filled.VerifiedUser, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "پرداخت امن از طریق درگاه بانکی",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // دکمه پرداخت
                Button(
                    onClick = {
                        // TODO: اتصال به درگاه پرداخت
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orangeColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.CreditCard, null)
                    Spacer(Modifier.width(8.dp))
                    Text("انتقال به درگاه پرداخت", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}