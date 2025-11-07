package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.QnAService
import com.appcenter.uniclub.network.dto.*

class QnARepository(private val api: QnAService) {
    suspend fun createQuestion(clubId: Long, content: String, anonymous: Boolean) =
        api.createQuestion(
            clubId = clubId,
            body = QuestionCreateRequestDto(content, anonymous)
        )

    suspend fun createAnswer(questionId: Long, parentId: Long, content: String, anonymous: Boolean) =
        api.createAnswer(
            questionId = questionId,
            parentsAnswerId = parentId,
            body = AnswerCreateRequestDto(content, anonymous)
        )

    suspend fun getQuestion(questionId: Long) =
        api.getQuestion(questionId)

    suspend fun deleteQuestion(questionId: Long) =
        api.deleteQuestion(questionId)

    suspend fun updateQuestion(questionId: Long, content: String, anonymous: Boolean, answered: Boolean) =
        api.updateQuestion(
            questionId = questionId,
            body = QuestionUpdateRequestDto(content, anonymous, answered)
        )

    suspend fun markAnswered(questionId: Long) =
        api.markAnswered(questionId)

    suspend fun searchQuestions(
        keyword: String? = null,
        clubId: Long? = null,
        answered: Boolean? = null,
        onlyMy: Boolean? = null,
        size: Int? = null
    ) = api.searchQuestions(keyword, clubId, answered, onlyMy, size)

    suspend fun searchClubsForQna(keyword: String) =
        api.searchClubsForQna(keyword)

    suspend fun deleteAnswer(answerId: Long) =
        api.deleteAnswer(answerId)
}