package com.krish.ai.interviewgenerator.service.impl;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import com.krish.ai.interviewgenerator.dto.request.QuestionGenerationRequest;
import com.krish.ai.interviewgenerator.dto.response.QuestionPageResponse;
import com.krish.ai.interviewgenerator.dto.response.QuestionResponse;
import com.krish.ai.interviewgenerator.entity.QuestionEntity;
import com.krish.ai.interviewgenerator.repository.QuestionRepository;
import com.krish.ai.interviewgenerator.service.QuestionGenerationService;
import com.krish.ai.interviewgenerator.service.QuestionService;
import com.krish.ai.interviewgenerator.util.Difficulty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionGenerationService questionGenerationService;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public List<QuestionResponse> generateQuestions(
            QuestionGenerationRequest request) {
        List<String> generatedQuestions = questionGenerationService.generate(request);

        List<QuestionEntity> entities = generatedQuestions.stream()
                .map(question -> QuestionEntity.builder()
                        .question(question)
                        .topic(request.getTopic())
                        .difficulty(request.getDifficulty())
                        .type(AppConstants.QuestionDefaults.TYPE_GENERATED)
                        .rating(AppConstants.QuestionDefaults.RATING_DEFAULT)
                        .build())
                .toList();

        List<QuestionResponse> responses = questionRepository.saveAllAndFlush(entities)
                .stream()
                .map(this::toResponse)
                .toList();

        log.info("QuestionService persisted and mapped generated questions: topic='{}', questionCount={}",
                request.getTopic(), responses.size());
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionPageResponse getQuestions(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String topic,
            Difficulty difficulty,
            Integer minRating) {

        Sort.Direction direction = AppConstants.ApiDefaults.SORT_DIR_DESC.equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, normalizeSortBy(sortBy)));

        Specification<QuestionEntity> specification = getQuestionEntitySpecification(topic, difficulty, minRating);

        Page<QuestionEntity> resultPage = questionRepository.findAll(specification, pageable);

        List<QuestionResponse> content = resultPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return QuestionPageResponse.builder()
                .content(content)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .build();
    }

    private static @NonNull Specification<QuestionEntity> getQuestionEntitySpecification(String topic, Difficulty difficulty, Integer minRating) {
        Specification<QuestionEntity> specification = (root, query, cb) -> cb.conjunction();

        if (topic != null && !topic.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get(AppConstants.EntityFields.TOPIC)), "%" + topic.toLowerCase() + "%"));
        }

        if (difficulty != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get(AppConstants.EntityFields.DIFFICULTY), difficulty));
        }

        if (minRating != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get(AppConstants.EntityFields.RATING), minRating));
        }
        return specification;
    }

    private String normalizeSortBy(String sortBy) {
        if (AppConstants.ApiDefaults.SORT_FIELD_RATING.equalsIgnoreCase(sortBy)) {
            return AppConstants.ApiDefaults.SORT_FIELD_RATING;
        }
        return AppConstants.ApiDefaults.SORT_FIELD_CREATED_AT;
    }

    private QuestionResponse toResponse(QuestionEntity entity) {
        return new QuestionResponse(
                entity.getId(),
                entity.getQuestion(),
                entity.getDifficulty(),
                entity.getTopic(),
                entity.getType(),
                entity.getCreatedAt(),
                entity.getRating()
        );
    }
}
