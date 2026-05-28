package com.krish.ai.interviewgenerator.controller;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.ApiResponse;
import com.krish.ai.interviewgenerator.dto.response.QuestionPageResponse;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.service.QuestionService;
import com.krish.ai.interviewgenerator.util.Difficulty;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppConstants.ApiPath.QUESTIONS_BASE)
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(AppConstants.ApiPath.GENERATE)
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> generate(
            @Valid @RequestBody QuestionGenerationRequest request) {
        log.info("Question generation request received: topic='{}', difficulty={}, questionCount={}",
                request.getTopic(), request.getDifficulty(), request.getQuestionCount());

        List<QuestionResponse> generatedQuestions = questionService.generateQuestions(request);
        log.info("Question generation request completed successfully: generatedCount={}",
                generatedQuestions.size());

        return ResponseEntity.ok(
                ApiResponse.success(
                        AppConstants.Messages.QUESTIONS_GENERATED,
                        generatedQuestions
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<QuestionPageResponse>> getQuestions(
            @RequestParam(defaultValue = AppConstants.ApiDefaults.PAGE) int page,
            @RequestParam(defaultValue = AppConstants.ApiDefaults.SIZE) int size,
            @RequestParam(defaultValue = AppConstants.ApiDefaults.SORT_BY) String sortBy,
            @RequestParam(defaultValue = AppConstants.ApiDefaults.SORT_DIR) String sortDir,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) Integer minRating) {

        QuestionPageResponse response = questionService.getQuestions(
                page, size, sortBy, sortDir, topic, difficulty, minRating);

        return ResponseEntity.ok(ApiResponse.success(AppConstants.Messages.QUESTIONS_FETCHED, response));
    }
}
