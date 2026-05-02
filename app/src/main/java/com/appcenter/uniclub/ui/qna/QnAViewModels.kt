package com.appcenter.uniclub.ui.qna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.QnARepository
import com.appcenter.uniclub.network.dto.QuestionResponseDto
import com.appcenter.uniclub.network.dto.ReportTargetType
import com.appcenter.uniclub.network.dto.SearchQuestionResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QnaListUiState(
    val loading: Boolean = false,
    val items: List<SearchQuestionResponseDto> = emptyList(),
    val hasNext: Boolean = false,
    val error: Throwable? = null
)

class QnaListViewModel(private val repo: QnARepository) : ViewModel() {
    private val _ui = MutableStateFlow(QnaListUiState(loading = true))
    val ui: StateFlow<QnaListUiState> = _ui

    //필터 상태
    var keyword: String? = null
    var clubId: Long? = null
    var answered: Boolean? = null
    var onlyMy: Boolean? = null
    var size: Int? = 30

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            repo.searchQuestions(keyword, clubId, answered, onlyMy, size)
                .onSuccess { page ->
                    _ui.update {
                        it.copy(
                            loading = false,
                            items = page.content,
                            hasNext = page.hasNext
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update { it.copy(loading = false, error = e) }
                }
        }
    }

    fun deleteQuestion(questionId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            repo.deleteQuestion(questionId)
                .onSuccess {
                    refresh()
                    onSuccess()
                }
                .onFailure { e ->
                    _ui.update { it.copy(error = e) }
                }
        }
    }

    fun reportQuestion(questionId: Long, reason: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }

            repo.createReport(
                targetType = ReportTargetType.QUESTION,
                targetId = questionId,
                reason = reason
            )
                .onSuccess {
                    _ui.update { it.copy(loading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    _ui.update { it.copy(loading = false, error = e) }
                }
        }
    }
}

class QnaListViewModelFactory(
    private val repo: QnARepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QnaListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QnaListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class QuestionDetailUiState(
    val loading: Boolean = false,
    val data: QuestionResponseDto? = null,
    val error: Throwable? = null
)

class QuestionDetailViewModel(private val repo: QnARepository) : ViewModel() {
    private val _ui = MutableStateFlow(QuestionDetailUiState(loading = true))
    val ui: StateFlow<QuestionDetailUiState> = _ui

    fun load(questionId: Long) {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            repo.getQuestion(questionId)
                .onSuccess { q -> _ui.update { it.copy(loading = false, data = q) }}
                .onFailure { e -> _ui.update { it.copy(loading = false, error = e) }}
        }
    }

    fun sendAnswer(
        questionId: Long,
        parentId: Long?,
        content: String,
        anonymous: Boolean
    ) {
        viewModelScope.launch {
            repo.createAnswer(questionId, parentId, content, anonymous)
                .onSuccess {
                    load(questionId)
                }
                .onFailure { e ->
                    _ui.update { it.copy(error = e) }
                }
        }
    }

    fun deleteAnswer(answerId: Long, questionId: Long) {
        val before = _ui.value.data
        setAnswerDeletedLocally(answerId)

        viewModelScope.launch {
            repo.deleteAnswer(answerId)
                .onSuccess {
                    load(questionId)
                }
                .onFailure { e ->
                    _ui.update { it.copy(data = before, error = e) }
                }
        }
    }

    fun markAnswered(questionId: Long) {
        val before = _ui.value.data
        toggleAnsweredLocally()

        viewModelScope.launch {
            repo.markAnswered(questionId)
                .onSuccess { load(questionId) }
                .onFailure { e ->
                    _ui.update { it.copy(data = before, error = e) }
                }
        }
    }

    //특정 answerId 를 로컬 상태에서 deleted=true, content="" 로 변경
    private fun setAnswerDeletedLocally(answerId: Long) {
        val current = _ui.value.data ?: return
        val updated = current.answers.map { ans ->
            if (ans.answerId == answerId) ans.copy(deleted = true, content = "")
            else ans
        }
        _ui.update { it.copy(data = current.copy(answers = updated)) }
    }

    //질문의 answered 플래그를 로컬에서 토글
    private fun toggleAnsweredLocally() {
        val current = _ui.value.data ?: return
        _ui.update { it.copy(data = current.copy(answered = !current.answered)) }
    }

    fun reportAnswer(answerId: Long, reason: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _ui.update { it.copy(error = null) }

            repo.createReport(
                targetType = ReportTargetType.ANSWER,
                targetId = answerId,
                reason = reason
            )
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    _ui.update { it.copy(error = e) }
                }
        }
    }
}

class QuestionDetailViewModelFactory(
    private val repo: QnARepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestionDetailViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class QuestionEditUiState(
    val submitting: Boolean = false,
    val success: Boolean = false,
    val error: Throwable? = null
)

class QuestionEditViewModel(private val repo: QnARepository) : ViewModel() {
    private val _ui = MutableStateFlow(QuestionEditUiState())
    val ui: StateFlow<QuestionEditUiState> = _ui

    fun create(clubId: Long, content: String, anonymous: Boolean) {
        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null, success = false) }
            repo.createQuestion(clubId, content, anonymous)
                .onSuccess { _ui.update { it.copy(submitting = false, success = true) } }
                .onFailure { e -> _ui.update { it.copy(submitting = false, error = e) } }
        }
    }

    fun update(questionId: Long, content: String) {
        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null, success = false) }
            repo.updateQuestion(questionId, content)
                .onSuccess { _ui.update { it.copy(submitting = false, success = true) } }
                .onFailure { e -> _ui.update { it.copy(submitting = false, success = true) } }
        }
    }
}

class QuestionEditViewModelFactory(
    private val repo: QnARepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestionEditViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}