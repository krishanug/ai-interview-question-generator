package com.krish.ai.interviewgenerator.service;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;

import java.util.List;

public interface QuestionGenerationService {
    List<String> generate(QuestionGenerationRequest request);
}
