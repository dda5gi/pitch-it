package com.alppano.speakon.domain.question.controller;

import com.alppano.speakon.common.dto.ApiResponse;
import com.alppano.speakon.domain.question.dto.QuestionInfo;
import com.alppano.speakon.domain.question.dto.QuestionRequest;
import com.alppano.speakon.domain.question.service.QuestionService;
import com.alppano.speakon.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "질문 관리")
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "질문 생성")
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<QuestionInfo>> createQuestion(@AuthenticationPrincipal LoginUser loginUser,
                                                                    @RequestBody QuestionRequest dto) {
        QuestionInfo questionInfo = questionService.createQuestion(dto, loginUser.getId());
        ApiResponse<QuestionInfo> result = new ApiResponse<>(true, "질문 생성 성공", questionInfo);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "질문 내용 수정")
    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<QuestionInfo>> createQuestion(@AuthenticationPrincipal LoginUser loginUser,
                                                                    @PathVariable("id") Long questionId,
                                                                    @RequestBody QuestionRequest dto) {
        QuestionInfo questionInfo = questionService.updateQuestion(dto, questionId, loginUser.getId());
        ApiResponse result = new ApiResponse<>(true, "질문 수정 성공", questionInfo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "질문 삭제")
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse> createQuestion(@AuthenticationPrincipal LoginUser loginUser,
                                                      @PathVariable("id") Long questionId) {
        questionService.deleteQuestion(questionId, loginUser.getId());
        ApiResponse result = new ApiResponse<>(true, "질문 삭제 성공");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "질문 목록 조회")
    @GetMapping("/interviewjoins/{id}/questions")
    public ResponseEntity<ApiResponse<List<QuestionInfo>>> getQuestionListByInterviewJoin(@AuthenticationPrincipal LoginUser loginUser,
                                                                                          @PathVariable("id") Long interviewJoinId) {
        List<QuestionInfo> list = questionService.getQuestionListByInterviewJoin(interviewJoinId, loginUser.getId());
        ApiResponse<List<QuestionInfo>> result = new ApiResponse<>(true, "질문 목록 조회", list);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}