package com.krish.ai.interviewgenerator.service;

import java.util.List;

public interface AiResponseParserService {
    List<String> parseQuestions(String aiRawResponse, int expectedCount);
}