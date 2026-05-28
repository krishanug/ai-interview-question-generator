package com.krish.ai.interviewgenerator.repository;

import com.krish.ai.interviewgenerator.entity.QuestionEntity;
import com.krish.ai.interviewgenerator.util.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {

    @Query("""
            select q.question
            from QuestionEntity q
            where q.topic = :topic
              and q.difficulty = :difficulty
              and q.question in :questions
            """)
    List<String> findExistingQuestions(
            @Param("topic") String topic,
            @Param("difficulty") Difficulty difficulty,
            @Param("questions") List<String> questions
    );

    List<QuestionEntity> findByTopicAndDifficultyAndQuestionIn(
            String topic,
            Difficulty difficulty,
            List<String> questions
    );
}
