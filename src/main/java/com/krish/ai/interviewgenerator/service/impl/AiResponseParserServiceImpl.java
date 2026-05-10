package com.krish.ai.interviewgenerator.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krish.ai.interviewgenerator.dto.ai.AiGeneratedQuestion;
import com.krish.ai.interviewgenerator.dto.ai.AiQuestionListResponse;
import com.krish.ai.interviewgenerator.service.AiResponseParserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiResponseParserServiceImpl implements AiResponseParserService {

    private final ObjectMapper objectMapper;

    public AiResponseParserServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> parseQuestions(String aiRawResponse, int expectedCount) {
        if (aiRawResponse == null || aiRawResponse.isBlank()) {
            throw new IllegalStateException("AI returned an empty response");
        }

        String normalized = removeMarkdownCodeFence(aiRawResponse.trim());

        try {
            AiQuestionListResponse parsed = objectMapper.readValue(normalized, AiQuestionListResponse.class);
            if (parsed.questions() == null || parsed.questions().isEmpty()) {
                throw new IllegalStateException("AI returned empty questions list");
            }

            return parsed.questions().stream()
                    .map(AiGeneratedQuestion::question)
                    .filter(q -> q != null && !q.isBlank())
                    .map(String::trim)
                    .limit(expectedCount)
                    .toList();
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse AI response into structured JSON", ex);
        }
    }

    private String removeMarkdownCodeFence(String value) {
        String sanitized = value;
        if (sanitized.startsWith("```json")) {
            sanitized = sanitized.substring(7);
        } else if (sanitized.startsWith("```")) {
            sanitized = sanitized.substring(3);
        }
        if (sanitized.endsWith("```")) {
            sanitized = sanitized.substring(0, sanitized.length() - 3);
        }
        return sanitized.trim();
    }
}
