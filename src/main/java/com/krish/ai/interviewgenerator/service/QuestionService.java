package com.krish.ai.interviewgenerator.service;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {
    List<QuestionResponse> generateQuestions(QuestionGenerationRequest request);
}
