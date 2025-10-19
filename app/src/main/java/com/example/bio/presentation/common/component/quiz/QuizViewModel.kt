package com.example.bio.presentation.common.component.quiz

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bio.presentation.common.component.chat.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// برای مدیریت وضعیت‌های مختلف UI (بدون تغییر)
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val topic: String, val questions: List<Question>) : QuizUiState()
    data class Evaluating(val message: String) : QuizUiState()
    data class Result(val results: List<EvaluationResult>) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

// برای نمایش نتایج در UI (بدون تغییر)
data class EvaluationResult(
    val question: Question,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val score: Float
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val userAnswers = mutableStateMapOf<Int, String>()
    private var correctAnswers = mapOf<Int, String>()
    private var allQuestions = listOf<Question>()

    fun loadQuiz(pdfFilename: String) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val response = apiService.generateQuiz(QuizRequest(pdf_filename = pdfFilename))
                if (response.isSuccessful && response.body() != null) {
                    val quiz = response.body()!!
                    correctAnswers = quiz.answers.associateBy({ it.id }, { it.text })
                    allQuestions = quiz.questions
                    quiz.questions.forEach { userAnswers[it.id] = "" }
                    _uiState.value = QuizUiState.Success(quiz.topic, quiz.questions)
                } else {
                    _uiState.value = QuizUiState.Error("Failed to load quiz: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    // ✅ تابع بازنویسی شده برای ارسال دسته‌ای
    fun submitQuiz() {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Evaluating("Evaluating your quiz, please wait...")

            // ۱. لیست پاسخ‌ها را برای ارسال در یک درخواست آماده کن
            val evaluationPayloads = correctAnswers.map { (questionId, correctAnswerText) ->
                AnswerEvaluationPayload(
                    question_id = questionId,
                    user_answer = userAnswers[questionId] ?: "",
                    correct_answer = correctAnswerText
                )
            }
            val request = EvaluateQuizRequest(answers = evaluationPayloads)

            try {
                // ۲. فقط یک درخواست API برای ارزیابی تمام پاسخ‌ها ارسال کن
                val response = apiService.evaluateQuiz(request)

                if (response.isSuccessful && response.body() != null) {
                    val batchResults = response.body()!!.results
                    val resultsMap = batchResults.associateBy { it.question_id }

                    // ۳. نتایج دریافت شده را با سوالات مپ کن تا در UI نمایش داده شوند
                    val finalResults = allQuestions.mapNotNull { question ->
                        resultsMap[question.id]?.let { result ->
                            EvaluationResult(
                                question = question,
                                userAnswer = userAnswers[question.id] ?: "",
                                correctAnswer = correctAnswers[question.id] ?: "",
                                isCorrect = result.is_correct,
                                score = result.similarity_score
                            )
                        }
                    }
                    _uiState.value = QuizUiState.Result(finalResults)
                } else {
                    _uiState.value = QuizUiState.Error("Failed to evaluate quiz: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error("Network error during evaluation: ${e.localizedMessage}")
            }
        }
    }
}