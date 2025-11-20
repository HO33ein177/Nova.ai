package com.example.bio.presentation.common.component.auth.signup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bio.R
import com.example.bio.data.local.dao.UserDao
import com.example.bio.data.local.entity.User
import com.example.bio.presentation.common.component.reusable.MyBasicTextField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

sealed interface SignupResult {
    data object Idle : SignupResult
    data object Loading : SignupResult
    data class Success(val userId: Long) : SignupResult
    data class Error(val message: String) : SignupResult
}

private const val TAG = "SignupScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavController,
    onSignupSuccess: (Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var signupStatus by remember { mutableStateOf<SignupResult>(SignupResult.Idle) }
    val isLoading = signupStatus is SignupResult.Loading
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Colors
    val navyColor = colorResource(R.color.pro_navy_dark)
    val orangeColor = colorResource(R.color.pro_orange)
    val backgroundColor = colorResource(R.color.pro_white_smoke)

    val hiltEntryPoint = EntryPointAccessors.fromActivity(
        context as androidx.activity.ComponentActivity,
        SignupScreenEntryPoint::class.java
    )
    val firebaseAuth = hiltEntryPoint.getFirebaseAuth()
    val userDao = hiltEntryPoint.getUserDao()

    LaunchedEffect(signupStatus) {
        when (val status = signupStatus) {
            is SignupResult.Success -> {
                Toast.makeText(context, "حساب کاربری با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show()
                onSignupSuccess(status.userId)
            }
            is SignupResult.Error -> {
                Toast.makeText(context, status.message, Toast.LENGTH_LONG).show()
                signupStatus = SignupResult.Idle
            }
            else -> {}
        }
    }

    fun attemptSignup() {
        // ... (Logic remains the same)
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank()) {
            signupStatus = SignupResult.Error("لطفا تمام فیلدها را پر کنید.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupStatus = SignupResult.Error("فرمت ایمیل صحیح نیست.")
            return
        }
        if (password != confirmPassword) {
            signupStatus = SignupResult.Error("رمز عبور و تکرار آن یکسان نیستند.")
            return
        }
        signupStatus = SignupResult.Loading
        coroutineScope.launch {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val localUser = User(email = email, password = "", name = name, firebaseUid = firebaseUser.uid)
                    val insertedUserId = withContext(Dispatchers.IO) { userDao.insert(localUser) }
                    signupStatus = SignupResult.Success(insertedUserId)
                } else {
                    signupStatus = SignupResult.Error("خطا در ایجاد کاربر.")
                }
            } catch (e: Exception) {
                signupStatus = SignupResult.Error("خطا: ${e.localizedMessage}")
            }
        }
    }

    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .imePadding() // ✅ 1. Padding for keyboard
                .verticalScroll(rememberScrollState()), // اضافه کردن اسکرول برای صفحه گوشی‌های کوچک
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = "بپیوندید Nova AI به",
                style = MaterialTheme.typography.headlineSmall,
                color = navyColor, // ۳. تیتر: سرمه‌ای
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Inputs
            MyBasicTextField(
                value = name,
                onValueChange = { name = it },
                label = "نام و نام خانوادگی",
                trailingIcon = Icons.Outlined.Person,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyBasicTextField(
                value = email,
                onValueChange = { email = it },
                label = "آدرس ایمیل",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                trailingIcon = Icons.Outlined.Email,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyBasicTextField(
                value = password,
                onValueChange = { password = it },
                label = "رمز عبور",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = Icons.Outlined.Lock,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            MyBasicTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "تکرار رمز عبور",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = Icons.Outlined.Lock,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Signup Button
            Button(
                onClick = { attemptSignup() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = orangeColor, // ۴. دکمه: نارنجی
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ثبت نام", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.textButtonColors(contentColor = navyColor) // ۵. لینک: سرمه‌ای
            ) {
                Text("قبلاً ثبت‌نام کرده‌اید؟ ورود")
            }
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.android.components.ActivityComponent::class)
interface SignupScreenEntryPoint {
    fun getFirebaseAuth(): FirebaseAuth
    fun getUserDao(): UserDao
}