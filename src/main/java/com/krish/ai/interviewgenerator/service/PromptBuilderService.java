package com.krish.ai.interviewgenerator.service;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;

import java.util.List;

public interface PromptBuilderService {
    String buildQuestionGenerationPrompt(QuestionGenerationRequest request);

    String buildRegenerationPrompt(
            QuestionGenerationRequest request,
            List<String> weakQuestions,
            int requiredCount
    );
}
