package com.example.bio.presentation.common.component.chat

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bio.AppDestinations
import com.example.bio.R

// دیتا مدل ساده برای پلن‌ها
data class SubscriptionPlan(
    val id: Int,
    val title: String,
    val price: String,
    val duration: String,
    val features: List<String>,
    val isRecommended: Boolean = false,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController
) {
    // رنگ‌های تم
    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)

    // وضعیت انتخاب پلن
    var selectedPlan by remember { mutableStateOf<SubscriptionPlan?>(null) }

    // تعریف پلن‌ها
    val plans = remember {
        listOf(
            SubscriptionPlan(
                id = 1,
                title = "پایه",
                price = "رایگان",
                duration = "همیشگی",
                features = listOf("دسترسی محدود به چت", "پاسخ‌دهی استاندارد", "بدون پشتیبانی اختصاصی"),
                isRecommended = false,
                icon = Icons.Filled.Star
            ),
            SubscriptionPlan(
                id = 2,
                title = "حرفه‌ای",
                price = "۹۹,۰۰۰ تومان",
                duration = "ماهانه",
                features = listOf("دسترسی نامحدود", "پاسخ‌دهی سریع (Turbo)", "دسترسی به مدل‌های پیشرفته", "حذف تبلیغات"),
                isRecommended = true,
                icon = Icons.Filled.Star
            ),
            SubscriptionPlan(
                id = 3,
                title = "سازمانی",
                price = "۸۹۰,۰۰۰ تومان",
                duration = "سالانه",
                features = listOf("تمام ویژگی‌های حرفه‌ای", "پشتیبانی ۲۴ ساعته", "API اختصاصی", "تخفیف ۲۰ درصدی"),
                isRecommended = false,
                icon = Icons.Filled.Star
            )
        )
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("خرید اشتراک", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = navyColor,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // دکمه پرداخت در پایین صفحه
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {

                        if (selectedPlan != null) {
                            navController.navigate(
                                AppDestinations.createPaymentRoute(
                                    planTitle = selectedPlan!!.title,
                                    planPrice = selectedPlan!!.price
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedPlan != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = orangeColor,
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    val buttonText = if (selectedPlan != null) {
                        "پرداخت ${selectedPlan!!.price}"
                    } else {
                        "یک پلن را انتخاب کنید"
                    }
                    Text(
                        text = buttonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // هدر صفحه
            Text(
                text = "پلن مناسب خود را انتخاب کنید",
                style = MaterialTheme.typography.headlineSmall,
                color = navyColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "برای دسترسی به امکانات پیشرفته هوش مصنوعی، اشتراک تهیه کنید.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // نمایش کارت‌ها
            plans.forEach { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = selectedPlan?.id == plan.id,
                    onSelect = { selectedPlan = plan },
                    navyColor = navyColor,
                    orangeColor = orangeColor
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelect: () -> Unit,
    navyColor: Color,
    orangeColor: Color
) {
    // انیمیشن تغییر رنگ
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) navyColor else Color.White,
        label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else navyColor,
        label = "textColor"
    )
    val featureTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.DarkGray,
        label = "featureColor"
    )

    val borderColor = if (isSelected) orangeColor else if (plan.isRecommended) orangeColor else Color.LightGray.copy(alpha = 0.5f)
    val borderWidth = if (isSelected || plan.isRecommended) 2.dp else 1.dp
    val elevation = if (isSelected) 8.dp else 2.dp

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect() },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(borderWidth, borderColor),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // عنوان پلن
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // قیمت
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isSelected) Color.White else if (plan.isRecommended) orangeColor else Color.Black,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = plan.duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(24.dp))

                // ✅ ویژگی‌ها (اصلاح شده برای راست‌چین بودن)
                // با استفاده از RTL، ترتیب Row به صورت [آیکون] [فاصله] [متن] از سمت راست چیده می‌شود
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        plan.features.forEach { feature ->
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth(), // تمام عرض را پر می‌کند تا تراز درست باشد
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = if (isSelected) orangeColor else if (plan.isRecommended) orangeColor else navyColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = featureTextColor,
                                    textAlign = TextAlign.Right // متن هم راست‌چین شود
                                )
                            }
                        }
                    }
                }
            }
        }

        // بج (Badge) پیشنهاد ویژه
        if (plan.isRecommended) {
            Surface(
                color = orangeColor,
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "پیشنهاد ویژه",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // بج (Badge) انتخاب شده
        if (isSelected) {
            Surface(
                color = orangeColor,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}