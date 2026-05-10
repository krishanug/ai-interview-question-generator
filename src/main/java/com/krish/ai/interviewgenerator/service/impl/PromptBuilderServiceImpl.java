package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.service.PromptBuilderService;
import com.krish.ai.interviewgenerator.util.Difficulty;
import org.springframework.stereotype.Service;

@Service
public class PromptBuilderServiceImpl implements PromptBuilderService {

    @Override
    public String buildQuestionGenerationPrompt(QuestionGenerationRequest request) {
        String difficultyGuideline = difficultyGuideline(request.getDifficulty());

        return """
                You are a senior backend interviewer.
                Generate exactly %d interview questions for topic: %s.
                Difficulty: %s.

                Rules:
                - Keep a backend engineering focus.
                - Prefer practical and scenario-based interview questions.
                - Avoid purely theoretical questions.
                - Avoid duplicate or near-duplicate questions.
                - Keep each question clear and concise.

                Difficulty behavior:
                %s

                Output format:
                - Return strictly valid JSON only.
                - Do not wrap response in markdown code fences.
                - JSON shape:
                  {
                    "questions": [
                      { "question": "..." }
                    ]
                  }
                - Exactly %d items in questions array.
                """.formatted(
                request.getQuestionCount(),
                request.getTopic(),
                request.getDifficulty().name(),
                difficultyGuideline,
                request.getQuestionCount()
        );
    }

    private String difficultyGuideline(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> "Ask foundational concept checks and simple practical usage questions.";
            case MEDIUM -> "Ask applied development and debugging scenarios from real projects.";
            case HARD -> "Ask architecture trade-off, scale, failure-handling, and production incident scenarios.";
        };
    }
}
