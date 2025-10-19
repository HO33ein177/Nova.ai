package com.example.bio.presentation.common.component.chat

import com.example.bio.presentation.common.component.quiz.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("chat")
    suspend fun sendTextMessage(@Body request: ChatRequest): Response<ChatResponse>

    @Multipart
    @POST("chat/audio")
    suspend fun sendAudioMessage(
        @Part("user_id") userId: RequestBody,
        @Part("conversation_id") conversationId: RequestBody,
        @Part audio: MultipartBody.Part
    ): Response<ChatResponse>

    @GET("pdfs")
    suspend fun getPdfList(): Response<PdfListResponse>

    @POST("generate/quiz")
    suspend fun generateQuiz(@Body request: QuizRequest): Response<QuizResponse>

    // ✅ تابع جدید برای ارزیابی دسته‌ای آزمون
    @POST("evaluate/answer")
    suspend fun evaluateQuiz(@Body request: EvaluateQuizRequest): Response<EvaluateQuizResponse>
}