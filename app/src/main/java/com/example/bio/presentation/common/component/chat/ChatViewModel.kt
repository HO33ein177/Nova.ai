package com.example.bio.presentation.common.component.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bio.AudioRecorderManager
import com.example.bio.data.local.dao.MessageDao
import com.example.bio.data.local.entity.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

const val SENDER_USER = "user"
const val SENDER_AI = "ai"
private const val TAG = "ChatViewModel"

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageDao: MessageDao,
    @ApplicationContext private val applicationContext: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var currentUserId: Int? = null
    private var currentConversationId: String? = null
    private var historyLoadingJob: Job? = null

    private val audioRecorder = AudioRecorderManager(applicationContext)

    fun loadDataForConversation(userId: Int, conversationId: String) {
        if (currentConversationId == conversationId && !_isLoading.value) {
            return
        }

        currentUserId = userId
        currentConversationId = conversationId
        _chatHistory.value = emptyList()
        _isLoading.value = true

        historyLoadingJob?.cancel()
        loadAndObserveHistory(userId, conversationId)
    }

    private fun loadAndObserveHistory(userId: Int, conversationId: String) {
        historyLoadingJob = viewModelScope.launch {
            messageDao.getMessagesForConversation(userId, conversationId)
                .catch { throwable ->
                    handleError("Error loading chat history: ${throwable.localizedMessage}", throwable as? Exception ?: RuntimeException(throwable))
                    _isLoading.value = false
                }
                .collect { dbMessages ->
                    val uiMessages = dbMessages.map { dbMsg ->
                        ChatMessage(
                            id = dbMsg.id.toLong(),
                            text = dbMsg.content,
                            isFromUser = dbMsg.sender == SENDER_USER,
                            isError = false,
                            messageType = if (dbMsg.audioPath != null) MessageType.AUDIO else MessageType.TEXT,
                            audioFilePath = dbMsg.audioPath
                        )
                    }
                    _chatHistory.value = uiMessages
                    _isLoading.value = false
                }
        }
    }


    fun deleteMessage(messageId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // شما باید متد deleteMessageById را به MessageDao خود اضافه کنید
                // مثال در MessageDao:
                // @Query("DELETE FROM messages WHERE id = :id")
                // suspend fun deleteMessageById(id: Long)

                messageDao.deleteMessageById(messageId)
            } catch (e: Exception) {
                Log.e(TAG, "[deleteMessage] Failed to delete message $messageId", e)
            }
        }
    }

    fun sendMessage(userInput: String) {
        val userId = currentUserId ?: return
        val conversationId = currentConversationId ?: return

        if (userInput.isBlank() || _isLoading.value || _isRecording.value) return

        _isLoading.value = true
        val userChatMessage = ChatMessage(id = 0 , text = userInput, isFromUser = true)
        _chatHistory.update { it + userChatMessage }

        viewModelScope.launch {
            val userDbMessage = Message(userId = userId, conversationId = conversationId, sender = SENDER_USER, content = userInput, timestamp = System.currentTimeMillis())
            withContext(Dispatchers.IO) {
                try {
                    messageDao.insert(userDbMessage)
                } catch (e: Exception) {
                    Log.e(TAG, "[sendMessage] Failed to save user message to DB", e)
                }
            }

            try {
                val request = ChatRequest(query = userInput, userId = userId)
                val functionCallingResponse = apiService.functionCalling(request)

                if (functionCallingResponse.isSuccessful && functionCallingResponse.body() != null) {
                    val functionName = functionCallingResponse.body()!!.functionName
                    if (functionName != "none") {
                        // TODO: Implement the actual function execution
                        val functionResponse = "Function '${functionName}' was recognized and would be executed here."
                        val aiDbMessage = Message(
                            userId = userId,
                            conversationId = conversationId,
                            sender = SENDER_AI,
                            content = functionResponse,
                            timestamp = System.currentTimeMillis()
                        )
                        withContext(Dispatchers.IO) {
                            messageDao.insert(aiDbMessage)
                        }
                    } else {
                        val response = apiService.sendTextMessage(request)
                        if (response.isSuccessful && response.body() != null) {
                            val responseText = response.body()!!.answer
                            val aiDbMessage = Message(
                                userId = userId,
                                conversationId = conversationId,
                                sender = SENDER_AI,
                                content = responseText,
                                timestamp = System.currentTimeMillis()
                            )
                            withContext(Dispatchers.IO) {
                                messageDao.insert(aiDbMessage)
                            }
                        } else {
                            val errorMsg = "API Error: ${response.code()} - ${response.message()}"
                            handleError(errorMsg, null)
                        }
                    }
                } else {
                    val errorMsg = "API Error: ${functionCallingResponse.code()} - ${functionCallingResponse.message()}"
                    handleError(errorMsg, null)
                }
            } catch (e: Exception) {
                handleError("Network Error: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun startRecordingAudio() {
        if (_isLoading.value || _isRecording.value) return
        viewModelScope.launch {
            if (audioRecorder.startRecording() != null) {
                _isRecording.value = true
            } else {
                handleError("Error: Could not start recording.", null)
            }
        }
    }

    fun stopRecordingAudioAndSend(prompt: String) {
        val userId = currentUserId ?: return
        val conversationId = currentConversationId ?: return
        if (!_isRecording.value) return

        viewModelScope.launch {
            _isRecording.value = false
            _isLoading.value = true

            val tempAudioFilePath = audioRecorder.stopRecording()
            if (tempAudioFilePath == null) {
                handleError("Error: Failed to stop recording or get file path.", null)
                _isLoading.value = false
                return@launch
            }

            val tempAudioFile = File(tempAudioFilePath)
            if (!tempAudioFile.exists() || tempAudioFile.length() == 0L) {
                handleError("Error: Recorded audio file is missing or empty.", null)
                if (tempAudioFile.exists()) tempAudioFile.delete()
                _isLoading.value = false
                return@launch
            }

            val persistentAudioFilePath = copyAudioToInternalStorage(tempAudioFile)
            if (persistentAudioFilePath != null) {
                val voiceMessagePlaceholderText = "[Voice Message]"
                val audioDbMessage = Message(
                    userId = userId,
                    conversationId = conversationId,
                    sender = SENDER_USER,
                    content = voiceMessagePlaceholderText,
                    timestamp = System.currentTimeMillis(),
                    audioPath = persistentAudioFilePath
                )
                withContext(Dispatchers.IO) { messageDao.insert(audioDbMessage) }
            }

            try {
                val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val conversationIdBody = conversationId.toRequestBody("text/plain".toMediaTypeOrNull())

                val requestFile = tempAudioFile.asRequestBody("audio/m4a".toMediaTypeOrNull())
                val audioPart = MultipartBody.Part.createFormData("audio", tempAudioFile.name, requestFile)

                val response = apiService.sendAudioMessage(userIdBody, conversationIdBody, audioPart)

                if (response.isSuccessful && response.body() != null) {
                    val responseText = response.body()!!.answer
                    val aiDbMessage = Message(userId = userId, conversationId = conversationId, sender = SENDER_AI, content = responseText, timestamp = System.currentTimeMillis())
                    withContext(Dispatchers.IO) {
                        messageDao.insert(aiDbMessage)
                    }
                } else {
                    val errorMsg = "API Error (Audio): ${response.code()} - ${response.message()}"
                    handleError(errorMsg, null)
                }

            } catch (e: Exception) {
                handleError("Network Error (Audio): ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
                try {
                    if (tempAudioFile.exists()) tempAudioFile.delete()
                } catch (e: Exception) {
                    Log.e(TAG, "[stopRecording] Error deleting temp file", e)
                }
            }
        }
    }

    private suspend fun copyAudioToInternalStorage(sourceFile: File): String? {
        return withContext(Dispatchers.IO) {
            try {
                val internalFilesDir = applicationContext.filesDir
                if (!internalFilesDir.exists()) internalFilesDir.mkdirs()
                val destinationFileName = "audio_${UUID.randomUUID()}.m4a"
                val destinationFile = File(internalFilesDir, destinationFileName)
                sourceFile.copyTo(destinationFile, overwrite = true)
                destinationFile.absolutePath
            } catch (e: IOException) {
                Log.e(TAG, "[copyAudio] Failed to copy audio file", e)
                null
            }
        }
    }

    private fun handleError(message: String, exception: Exception?) {
        Log.e(TAG, "handleError called: $message", exception)
        val errorChatMessage = ChatMessage(
            id = System.currentTimeMillis(),
            text = "Error: ${message.substringBefore('\n').substringBefore(':')}",
            isFromUser = false,
            isError = true,
            messageType = MessageType.TEXT
        )
        _chatHistory.update { it + errorChatMessage }
        _isLoading.value = false
        _isRecording.value = false
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.releaseRecorder()
        historyLoadingJob?.cancel()
    }
}