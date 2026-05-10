package com.krish.ai.interviewgenerator.service;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;

public interface PromptBuilderService {
    String buildQuestionGenerationPrompt(QuestionGenerationRequest request);
}
