package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.service.PromptBuilderService;
import com.krish.ai.interviewgenerator.service.QuestionGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionGenerationServiceImpl implements QuestionGenerationService {

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final PromptBuilderService promptBuilderService;

    public QuestionGenerationServiceImpl(
            ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
            PromptBuilderService promptBuilderService) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.promptBuilderService = promptBuilderService;
    }

    @Override
    public List<String> generate(QuestionGenerationRequest request) {
        log.info("Calling Gemini model for question generation: topic='{}', difficulty={}, questionCount={}",
                request.getTopic(), request.getDifficulty(), request.getQuestionCount());

        ChatClient.Builder chatClientBuilder = chatClientBuilderProvider.getIfAvailable();
        if (chatClientBuilder == null) {
            throw new IllegalStateException("AI client is not configured");
        }

        String prompt = promptBuilderService.buildQuestionGenerationPrompt(request);
        log.info("Final AI prompt: {}", prompt);

        String content = chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();

        if (content == null || content.isBlank()) {
            throw new IllegalStateException("AI returned an empty response");
        }

        List<String> questions = Arrays.stream(content.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(this::stripNumbering)
                .limit(request.getQuestionCount())
                .collect(Collectors.toList());

        log.info("Gemini response processed successfully: questionCount={}", questions.size());
        return questions;
    }

    private String stripNumbering(String line) {
        return line.replaceFirst("^\\d+[.)]\\s*", "")
                .replaceFirst("^[-*]\\s*", "")
                .trim();
    }
}
