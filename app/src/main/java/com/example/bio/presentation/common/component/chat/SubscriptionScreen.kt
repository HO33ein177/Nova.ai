package com.example.bio.presentation.common.component.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults

/**
 * یک دیتا کلاس برای نگهداری اطلاعات هر پلن اشتراک
 */
data class SubscriptionPlan(
    val title: String,
    val price: String,
    val priceDetails: String, // مثلا "ماهانه" یا "سالانه"
    val features: List<String>,
    val isRecommended: Boolean = false // برای هایلایت کردن پلن پیشنهادی
)

/**
 * صفحه اصلی نمایش پلن‌های اشتراک
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController
) {
    // لیست پلن‌ها
    val plans = listOf(
        SubscriptionPlan(
            title = "اقتصادی",
            price = "۹۹,۰۰۰",
            priceDetails = "تومان / ماهانه",
            features = listOf(
                "دسترسی به مدل هوش مصنوعی پایه",
                "پاسخ‌دهی با سرعت استاندارد",
                "۵۰ پیام در روز"
            )
        ),
        SubscriptionPlan(
            title = "متوسط",
            price = "۱۹۹,۰۰۰",
            priceDetails = "تومان / ماهانه",
            features = listOf(
                "دسترسی به مدل هوش مصنوعی پیشرفته",
                "پاسخ‌دهی سریع‌تر",
                "۲۰۰ پیام در روز",
                "پشتیبانی ۲۴ ساعته"
            ),
            isRecommended = true // این پلن هایلایت می‌شود
        ),
        SubscriptionPlan(
            title = "حرفه‌ای",
            price = "۴۹۹,۰۰۰",
            priceDetails = "تومان / ماهانه",
            features = listOf(
                "دسترسی به قوی‌ترین مدل هوش مصنوعی",
                "اولویت در پاسخ‌دهی",
                "پیام نامحدود",
                "پشتیبانی اختصاصی"
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("خرید اشتراک") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp) // پدینگ داخلی برای محتوا
                .verticalScroll(rememberScrollState()), // قابلیت اسکرول
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.WorkspacePremium,
                contentDescription = "Subscription Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = "پلن خود را انتخاب کنید",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "برای دسترسی به تمام امکانات، اشتراک تهیه کنید.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // رندر کردن کارت‌های اشتراک
            plans.forEach { plan ->
                SubscriptionPlanCard(plan = plan)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * کامپوزبل برای نمایش یک کارت اشتراک
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPlanCard(
    plan: SubscriptionPlan
) {
    val context = LocalContext.current
    val cardBorderColor = if (plan.isRecommended) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (plan.isRecommended) 2.dp else 1.dp,
            color = cardBorderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // نمایش "پیشنهادی" اگر پلن توصیه شده باشد
            if (plan.isRecommended) {
                // از کامپوننت استاندارد Material 3 استفاده می‌کنیم
                AssistChip(
                    onClick = { /* این چیپ نیازی به کلیک ندارد */ },
                    label = {
                        Text(
                            "پیشنهادی",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = null, // حذف بوردر پیش‌فرض
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // عنوان پلن
            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // قیمت
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = plan.priceDetails,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(start = 4.dp, bottom = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // لیست ویژگی‌ها
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // چینش از راست به چپ (در حالت RTL)
            ) {
                plan.features.forEach { feature ->
                    FeatureItem(text = feature)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // دکمه خرید
            Button(
                onClick = {
                    // TODO: منطق پرداخت را اینجا پیاده‌سازی کنید
                    // مثلا: launchBillingFlow(context, SkuDetails(plan.sku))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (plan.isRecommended) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    contentColor = if (plan.isRecommended) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = "انتخاب پلن ${plan.title}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * کامپوزبل برای نمایش یک آیتم در لیست ویژگی‌ها (با تیک)
 */
@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Feature Check",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start // برای اطمینان از چینش درست در RTL
        )
    }
}

