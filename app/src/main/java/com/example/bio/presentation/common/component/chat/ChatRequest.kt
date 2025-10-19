package com.example.bio.presentation.common.component.chat

data class ChatRequest(
    val query: String,
    val userId: Int
)

data class ChatResponse(
    val answer: String
)