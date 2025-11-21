package com.appcenter.uniclub.network.dto

//페이지 공통 래퍼
data class PageResponse<T>(
    val content: List<T>,
    val hasNext: Boolean
)

//질문 생성
data class QuestionCreateRequestDto(
    val content: String,
    val anonymous: Boolean
)
data class QuestionCreateResponseDto(
    val questionId: Long
)

//답변 생성
data class AnswerCreateRequestDto(
    val content: String,
    val anonymous: Boolean
)
data class AnswerCreateResponseDto(
    val answerId: Long
)

//질문 수정
data class QuestionUpdateRequestDto(
    val content: String,
    val anonymous: Boolean
)

//답변 조회
data class AnswerResponseDto(
    val answerId: Long,
    val nickname: String,
    val content: String,
    val anonymous: Boolean,
    val deleted: Boolean,
    val updateTime: String,
    val parentAnswerId: Long?, //대댓글이 아닐 경우 null
    val owner: Boolean,
    val president: Boolean, //동아리 회장 여부
    val profile: String? = null
)

//질문 조회
data class QuestionResponseDto(
    val questionId: Long,
    val nickname: String,
    val clubName: String,
    val content: String,
    val anonymous: Boolean,
    val answered: Boolean,
    val updatedAt: String,
    val owner: Boolean, //본인 질문 여부
    val profile: String?,
    val president: Boolean,
    val answers: List<AnswerResponseDto>
)

data class SearchQuestionResponseDto(
    val questionId: Long,
    val nickname: String,
    val clubName: String,
    val clubId: Long,
    val content: String,
    val owner: Boolean,
    val countAnswer: Long,
    val updatedAt: String,
    val profile: String?
)

//페이지형 질문 목록
typealias PageQuestionResponseDto = PageResponse<SearchQuestionResponseDto>

//QnA용 동아리 검색 응답
data class QnaClubResponseDto(
    val clubId: Long,
    val clubName: String,
    val categoryType: String
)