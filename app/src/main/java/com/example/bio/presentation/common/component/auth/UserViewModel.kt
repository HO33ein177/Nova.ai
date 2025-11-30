package com.example.bio.presentation.common.component.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bio.data.local.dao.UserDao
import com.example.bio.data.local.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID


private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ✅ اصلاح ۱: تغییر نوع StateFlow به User?
    // (دیگر از CombinedUserInfo استفاده نمی‌کنیم تا پیچیدگی کم شود)
    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isLoading.value = true
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            fetchLocalUserData(firebaseUser)
        } else {
            _userInfo.value = null
            _isLoading.value = false
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun fetchLocalUserData(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            try {
                val email = firebaseUser.email
                if (email != null) {
                    val localUser = withContext(Dispatchers.IO) {
                        userDao.getUserByEmail(email)
                    }
                    // ✅ اصلاح ۲: مستقیم یوزر را ست می‌کنیم
                    _userInfo.value = localUser
                } else {
                    _userInfo.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching local user data", e)
                _userInfo.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    // ✅ اصلاح ۳: این تابع حالا درست کار می‌کند چون نوع‌ها یکی شدند
    fun getUserInfo(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userDao.getUserById(userId)
                _userInfo.value = user
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfilePicture(userId: Int, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. کپی کردن عکس به حافظه داخلی برنامه
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "profile_${userId}_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)

                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // 2. ذخیره مسیر فایل جدید در دیتابیس
                val newPath = file.absolutePath
                userDao.updateUserProfilePicture(userId, newPath)

                // 3. آپدیت کردن StateFlow برای نمایش آنی در UI
                val currentUser = _userInfo.value
                if (currentUser != null) {
                    _userInfo.value = currentUser.copy(profilePicturePath = newPath)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}