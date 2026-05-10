package com.krish.ai.interviewgenerator.service;

import java.util.List;

public interface QuestionValidationService {
    void validate(List<String> questions, String topic, int expectedCount);
}
