package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.service.QuestionGenerationService;
import com.krish.ai.interviewgenerator.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionGenerationService questionGenerationService;

    @Override
    public List<QuestionResponse> generateQuestions(
            QuestionGenerationRequest request) {
        List<QuestionResponse> responses = questionGenerationService.generate(request)
                .stream()
                .map(question -> new QuestionResponse(
                        question,
                        request.getDifficulty(),
                        request.getTopic()
                ))
                .toList();

        log.info("QuestionService mapped generated questions: topic='{}', questionCount={}",
                request.getTopic(), responses.size());
        return responses;
    }
}
