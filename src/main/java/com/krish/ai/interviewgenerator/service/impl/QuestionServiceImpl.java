package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Override
    public List<QuestionResponse> generateQuestions(
            QuestionGenerationRequest request) {

        return List.of(
                new QuestionResponse(
                        "Explain JVM Memory Model",
                        request.getDifficulty(),
                        request.getTopic()
                )
        );
    }
}
