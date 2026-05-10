package com.krish.ai.interviewgenerator.controller;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.ApiResponse;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> generate(
            @Valid @RequestBody QuestionGenerationRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        questionService.generateQuestions(request)
                )
        );
    }
}
