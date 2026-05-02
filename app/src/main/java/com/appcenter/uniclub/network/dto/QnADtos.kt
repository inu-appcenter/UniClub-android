package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//페이지 공통 래퍼
@Keep
data class PageResponse<T>(
    @field:SerializedName("content") val content: List<T>,
    @field:SerializedName("hasNext") val hasNext: Boolean
)

//질문 생성
@Keep
data class QuestionCreateRequestDto(
    @field:SerializedName("content") val content: String,
    @field:SerializedName("anonymous") val anonymous: Boolean
)

@Keep
data class QuestionCreateResponseDto(
    @field:SerializedName("questionId") val questionId: Long
)

//답변 생성
@Keep
data class AnswerCreateRequestDto(
    @field:SerializedName("content") val content: String,
    @field:SerializedName("anonymous") val anonymous: Boolean
)

@Keep
data class AnswerCreateResponseDto(
    @field:SerializedName("answerId") val answerId: Long
)

//질문 수정
@Keep
data class QuestionUpdateRequestDto(
    @field:SerializedName("content") val content: String
)

//답변 조회
@Keep
data class AnswerResponseDto(
    @field:SerializedName("answerId") val answerId: Long,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("content") val content: String,
    @field:SerializedName("anonymous") val anonymous: Boolean,
    @field:SerializedName("deleted") val deleted: Boolean,
    @field:SerializedName("updateTime") val updateTime: String,
    @field:SerializedName("parentAnswerId") val parentAnswerId: Long?, //대댓글이 아닐 경우 null
    @field:SerializedName("owner") val owner: Boolean,
    @field:SerializedName("president") val president: Boolean, //동아리 회장 여부
    @field:SerializedName("profile") val profile: String? = null
)

//질문 조회
@Keep
data class QuestionResponseDto(
    @field:SerializedName("questionId") val questionId: Long,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("content") val content: String,
    @field:SerializedName("anonymous") val anonymous: Boolean,
    @field:SerializedName("answered") val answered: Boolean,
    @field:SerializedName("updatedAt") val updatedAt: String,
    @field:SerializedName("owner") val owner: Boolean, //본인 질문 여부
    @field:SerializedName("profile") val profile: String?,
    @field:SerializedName("president") val president: Boolean,
    @field:SerializedName("answers") val answers: List<AnswerResponseDto>
)

@Keep
data class SearchQuestionResponseDto(
    @field:SerializedName("questionId") val questionId: Long,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("clubId") val clubId: Long,
    @field:SerializedName("content") val content: String,
    @field:SerializedName("owner") val owner: Boolean,
    @field:SerializedName("countAnswer") val countAnswer: Long,
    @field:SerializedName("updatedAt") val updatedAt: String,
    @field:SerializedName("profile") val profile: String?
)

//페이지형 질문 목록
typealias PageQuestionResponseDto = PageResponse<SearchQuestionResponseDto>

//QnA용 동아리 검색 응답
@Keep
data class QnaClubResponseDto(
    @field:SerializedName("clubId") val clubId: Long,
    @field:SerializedName("clubName") val clubName: String,
    @field:SerializedName("categoryType") val categoryType: String
)

//신고 생성
@Keep
data class ReportCreateRequestDto(
    @field:SerializedName("targetType") val targetType: String,
    @field:SerializedName("targetId") val targetId: Long,
    @field:SerializedName("reason") val reason: String? = null
)

object ReportTargetType {
    const val QUESTION = "QUESTION"
    const val ANSWER = "ANSWER"
}