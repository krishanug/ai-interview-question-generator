package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import com.krish.ai.interviewgenerator.service.QuestionValidationService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class QuestionValidationServiceImpl implements QuestionValidationService {

    @Override
    public void validate(List<String> questions, String topic, int expectedCount) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("AI returned no questions");
        }

        if (questions.size() < expectedCount) {
            throw new IllegalStateException("AI returned fewer questions than requested");
        }

        ensureNoDuplicates(questions);
        ensureValidFormat(questions);
        ensureTopicRelevance(questions, topic);
    }

    private void ensureNoDuplicates(List<String> questions) {
        Set<String> unique = new HashSet<>();
        for (String question : questions) {
            String normalized = question.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
            if (!unique.add(normalized)) {
                throw new IllegalStateException("AI returned duplicate questions");
            }
        }
    }

    private void ensureValidFormat(List<String> questions) {
        for (String question : questions) {
            if (question == null || question.isBlank()) {
                throw new IllegalStateException("AI returned invalid question format");
            }

            String normalizedQuestion = question.trim();
            int length = normalizedQuestion.length();

            if (length < AppConstants.Validation.QUESTION_MIN_LENGTH
                    || length > AppConstants.Validation.QUESTION_MAX_LENGTH) {
                throw new IllegalStateException("AI returned question with invalid length");
            }
        }
    }

    private void ensureTopicRelevance(List<String> questions, String topic) {
        String normalizedTopic = topic == null ? "" : topic.toLowerCase(Locale.ROOT).trim();
        if (normalizedTopic.isBlank()) {
            return;
        }

        String[] keywords = normalizedTopic.split("\\s+");
        long relevantCount = questions.stream()
                .filter(question -> containsAnyKeyword(question, keywords))
                .count();

        if (relevantCount == 0) {
            throw new IllegalStateException("AI returned irrelevant questions for the requested topic");
        }
    }

    private boolean containsAnyKeyword(String question, String[] keywords) {
        String normalizedQuestion = question.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (keyword.length() >= 3 && normalizedQuestion.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
