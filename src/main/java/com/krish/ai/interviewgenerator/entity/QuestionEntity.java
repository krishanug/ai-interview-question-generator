package com.krish.ai.interviewgenerator.entity;

import com.krish.ai.interviewgenerator.util.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "questions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_questions_topic_difficulty_question",
                        columnNames = {"topic", "difficulty", "question"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(length = 100)
    private String type;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Integer rating;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer ratingCount;

    @PrePersist
    void initializeDefaults() {
        if (ratingCount == null) {
            ratingCount = 0;
        }
    }
}
