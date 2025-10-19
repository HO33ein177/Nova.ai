package com.example.bio.presentation.common.component.quiz

// مدل‌های مربوط به دریافت لیست سوالات (بدون تغییر)
data class QuizRequest(val pdf_filename: String)

data class QuizResponse(
    val topic: String,
    val questions: List<Question>,
    val answers: List<Answer>
)

data class Question(
    val id: Int,
    val text: String
)

data class Answer(
    val id: Int,
    val text: String
)

// --- مدل‌های جدید برای ارزیابی دسته‌ای ---

// مدل اصلی برای ارسال یکجای تمام پاسخ‌ها
data class EvaluateQuizRequest(
    val answers: List<AnswerEvaluationPayload>
)

// مدلی برای نگهداری اطلاعات یک پاسخ جهت ارسال به سرور
data class AnswerEvaluationPayload(
    val question_id: Int,
    val user_answer: String,
    val correct_answer: String
)

// مدل اصلی برای دریافت یکجای تمام نتایج
data class EvaluateQuizResponse(
    val results: List<BatchEvaluationResult>
)

// مدل نتیجه برای هر سوال در پاسخ دسته‌ای از سرور
data class BatchEvaluationResult(
    val question_id: Int,
    val is_correct: Boolean,
    val similarity_score: Float
)