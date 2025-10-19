package com.example.bio.presentation.common.component.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    pdfFilename: String,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = pdfFilename) {
        viewModel.loadQuiz(pdfFilename)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> CircularProgressIndicator()
                is QuizUiState.Error -> Text("Error: ${state.message}")
                is QuizUiState.Success -> QuizContent(
                    topic = state.topic,
                    questions = state.questions,
                    userAnswers = viewModel.userAnswers,
                    onAnswerChanged = { qId, ans -> viewModel.userAnswers[qId] = ans },
                    onSubmit = { viewModel.submitQuiz() }
                )
                is QuizUiState.Evaluating -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(state.message)
                }
                is QuizUiState.Result -> QuizResultContent(state.results)
            }
        }
    }
}

@Composable
fun QuizContent(
    topic: String,
    questions: List<Question>,
    userAnswers: Map<Int, String>,
    onAnswerChanged: (Int, String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = topic, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(questions) { question ->
                Text(text = "${question.id}. ${question.text}", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = userAnswers[question.id] ?: "",
                    onValueChange = { onAnswerChanged(question.id, it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Your answer") }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Submit Quiz")
        }
    }
}

@Composable
fun QuizResultContent(results: List<EvaluationResult>) {
    val correctCount = results.count { it.isCorrect }
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Result: You got $correctCount out of ${results.size} correct!", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(results) { result ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(result.question.text, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your answer: ${result.userAnswer}", style = MaterialTheme.typography.bodyMedium)
                        Text("Correct answer: ${result.correctAnswer}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = if (result.isCorrect) "✔ Correct" else "❌ Incorrect",
                            color = if (result.isCorrect) Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    }
}