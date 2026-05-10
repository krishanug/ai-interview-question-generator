package com.krish.ai.interviewgenerator.controller;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.ApiResponse;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> generate(
            @Valid @RequestBody QuestionGenerationRequest request) {
        log.info("Question generation request received: topic='{}', difficulty={}, questionCount={}",
                request.getTopic(), request.getDifficulty(), request.getQuestionCount());

        List<QuestionResponse> generatedQuestions = questionService.generateQuestions(request);
        log.info("Question generation request completed successfully: generatedCount={}",
                generatedQuestions.size());

        return ResponseEntity.ok(
                ApiResponse.success(
                        generatedQuestions
                )
        );
    }
}
