package com.example.bio // Ensure correct package

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bio.presentation.common.component.auth.change_password.ChangePasswordScreen
import com.example.bio.presentation.common.component.auth.login.LoginScreen
import com.example.bio.presentation.common.component.auth.signup.SignupScreen
import com.example.bio.presentation.common.component.chat.ChatScreen
import com.example.bio.presentation.common.component.chat.ConversationListScreen
import com.example.bio.presentation.common.component.landing.SimpleLandingScreen
import com.example.bio.presentation.common.component.onboarding.OnboardingScreen
import com.example.bio.presentation.common.component.quiz.PdfListScreen
import com.example.bio.presentation.common.component.quiz.QuizScreen
import com.example.bio.presentation.common.component.splash.SplashScreen
import com.example.bio.presentation.common.component.theme.BioTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

private const val TAG = "AppNavigation" // For logging

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Call the NavHost setup
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH_ROUTE
    ) {
        // Splash Screen
        composable(route = AppDestinations.SPLASH_ROUTE) {
            SplashScreen(navController = navController)
        }

        // Onboarding Screen
        composable(route = AppDestinations.ONBOARDING_ROUTE) {
            OnboardingScreen(navController = navController)
        }

        // Login Screen
        composable(route = AppDestinations.LOGIN_ROUTE) {
            LoginScreen(navController = navController)
        }

        // Signup Screen
        composable(route = AppDestinations.SIGNUP_ROUTE) {
            SignupScreen(
                navController = navController,
                onSignupSuccess = { userId ->
                    val newConversationId = UUID.randomUUID().toString()
                    val route = AppDestinations.createChatRoute(userId.toInt(), newConversationId)
                    navController.navigate(route) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // Forget Password Screen
        composable(route = AppDestinations.FORGET_PASSWORD_ROUTE) {
            ChangePasswordScreen(navController = navController)
        }

        composable(route = AppDestinations.SUBSCRIPTION_ROUTE) {
            com.example.bio.presentation.common.component.chat.SubscriptionScreen(navController = navController)
        }

        composable(
            route = AppDestinations.PAYMENT_ROUTE,
            arguments = listOf(
                navArgument("planTitle") { type = NavType.StringType },
                navArgument("planPrice") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val planTitle = backStackEntry.arguments?.getString("planTitle") ?: "نامشخص"
            val planPrice = backStackEntry.arguments?.getString("planPrice") ?: "0"

            // فراخوانی صفحه جدید
            com.example.bio.presentation.common.component.chat.PaymentScreen(
                navController = navController,
                planTitle = planTitle,
                planPrice = planPrice
            )
        }


        // Conversation List Screen
        composable(
            route = AppDestinations.CONVERSATION_LIST_ROUTE,
            arguments = listOf(navArgument(NavArguments.USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavArguments.USER_ID)
            if (userId != null) {
                ConversationListScreen(navController = navController, userId = userId)
            } else {
                Text("Error: Missing User ID for Conversation List.")
            }
        }

        // Chat Screen
        composable(
            route = AppDestinations.CHAT_ROUTE,
            arguments = listOf(
                navArgument(NavArguments.USER_ID) { type = NavType.IntType },
                navArgument(NavArguments.CONVERSATION_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavArguments.USER_ID)
            val conversationId = backStackEntry.arguments?.getString(NavArguments.CONVERSATION_ID)

            if (userId != null && conversationId != null) {
                ChatScreen(
                    userId = userId,
                    conversationId = conversationId,
                    navController = navController
                )
            } else {
                Log.e(TAG, "Error: Missing required arguments for chat.")
                Text("Error: Missing required arguments for chat.")
            }
        }

        // Simple Landing Screen
        composable(
            route = AppDestinations.SIMPLE_LANDING_ROUTE,
            arguments = listOf(navArgument(NavArguments.USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavArguments.USER_ID)
            if (userId != null) {
                SimpleLandingScreen(userId = userId)
            } else {
                Log.e(TAG, "Error: Missing userId argument for Landing Screen.")
                Text("Error: Missing User ID for Landing Screen.")
            }
        }

        // --- نهایی شده بخش آزمون ---

        // 1. نقطه ورود به بخش آزمون: این مسیر صفحه لیست PDF را نشان می‌دهد
        composable(route = AppDestinations.QUIZ_ENTRY_ROUTE) {
            PdfListScreen(navController = navController)
        }

        // 2. مسیر داخلی برای نمایش صفحه آزمون پس از انتخاب یک فایل
        composable(
            route = AppDestinations.QUIZ_SCREEN_ROUTE,
            arguments = listOf(navArgument(NavArguments.PDF_FILENAME) { type = NavType.StringType })
        ) { backStackEntry ->
            val pdfFilename = backStackEntry.arguments?.getString(NavArguments.PDF_FILENAME)
            if (pdfFilename != null) {
                QuizScreen(navController = navController, pdfFilename = pdfFilename)
            } else {
                // در صورت بروز خطا به صفحه قبل برمی‌گردیم
                Text("Error: PDF filename not provided.")
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}