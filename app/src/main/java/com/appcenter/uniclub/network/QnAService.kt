package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.*
import retrofit2.http.*

interface QnAService {
    //질문 등록
    @POST("/api/v1/qna")
    suspend fun createQuestion(
        @Query("clubId") clubId: Long,
        @Body body: QuestionCreateRequestDto
    ): QuestionCreateResponseDto

    //답변,대댓글 등록
    @POST("/api/v1/qna/{questionId}/answers")
    suspend fun createAnswer(
        @Path("questionId") questionId: Long,
        @Query("parentsAnswerId") parentsAnswerId: Long?,
        @Body body: AnswerCreateRequestDto
    ): AnswerCreateResponseDto

    //특정 질문 조회
    @GET("/api/v1/qna/{questionId}")
    suspend fun getQuestion(
        @Path("questionId") questionId: Long
    ): QuestionResponseDto

    //질문 삭제
    @DELETE("/api/v1/qna/{questionId}")
    suspend fun deleteQuestion(
        @Path("questionId") questionId: Long
    ): Unit

    //질문 수정
    @PATCH("/api/v1/qna/{questionId}")
    suspend fun updateQuestion(
        @Path("questionId") questionId: Long,
        @Body body: QuestionUpdateRequestDto
    ): Unit

    //답변 완료 표시 (회장)
    @PATCH("/api/v1/qna/{questionId}/answered")
    suspend fun markAnswered(
        @Path("questionId") questionId: Long
    ): Unit

    //질문 검색 (페이징)
    @GET("/api/v1/qna/search")
    suspend fun searchQuestions(
        @Query("keyword") keyword: String? = null,
        @Query("clubId") clubId: Long? = null,
        @Query("answered") answered: Boolean? = null,
        @Query("onlyMyQuestions") onlyMyQuestions: Boolean? = null,
        @Query("size") size: Int? = null
    ): PageQuestionResponseDto

    //QnA용 동아리 검색
    @GET("/api/v1/qna/search-clubs")
    suspend fun searchClubsForQna(
        @Query("keyword") keyword: String? = null
    ): List<QnaClubResponseDto>

    //답변 삭제
    @DELETE("/api/v1/qna/answers/{answerId}")
    suspend fun deleteAnswer(
        @Path("answerId") answerId: Long
    ): Unit

    //질문/답변 신고
    @POST("/api/v1/qna/reports")
    suspend fun createReport(
        @Body body: ReportCreateRequestDto
    ): Unit
}