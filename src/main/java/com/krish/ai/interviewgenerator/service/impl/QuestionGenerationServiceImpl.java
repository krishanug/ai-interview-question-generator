package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.service.AiResponseParserService;
import com.krish.ai.interviewgenerator.service.PromptBuilderService;
import com.krish.ai.interviewgenerator.service.QuestionGenerationService;
import com.krish.ai.interviewgenerator.service.QuestionValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuestionGenerationServiceImpl implements QuestionGenerationService {

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final PromptBuilderService promptBuilderService;
    private final AiResponseParserService aiResponseParserService;
    private final QuestionValidationService questionValidationService;

    public QuestionGenerationServiceImpl(
            ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
            PromptBuilderService promptBuilderService,
            AiResponseParserService aiResponseParserService,
            QuestionValidationService questionValidationService) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.promptBuilderService = promptBuilderService;
        this.aiResponseParserService = aiResponseParserService;
        this.questionValidationService = questionValidationService;
    }

    @Override
    @Cacheable(cacheNames = AppConstants.Cache.QUESTIONS_CACHE, key = AppConstants.Cache.GENERATE_KEY_EXPRESSION)
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

        List<String> questions = aiResponseParserService.parseQuestions(content, request.getQuestionCount());
        questionValidationService.validate(questions, request.getTopic(), request.getQuestionCount());

        log.info("Gemini response processed successfully: questionCount={}", questions.size());
        return questions;
    }
}
