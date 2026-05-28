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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        String content = callModel(chatClientBuilder, prompt);

        if (content == null || content.isBlank()) {
            throw new IllegalStateException("AI returned an empty response");
        }

        List<String> questions = new ArrayList<>(
                aiResponseParserService.parseQuestions(content, request.getQuestionCount())
        );
        questions = improveWeakQuestionsOnce(request, chatClientBuilder, questions);
        questionValidationService.validate(questions, request.getTopic(), request.getQuestionCount());

        log.info("Gemini response processed successfully: questionCount={}", questions.size());
        return questions;
    }

    private List<String> improveWeakQuestionsOnce(
            QuestionGenerationRequest request,
            ChatClient.Builder chatClientBuilder,
            List<String> questions) {
        List<Integer> weakIndexes = findWeakQuestionIndexes(questions);
        if (weakIndexes.isEmpty()) {
            return questions;
        }

        List<String> weakQuestions = weakIndexes.stream().map(questions::get).toList();
        String regenerationPrompt = promptBuilderService.buildRegenerationPrompt(
                request,
                weakQuestions,
                weakQuestions.size()
        );

        String regenerationContent = callModel(chatClientBuilder, regenerationPrompt);
        if (regenerationContent == null || regenerationContent.isBlank()) {
            return questions;
        }

        List<String> regenerated = aiResponseParserService.parseQuestions(regenerationContent, weakQuestions.size());
        if (regenerated.size() < weakIndexes.size()) {
            return questions;
        }

        for (int i = 0; i < weakIndexes.size(); i++) {
            questions.set(weakIndexes.get(i), regenerated.get(i));
        }

        log.info("Regenerated weak questions once: weakCount={}", weakIndexes.size());
        return questions;
    }

    private List<Integer> findWeakQuestionIndexes(List<String> questions) {
        return java.util.stream.IntStream.range(0, questions.size())
                .filter(index -> isWeakQuestion(questions.get(index)))
                .boxed()
                .toList();
    }

    private boolean isWeakQuestion(String question) {
        if (question == null || question.isBlank()) {
            return true;
        }

        String normalized = question.trim();
        int wordCount = normalized.split("\\s+").length;
        if (wordCount < AppConstants.Quality.MIN_WORD_COUNT) {
            return true;
        }

        String lower = normalized.toLowerCase(Locale.ROOT);
        return lower.startsWith("what is ")
                || lower.startsWith("define ")
                || lower.startsWith("explain ");
    }

    private String callModel(ChatClient.Builder chatClientBuilder, String prompt) {
        return chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
}
