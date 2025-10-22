package com.example.bio.presentation.common.component.chat

data class FunctionCallingResponse(
    val functionName: String,
    val parameters: Map<String, Any>
)
