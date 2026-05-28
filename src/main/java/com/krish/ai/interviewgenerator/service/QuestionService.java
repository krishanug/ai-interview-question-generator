package com.krish.ai.interviewgenerator.service;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.QuestionPageResponse;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.util.Difficulty;

import java.util.List;

public interface QuestionService {
    List<QuestionResponse> generateQuestions(QuestionGenerationRequest request);

    QuestionPageResponse getQuestions(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String topic,
            Difficulty difficulty,
            Integer minRating
    );
}
