package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.QnAService
import com.appcenter.uniclub.network.dto.*

class QnARepository(private val api: QnAService) {
    suspend fun createQuestion(
        clubId: Long, content: String, anonymous: Boolean
    ): Result<QuestionCreateResponseDto> =
        runCatching {
            api.createQuestion(
                clubId = clubId,
                body = QuestionCreateRequestDto(content, anonymous)
            )
        }

    suspend fun createAnswer(
        questionId: Long, parentId: Long?, content: String, anonymous: Boolean
    ): Result<AnswerCreateResponseDto> =
        runCatching {
            api.createAnswer(
                questionId = questionId,
                parentsAnswerId = parentId,
                body = AnswerCreateRequestDto(content, anonymous)
            )
        }

    suspend fun getQuestion(questionId: Long) =
        runCatching { api.getQuestion(questionId) }

    suspend fun deleteQuestion(questionId: Long) =
        runCatching { api.deleteQuestion(questionId) }

    suspend fun updateQuestion(
        questionId: Long, content: String
    ): Result<Unit> =
        runCatching {
            api.updateQuestion(
                questionId = questionId,
                body = QuestionUpdateRequestDto(content)
            )
        }

    suspend fun markAnswered(questionId: Long) =
        runCatching { api.markAnswered(questionId) }

    suspend fun searchQuestions(
        keyword: String? = null,
        clubId: Long? = null,
        answered: Boolean? = null,
        onlyMy: Boolean? = null,
        size: Int? = null
    ): Result<PageQuestionResponseDto> =
        runCatching { api.searchQuestions(keyword, clubId, answered, onlyMy, size) }

    suspend fun searchClubsForQna(keyword: String?): Result<List<QnaClubResponseDto>> =
        runCatching { api.searchClubsForQna(keyword) }

    suspend fun deleteAnswer(answerId: Long): Result<Unit> =
        runCatching { api.deleteAnswer(answerId) }

    suspend fun createReport(
        targetType: String,
        targetId: Long,
        reason: String?
    ): Result<Unit> =
        runCatching {
            api.createReport(
                ReportCreateRequestDto(
                    targetType = targetType,
                    targetId = targetId,
                    reason = reason?.takeIf { it.isNotBlank() }
                )
            )
        }
}